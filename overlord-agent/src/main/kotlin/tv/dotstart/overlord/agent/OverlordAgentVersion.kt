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

import tv.dotstart.overlord.shared.util.openResource
import java.io.IOException
import java.util.*

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
object OverlordAgentVersion {

  /**
   * Identifies the human readable version number for this agent implementation.
   */
  val version: String

  /**
   * Specifies a channel on which this agent revision was released.
   *
   * Each channel maintains its own respective set of releases and primarily identifies whether a
   * given release is considered stable or receives support. It may also be used for automatic
   * update notifications.
   */
  val channel: String

  /**
   * Identifies whether this release is considered stable.
   */
  val stable: Boolean

  init {
    val properties = Properties()
    try {
      openResource("agent-version.properties")
          ?.let { properties.load(it) }
    } catch (ignore: IOException) {
    }

    this.version = properties.getProperty("version", "0.0.0+dev")
    this.channel = properties.getProperty("channel", "develop")
    this.stable = properties.getProperty("stable", "false")!!.toBoolean()
  }
}
