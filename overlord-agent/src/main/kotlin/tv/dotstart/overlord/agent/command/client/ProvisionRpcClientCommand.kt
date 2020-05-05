package tv.dotstart.overlord.agent.command.client

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import io.grpc.ManagedChannel
import tv.dotstart.overlord.agent.api.AgentGrpc
import tv.dotstart.overlord.agent.api.AgentService
import tv.dotstart.overlord.agent.api.toConfigurationMap
import tv.dotstart.overlord.model.server.ServerModel
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 06/05/2020
 */
object ProvisionRpcClientCommand : AbstractRpcClientCommand(
    "provision",
    """Provisions a remote agent with a given server definition file""") {

  /**
   * Identifies the location of the definition file which is to be transmitted to the agent for
   * provisioning.
   */
  private val definitionFile by argument(
      "definition-file",
      help = "Specifies the server definition which is to be provisioned")
      .convert { Paths.get(it) }

  private val serverPluginOverride by option(
      "--override-server-plugin",
      help = "Specifies a plugin URI which replaces the value provided by the definition file")
      .convert { URI.create(it) }

  override fun run(channel: ManagedChannel) {
    val agent = AgentGrpc.newBlockingStub(channel)

    val definition = Files.newBufferedReader(this.definitionFile)
        .use { ServerModel.load(it) }

    println("Transmitting provisioning request for definition $definitionFile")
    val response = agent.provision(definition.toRequest())

    println()
    println("+-------------------------------------+")
    println("|               SUCCESS               |")
    println("+-------------------------------------+")
    println()
    println("Plugin: ${response.pluginDisplayName}")
    println("Version: ${response.pluginVersion}")
    println("URI: ${response.pluginUri}")
    println()
    println("Capabilities")
    println("------------")
    println()
    response.capabilitiesList.forEach { println(" - $it") }
  }

  private fun ServerModel.toRequest(): AgentService.ProvisionRequest {
    val pluginUri = serverPluginOverride ?: this.plugin

    return AgentService.ProvisionRequest
        .newBuilder()
        .setPluginUri(pluginUri.toASCIIString())
        .setPluginConfiguration(this.configuration.toConfigurationMap())
        .build()
  }
}
