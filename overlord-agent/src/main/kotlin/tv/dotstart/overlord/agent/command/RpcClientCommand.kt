package tv.dotstart.overlord.agent.command

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import tv.dotstart.overlord.agent.command.client.ProvisionRpcClientCommand

/**
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 * @date 05/05/2020
 */
object RpcClientCommand : NoOpCliktCommand(
    name = "rpc",
    help = """
      Provides a simple RPC client for interaction with RPC based agent instances.
      
      This command is primarily provided for debugging and maintenance purposes where user-level
      interaction with agent instances is desired.
    """.trimIndent()) {

  init {
    subcommands(
        ProvisionRpcClientCommand
    )
  }
}
