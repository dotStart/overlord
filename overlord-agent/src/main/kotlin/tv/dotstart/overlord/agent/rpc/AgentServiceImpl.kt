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
package tv.dotstart.overlord.agent.rpc

import com.google.protobuf.Empty
import com.google.protobuf.StringValue
import io.grpc.stub.StreamObserver
import tv.dotstart.overlord.agent.api.AgentGrpc
import tv.dotstart.overlord.agent.api.AgentService.*
import tv.dotstart.overlord.agent.api.toMap
import tv.dotstart.overlord.agent.plugin.PluginContext
import tv.dotstart.overlord.agent.util.addShutdownHook
import tv.dotstart.overlord.agent.util.complete
import tv.dotstart.overlord.agent.util.completeObserver
import tv.dotstart.overlord.agent.util.noopObserver
import tv.dotstart.overlord.plugin.api.repository.Repository
import tv.dotstart.overlord.plugin.api.repository.getMatching
import tv.dotstart.overlord.plugin.api.server.ServerPlugin
import tv.dotstart.overlord.plugin.api.server.extensions.Announcing
import tv.dotstart.overlord.plugin.api.server.extensions.CommandReceiver
import tv.dotstart.overlord.plugin.api.server.extensions.Reloadable
import tv.dotstart.overlord.plugin.api.server.instance.ServerFactoryContext
import tv.dotstart.overlord.plugin.api.server.instance.ServerInstance
import tv.dotstart.overlord.shared.delegate.log
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.util.concurrent.TimeoutException

/**
 * Provides an RPC server implementation.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */
