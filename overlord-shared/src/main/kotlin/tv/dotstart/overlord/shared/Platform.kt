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
package tv.dotstart.overlord.shared

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
enum class Platform(vararg names: String) {

  LINUX("nux"),
  MAC_OS("mac"),
  UNIX("nix"),
  WINDOWS("win"),
  UNKNOWN;

  /**
   * Identifies whether this platform is currently executing the application.
   */
  val running: Boolean

  /**
   * Identifies the file extension which is appended to executable files on this operating system.
   */
  open val executableExtension = ""

  init {
    val osName = System.getProperty("os.name", "")
    this.running = names.any { osName.contains(it, ignoreCase = true) }
  }

  companion object {

    /**
     * Retrieves the currently executing platform.
     */
    val current: Platform by lazy {
      values().firstOrNull { it.running }
          ?: UNKNOWN
    }
  }
}
