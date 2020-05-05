package tv.dotstart.overlord.agent.command.client

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import io.grpc.ManagedChannel
import tv.dotstart.overlord.agent.api.AgentGrpc
import tv.dotstart.overlord.agent.api.AgentService

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @date 06/05/2020
 */
object StartRpcClientCommand : AbstractRpcClientCommand(
    "stop",
    "Stops the execution of a previously provisioned agent") {

  private val timeout by option(
      "-t", "--timeout",
      help = "Specifies the maximum amount of time the agent may take to shut down the server before the request is considered failed")
      .long()
      .default(60000)

  override fun run(channel: ManagedChannel) {
    val agent = AgentGrpc.newBlockingStub(channel)

    println("Requesting server start ...")
    agent.stop(AgentService.StopRequest.newBuilder()
                   .setTimeout(this.timeout)
                   .build())

    println()
    println("+-------------------------------------+")
    println("|               SUCCESS               |")
    println("+-------------------------------------+")
  }
}
