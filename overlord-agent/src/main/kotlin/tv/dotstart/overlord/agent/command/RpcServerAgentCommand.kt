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

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.defaultLazy
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import tv.dotstart.overlord.agent.plugin.ServerFactoryContextImpl
import tv.dotstart.overlord.agent.rpc.AgentServiceImpl
import tv.dotstart.overlord.agent.util.parseSocketAddress
import tv.dotstart.overlord.plugin.api.repository.Repository
import java.net.InetSocketAddress

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */
object RpcServerAgentCommand : AbstractExecutionAgentCommand(
    "rpc-server",
    """
      Starts an RPC session in which the agent may be provisioned and controlled via an external
      application.
      
      For instance:
      
        $ java -jar overlord-agent.jar rpc-server
        
      Followed by one of the various rpc-client commands:
      
        $ java -jar overlord-agent.jar rpc provision examples/minecraft.yaml
        $ java -jar overlord-agent.jar rpc start
        $ java -jar overlord-agent.jar rpc stop
        
      Note: The RPC server is not designed to be interacted with directly. Instead, the SDK should
      be used to create a supervisor implementation which manages server instances.
    """.trimIndent()) {

  private const val defaultPort = 10431

  private val address by argument(
      "listen-address",
      help = """
        Specifies an address on which the RPC server will listen.
        
        When the hostname/ip address aspect is omitted, the server will simply listen on all
        available addresses (this may be preferable for container or virtual machine configurations
        where the public address is not known ahead of time).
        
        Alternatively, agents may listen on a UNIX domain socket on compatible Linux machines where
        this functionality is available (for example: "unix:/var/run/overlord.sock"). This listening
        configuration may be preferable when the supervisor is executed on the same machine as the
        agent as it provides the least overhead.
        """.trimIndent())
      .convert { parseSocketAddress(it, defaultPort) }
      .defaultLazy { InetSocketAddress(defaultPort) }

  override fun run(repositoryPlugins: List<Repository>) {
    val ctx = ServerFactoryContextImpl(this.cacheLocation, this.dataLocation)

    val grpc = NettyServerBuilder
        .forAddress(this.address)
        .addService(AgentServiceImpl(ctx, repositoryPlugins))
        .build()

    grpc.start()

    logger.info("Listening for RPC calls on $address")

    grpc.awaitTermination()
  }
}
