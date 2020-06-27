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
