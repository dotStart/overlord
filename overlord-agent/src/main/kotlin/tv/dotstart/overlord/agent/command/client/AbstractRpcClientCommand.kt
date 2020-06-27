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
package tv.dotstart.overlord.agent.command.client

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import tv.dotstart.overlord.agent.util.parseSocketAddress
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 05/05/2020
 */
abstract class AbstractRpcClientCommand(
    name: String,
    help: String) : CliktCommand(name = name, help = help) {

  /**
   * Defines the address of the agent with which communication is established by this client
   * command.
   */
  protected val serverAddress by option(
      "-a", "--agent-address",
      help = "Specifies the address at which the target agent listens for requests")
      .convert { parseSocketAddress(it, defaultPort) }
      .defaultLazy { InetSocketAddress(InetAddress.getLoopbackAddress(), defaultPort) }

  /**
   * Disables TLS validation on the RPC server side.
   */
  protected val insecure by option(
      "--insecure",
      help = """
        Disables TLS communication with the agent.
        
        This option is provided for cases where secure communication may not be beneficial (as is
        the case when using UNIX domain sockets) and would thus only introduce additional load.
        
        As such, this option should be left disabled unless the network is trustworthy in its
        entirety and intrusion is impossible (e.g. when listening on local addresses which are not
        routed to the outside).
        
        Note, however, that authentication may still be preferable for VPC networks as attackers may
        potentially be able to abuse the remote execution abilities provided by the Overlord agent
        through other intrusion vectors.
      """.trimIndent())
      .flag()

  companion object {
    private const val defaultPort = 10431
  }

  override fun run() {
    println("Establishing communication with agent at $serverAddress")
    if (this.insecure) {
      println("Warning: TLS has been disabled by user request")
    }

    val client = NettyChannelBuilder.forAddress(this.serverAddress)
        .also {
          if (this.insecure) {
            it.usePlaintext()
          }
        }
        .build()

    this.run(client)
  }

  protected abstract fun run(channel: ManagedChannel)
}
