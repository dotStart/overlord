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

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */
abstract class AbstractExecutionAgentCommand(name: String, help: String) :
    AbstractAgentCommand(name, help) {

  /**
   * Identifies the location at which server cache files (such as binaries and results of expensive
   * computations) are located.
   */
  protected val cacheLocation by option(
      "-c", "--cache-location",
      help = "Location of the server cache")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("cache") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal cache directory" }
      }

  /**
   * Identifies the persistent location at which server data (such as save data and plugins) are
   * located.
   */
  protected val dataLocation by option(
      "-d", "--data-location",
      help = "Location of the server data")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("data") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal data directory" }
      }

  /**
   * Identifies a location at which additional agent plugins are stored.
   *
   * JAR archives from this directory will be loaded upon startup in order to provide additional
   * support for repositories and other basic functionality.
   */
  protected val pluginLocation by option(
      "-p", "--plugin-location",
      help = "Location of additional agent plugins")
      .convert { Paths.get(it) as Path }
      .defaultLazy { Paths.get("plugins") }
      .validate {
        require(Files.notExists(it) || Files.isDirectory(it)) { "Illegal plugin directory" }
      }
}
