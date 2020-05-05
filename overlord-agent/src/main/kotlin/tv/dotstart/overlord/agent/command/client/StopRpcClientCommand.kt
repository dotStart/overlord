package tv.dotstart.overlord.agent.command.client

import com.google.protobuf.Empty
import io.grpc.ManagedChannel
import tv.dotstart.overlord.agent.api.AgentGrpc

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @date 06/05/2020
 */
object StopRpcClientCommand : AbstractRpcClientCommand(
    "start",
    "Starts the execution of a previously provisioned agent") {

  override fun run(channel: ManagedChannel) {
    val agent = AgentGrpc.newBlockingStub(channel)

    println("Requesting server stop ...")
    agent.start(Empty.getDefaultInstance())

    println()
    println("+-------------------------------------+")
    println("|               SUCCESS               |")
    println("+-------------------------------------+")
  }
}
