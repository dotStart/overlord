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
package tv.dotstart.overlord.agent

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import tv.dotstart.overlord.agent.command.StandaloneAgentCommand
import tv.dotstart.overlord.shared.MachineVersion

/**
 * Bootstraps the Overlord agent runtime for execution in either standalone or rpc mode.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
object OverlordAgent : NoOpCliktCommand(name = "overlord-agent") {

  init {
    versionOption(OverlordAgentVersion.version)

    subcommands(
        StandaloneAgentCommand
    )
  }
}

fun main(args: Array<String>) {
  MachineVersion.JAVA_11.require()

  OverlordAgent.main(args)
}
