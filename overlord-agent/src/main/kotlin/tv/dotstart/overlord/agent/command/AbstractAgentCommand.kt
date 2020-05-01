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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.switch
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import tv.dotstart.overlord.shared.delegate.log
import tv.dotstart.overlord.shared.util.printBanner
import tv.dotstart.overlord.agent.OverlordAgentVersion as AgentVersion

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
abstract class AbstractAgentCommand(name: String, help: String) :
    CliktCommand(name = name, help = help) {

  protected val logger by log()

  private val skipBanner by option(
      "--skip-banner",
      help = "Skips the application banner at startup")
      .flag()

  private val logLevel by option().switch(
      "--errors-only" to Level.ERROR,
      "--debug" to Level.DEBUG,
      "--verbose" to Level.ALL)
      .default(Level.INFO)

  override fun run() {
    if (!this.skipBanner) {
      printBanner()
      println("v${AgentVersion.version} (${AgentVersion.channel})")
      println()
    }

    if (!AgentVersion.stable) {
      logger.warn("You are running an unstable version of Overlord")
      logger.warn("No support will be provided for this release")
    }

    if (this.logLevel != Level.INFO) {
      Configurator.setRootLevel(this.logLevel)
      logger.warn("Adjusted log level to $logLevel by user request")
    }
  }
}