class AgentServiceImpl(
    private val ctx: ServerFactoryContext,
    private val repositories: List<Repository>) : AgentGrpc.AgentImplBase() {

  private var serverInstance: ServerInstance? = null
  private var pluginUri: URI? = null
  private var alive = false

  companion object {
    private val logger by log()
  }

  override fun getStatus(request: Empty,
                         ob: StreamObserver<AgentState>) {
    val provisioned = this.serverInstance != null
    val alive = this.alive
    val pluginUri = this.pluginUri

    ob.complete(AgentState.newBuilder()
                    .setProvisioned(provisioned)
                    .setAlive(alive)
                    .apply {
                      pluginUri?.toASCIIString()
                          ?.let { setPluginUri(it) }
                    }
                    .build())
  }

  override fun subscribe(ob: StreamObserver<StateUpdate>): StreamObserver<Empty> {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return noopObserver()
    }

    val aliveDisposable = serverInstance.alive
        .subscribe {
          ob.onNext(StateUpdate.newBuilder()
                        .setAlive(it)
                        .build())
        }
    val logDisposable = serverInstance.stdout
        .subscribe {
          ob.onNext(StateUpdate.newBuilder()
                        .setLogMessage(it)
                        .build())
        }

    return completeObserver {
      aliveDisposable.dispose()
      logDisposable.dispose()
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  override fun provision(request: ProvisionRequest, ob: StreamObserver<ProvisionResponse>) {
    if (this.serverInstance != null) {
      ob.onError(IllegalStateException("Already provisioned"))
      return
    }

    val pluginUri = request.pluginUri
        ?.let { URI.create(it) }
    val pluginString = request.pluginBlob
    val pluginPath = Files.createTempFile("overlord_agent", "rpc_server_plugin.jar")

    when {
      pluginString != null -> {
        logger.debug("Writing plugin blob to $pluginPath")
        Files.newOutputStream(pluginPath, StandardOpenOption.TRUNCATE_EXISTING).use {
          pluginString.writeTo(it)
        }
      }
      pluginUri != null -> {
        logger.debug("Fetching plugin from $pluginUri to $pluginPath")

        val repository = this.repositories.getMatching(pluginUri)
        if (repository == null) {
          logger.error("No compatible repository for plugin URI: $pluginUri")
          ob.onError(IllegalStateException("No compatible repository for plugin URI: $pluginUri"))
          return
        }

        repository.fetch(pluginUri, pluginPath)
      }
      else -> {
        logger.error("RPC failed to provide plugin blob or URI")
        ob.onError(IllegalStateException("Failed to provide plugin blob or URI"))
        return
      }
    }

    addShutdownHook("delete-plugin") {
      logger.info("Deleting server plugin archive from $pluginPath")
      Files.deleteIfExists(pluginPath)
    }

    val pluginConfig = request.pluginConfiguration
        ?.toMap()
    if (pluginConfig == null) {
      logger.error("RPC failed to provide plugin configuration")
      ob.onError(IllegalStateException("Failed to provide plugin configuration"))
      return
    }

    logger.info("Initializing server plugin")
    val pluginCtx = PluginContext(pluginPath)
    val serverPlugin = pluginCtx[ServerPlugin]
    if (serverPlugin == null) {
      logger.error("RPC provided plugin archive does not provide ServerPlugin implementation")
      ob.onError(IllegalStateException(
          "Server plugin archive does not provide ServerPlugin implementation"))
      return
    }

    logger.info("Using plugin ${serverPlugin.displayName} v${serverPlugin.version}")

    logger.debug("Initializing server instance factory")
    val serverInstanceFactory = serverPlugin.createInstanceFactory(this.ctx)

    logger.info("Provisioning server instance")
    val serverInstance = serverInstanceFactory.createInstance(pluginConfig)

    val capabilities = buildList {
      if (serverInstance is Announcing) {
        add(ServerInstanceCapability.ANNOUNCEMENTS)
      }

      if (serverInstance is CommandReceiver) {
        add(ServerInstanceCapability.COMMAND_EXECUTION)
      }

      if (serverInstance is Reloadable) {
        add(ServerInstanceCapability.CONFIGURATION_RELOAD)
      }
    }

    this.serverInstance = serverInstance
    ob.complete(ProvisionResponse.newBuilder()
                    .setPluginDisplayName(serverPlugin.displayName)
                    .setPluginVersion(serverPlugin.version)
                    .addAllCapabilities(capabilities)
                    .apply {
                      pluginUri?.toASCIIString()
                          ?.let(this::setPluginUri)
                    }
                    .build())
  }

  override fun reconfigure(request: ConfigurationMap, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    val pluginConfig = request.toMap()
    serverInstance.reconfigure(pluginConfig)

    ob.complete()
  }

  override fun start(request: Empty, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    serverInstance.start()

    ob.complete()
  }

  override fun stop(request: StopRequest, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    if (!serverInstance.stop(Duration.ofMillis(request.timeout))) {
      ob.onError(TimeoutException("Failed to shut down within timeout period"))
      return
    }

    ob.complete()
  }

  override fun kill(request: Empty, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    serverInstance.kill()

    ob.complete()
  }

  override fun executeCommand(request: StringValue, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    val commandReceiver = serverInstance as? CommandReceiver
    if (commandReceiver == null) {
      ob.onError(IllegalStateException("Server Instance does not support commands"))
      return
    }

    commandReceiver.sendCommand(request.value)

    ob.complete()
  }

  override fun announce(request: StringValue, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    val announcing = serverInstance as? Announcing
    if (announcing == null) {
      ob.onError(IllegalStateException("Server Instance does not support announcements"))
      return
    }

    announcing.announce(request.value)

    ob.complete()
  }

  override fun reload(request: Empty, ob: StreamObserver<Empty>) {
    val serverInstance = this.serverInstance
    if (serverInstance == null) {
      ob.onError(IllegalStateException("Agent has yet to be provisioned"))
      return
    }

    val reloadable = serverInstance as? Reloadable
    if (reloadable == null) {
      ob.onError(IllegalStateException("Server Instance does not support reloading"))
      return
    }

    reloadable.reload()

    ob.complete()
  }
}
