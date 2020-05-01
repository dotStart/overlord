/*
 * Copyright (C) 2020 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tv.dotstart.overlord.plugin.minecraft.instance

import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.ReplayProcessor
import tv.dotstart.overlord.plugin.minecraft.config.MinecraftPluginConfiguration
import tv.dotstart.overlord.shared.delegate.log
import tv.dotstart.overlord.shared.plugin.server.extensions.Announcing
import tv.dotstart.overlord.shared.plugin.server.extensions.CommandReceiver
import tv.dotstart.overlord.shared.plugin.server.instance.ServerFactoryContext
import tv.dotstart.overlord.shared.plugin.server.instance.ServerInstance
import tv.dotstart.overlord.shared.util.callingThread
import tv.dotstart.overlord.shared.util.javaBinary
import tv.dotstart.overlord.shared.util.lineFlux
import tv.dotstart.overlord.shared.util.supervise
import java.io.IOException
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.thread
import kotlin.concurrent.write

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
class MinecraftServerInstance(
    private val ctx: ServerFactoryContext,
    private val jarPath: Path,
    private val config: MinecraftPluginConfiguration) : ServerInstance, Announcing,
                                                        CommandReceiver {

  private val aliveProcessor = ReplayProcessor.create<Boolean>(1)
  private val stdoutProcessor = EmitterProcessor.create<String>()

  private val aliveSink = this.aliveProcessor.sink()
  private val stdoutSink = this.stdoutProcessor.sink()

  private val lock = ReentrantReadWriteLock()
  private val shutdownFlag = AtomicBoolean()
  private var process: Process? = null
  private var outputWriter: Writer? = null

  override val alive: Flux<Boolean>
    get() = this.aliveProcessor
  override val stdout: Flux<String>
    get() = this.stdoutProcessor

  companion object {
    private val logger by log()
  }

  override fun reconfigure(configuration: Map<String, Any?>) {
    val now = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        .format(OffsetDateTime.now())

    val eulaText = buildString {
      append("# Accepted via Overlord flags at ")
      append(now)
      append("\r\n")

      append("eula=true\r\n")
    }
    Files.writeString(this.ctx.dataLocation.resolve("eula.txt"), eulaText)
    logger.debug("Written EULA accept flag to eula.txt")

    if (this.config.serverProperties.isNotEmpty()) {
      val correctedProperties = this.config.serverProperties
          .map { (key, value) ->
            val actualValue = value
                ?.toString()
                ?: ""

            key to actualValue
          }
          .toMap()

      val properties = Properties()
      properties.putAll(correctedProperties)

      Files.newBufferedWriter(this.ctx.dataLocation.resolve("server.properties"),
                              StandardOpenOption.TRUNCATE_EXISTING).use {
        properties.store(it, "Generated via Overlord at $now")
      }

      logger.debug("Written server.properties:")
      correctedProperties.forEach { (key, value) ->
        logger.debug("  $key => \"$value\"")
      }
    }
  }

  override fun start() {
    this.lock.write {
      require(this.process == null) { "Server is already alive" }

      Files.createDirectories(this.ctx.dataLocation)

      this.reconfigure(this.config.serverProperties)
      this.superviseProcess()
    }
  }

  private fun superviseProcess() {
    val command = mutableListOf<String>()
    command += javaBinary.toAbsolutePath().toString()

    this.config.jvmParameters.split(' ')
        .filter { it.isNotBlank() }
        .forEach { command.add(it) }

    command.add("-Xms${config.memory.minimum}M")
    command.add("-Xmx${config.memory.maximum}M")

    command += "-jar"
    command += this.jarPath.toAbsolutePath().toString()

    command += "-nogui"

    // TODO: MC Arguments

    val latch = CountDownLatch(1)
    thread(name = "supervisor") {
      val thread = callingThread
      this.shutdownFlag.set(false)

      while (true) {
        val process = ProcessBuilder()
            .command(command)
            .directory(this.ctx.dataLocation.toFile())
            .start()

        val stdoutFlux = process.inputStream.bufferedReader()
            .lineFlux()
        val stderrFlux = process.errorStream.bufferedReader()
            .lineFlux()
        val logFlux = Flux.merge(stdoutFlux, stderrFlux)

        val disposable = logFlux
            .subscribe { this.stdoutSink.next(it) }

        this.process = process
        this.outputWriter = process.outputStream.writer()

        latch.countDown()

        this.aliveSink.next(true)

        var exitCode = -1
        supervise(process)
            .subscribe {
              exitCode = it
              LockSupport.unpark(thread)
            }

        LockSupport.park()

        disposable.dispose()
        this.aliveSink.next(false)

        this.lock.write {
          this.process = null
          this.outputWriter = null
        }

        if (exitCode != 0) {
          logger.info("Server has died with exit code $exitCode - Restarting")
        } else {
          logger.info("Server has been shut down")
        }

        if (this.shutdownFlag.compareAndSet(true, false)) {
          break
        }
      }
    }

    latch.await()
  }

  override fun announce(message: String) {
    this.sendCommand("say $message")
  }

  override fun sendCommand(command: String) {
    val writer = this.lock.read { this.outputWriter }
    require(writer != null) { "Server is not alive" }

    try {
      writer.write("$command\r\n")
      writer.flush()
    } catch (ex: IOException) {
      throw IllegalStateException("Failed to send command", ex)
    }
  }

  override fun stop(timeout: Duration): Boolean {
    val process = this.lock.read { this.process }
    require(process != null) { "Server is not alive" }

    this.shutdownFlag.set(true)
    this.sendCommand("stop")

    val exited = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)
    if (exited) {
      return true
    }

    return false
  }

  override fun kill() {
    val process = this.lock.read { this.process }
    require(process != null) { "Server is not alive" }

    process.destroyForcibly()
  }
}
