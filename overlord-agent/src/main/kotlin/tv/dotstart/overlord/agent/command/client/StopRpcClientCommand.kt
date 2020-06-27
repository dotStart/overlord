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
