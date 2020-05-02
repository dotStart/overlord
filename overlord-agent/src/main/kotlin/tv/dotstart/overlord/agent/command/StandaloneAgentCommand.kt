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
package tv.dotstart.overlord.agent.command

import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.*
import org.apache.logging.log4j.LogManager
import tv.dotstart.overlord.agent.plugin.PluginContext
import tv.dotstart.overlord.agent.plugin.ServerFactoryContextImpl
import tv.dotstart.overlord.agent.plugin.getInstances
import tv.dotstart.overlord.agent.util.addShutdownHook
import tv.dotstart.overlord.model.server.ServerModel
import tv.dotstart.overlord.plugin.api.repository.Repository
import tv.dotstart.overlord.plugin.api.repository.getMatching
import tv.dotstart.overlord.plugin.api.server.ServerPlugin
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import kotlin.system.exitProcess

/**
 * Launches a standalone agent instance which maintains a server instance without requiring a
 * controller to instruct it via RPC.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
object StandaloneAgentCommand : AbstractAgentCommand("standalone", """
  Launches a standalone server instance without external oversight.
  
  Note: When launching in standalone mode, server instances will be managed locally by the agent
  itself thus requiring all resources to be available on the executing node.
  
  For example:
  
    $ java -jar overlord-agent.jar my-server.json
    
  Or, if you wish to execute a server bundle:
  
    $ java -jar overlord-agent.jar my-server.zip""") {

  /**
   * Defines the location at which the server definition file is located.
   */
  private val definitionFile by argument(
      "definition-file",
      help = "Location of the server definition file")
      .convert { Paths.get(it) as Path }
      .validate {
        require(Files.exists(it)) { "No such definition file" }
      }

  /**
   * Identifies whether the application is being executed in batch mode.
   */
  private val batchMode by option(
      "-b", "--batch",
      help = "Disables all interactive prompts (for use in scripts)")
      .flag()

  /**
   * Identifies a listing of end user agreement URLs which have been accepted by the end user.
   */
  private val acceptedEulaUrls by option(
      "--accept-eula",
      help = "Accepts a given end user agreement")
      .multiple()

  /**
   * Identifies the location at which server cache files (such as binaries and results of expensive
   * computations) are located.
   */
  private val cacheLocation by option(
      "-c", "--cache-location",
      help = "Location of the server cache")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("cache") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal cache directory" }
      }

  /**
   * Identifies the persistent location at which server data (such as save data and plugins) are
   * located.
   */
  private val dataLocation by option(
      "-d", "--data-location",
      help = "Location of the server data")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("data") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal data directory" }
      }

  /**
   * Identifies a location at which additional agent plugins are stored.
   *
   * JAR archives from this directory will be loaded upon startup in order to provide additional
   * support for repositories and other basic functionality.
   */
  private val pluginLocation by option(
      "-p", "--plugin-location",
      help = "Location of additional agent plugins")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("plugins") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal plugin directory" }
      }

  /**
   * Overrides the URI of the plugin to be used for managing the server instance.
   *
   * Typically this option is used during plugin development in order to force a known good
   * definition to be operated with a specific version of the plugin (or an alternative if
   * applicable).
   */
  private val serverPluginOverride by option(
      "--override-server-plugin",
      help = "Overrides the server management plugin to be used")
      .convert { URI.create(it) }

  /**
   * Defines the total amount of time the server may take to shut down gracefully (in minutes)
   * before it is killed by the agent.
   *
   * This value is only relevant when the agent is shut down gracefully (e.g. via ctrl+c or a
   * comparable signal). If a kill signal is received, the JVM will shut down immediately without
   * performing a clean server shutdown.
   */
  private val shutdownTimeout by option(
      "--shutdown-timeout",
      help = "Specifies the maximum amount a server may take for a graceful shutdown (in minutes)")
      .convert {
        val minutes = it.toLong(10)
        Duration.ofMinutes(minutes)
      }
      .defaultLazy { Duration.ofMinutes(1) }

  override fun run() {
    super.run()

    logger.info("Server definition: ${definitionFile.toAbsolutePath()}")

    val definition = Files.newBufferedReader(this.definitionFile)
        .use { ServerModel.load(it) }

    logger.debug("Definition Version: ${definition.version}")
    logger.info(
        "Launching definition \"${definition.metadata.name}\" v${definition.metadata.version}")

    var repositoryPlugins = Repository.available
    if (Files.exists(this.pluginLocation)) {
      logger.info("Loading plugins from $pluginLocation")
      val plugins = PluginContext.loadAll(this.pluginLocation)

      repositoryPlugins = (repositoryPlugins + plugins.getInstances(Repository))
          .sortedByDescending { it.priority }
          .distinctBy { it.scheme }

      logger.debug("Discovered ${repositoryPlugins.size} repository plugins")
    } else {
      logger.debug("Using ${repositoryPlugins.size} standard repository plugins")
    }

    val pluginUri = this.serverPluginOverride ?: definition.plugin
    logger.info("Server Plugin: $pluginUri")

    val pluginPath = if (pluginUri.scheme == "file") {
      Paths.get(pluginUri)
    } else {
      val repository = repositoryPlugins.getMatching(pluginUri)
          ?: throw IllegalStateException("No repository for scheme ${pluginUri.scheme}")

      val targetPath = Files.createTempFile("overlord_plugin", "_srv.jar")
      addShutdownHook("delete-plugin") {
        logger.info("Deleting server plugin at $targetPath")
        Files.deleteIfExists(targetPath)
      }

      logger.info("Fetching server plugin")
      repository.fetch(pluginUri, targetPath)
      targetPath
    }

    logger.debug("Plugin Path: $pluginPath")

    val pluginContext = PluginContext(pluginPath)
    val serverPlugin = pluginContext[ServerPlugin] ?: throw IllegalStateException(
        "Illegal server plugin: No compatible ServerPlugin instance available")

    if (serverPlugin.eulaUrls.isNotEmpty()) {
      val remainingUrls = serverPlugin.eulaUrls
          .filter { it !in this.acceptedEulaUrls }

      serverPlugin.eulaUrls
          .filter { it in this.acceptedEulaUrls }
          .forEach {
            logger.warn("Accepted EULA at $it via command-line arguments")
          }

      if (this.batchMode && remainingUrls.isNotEmpty()) {
        logger.error("Missing EULA agreements:")
        remainingUrls.forEach {
          logger.error("  - $it")
        }

        exitProcess(1)
      }

      if (remainingUrls.isNotEmpty()) {
        remainingUrls.forEach {
          val result = TermUi.confirm("Accept terms at $it") ?: false
          if (!result) {
            logger.error("All EULAs must be accepted to permit server installation and execution")
            exitProcess(1)
          }
        }
      }
    }

    logger.info("Using ${serverPlugin.displayName} v${serverPlugin.version}")
    val ctx = ServerFactoryContextImpl(this.cacheLocation, this.dataLocation)
    val serverFactory = serverPlugin.createInstanceFactory(ctx)

    pluginContext.using {
      logger.info("Configuring server instance")
      val instance = serverFactory.createInstance(definition.configuration)

      val serverLogger = LogManager.getLogger("server-instance")
      instance.stdout.subscribe { serverLogger.info(it) }

      logger.info("Starting server")
      instance.start()

      addShutdownHook("server-shutdown") {
        logger.info("Received shutdown signal - Performing graceful server shutdown")

        if (!instance.stop(this.shutdownTimeout)) {
          logger.warn("Shutdown timeout exceeded - Killing server")
          instance.kill()
        }
      }
    }
  }
}
