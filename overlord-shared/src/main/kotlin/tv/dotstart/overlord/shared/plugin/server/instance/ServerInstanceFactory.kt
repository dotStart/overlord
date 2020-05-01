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
package tv.dotstart.overlord.shared.plugin.server.instance

/**
 * Provides integration with a game server implementation.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 23/04/2020
 */
interface ServerInstanceFactory {

  /**
   * Provisions a new server instance at the desired location.
   *
   * When invoked, the plugin is expected to prepare the server's cache and data directories for
   * execution and return a handle with which the server may be re-configured, started and stopped.
   */
  fun createInstance(configuration: Map<String, Any?>): ServerInstance
}
