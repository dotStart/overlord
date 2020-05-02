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
package tv.dotstart.overlord.plugin.api.server.extensions

import tv.dotstart.overlord.plugin.api.server.instance.ServerInstance

/**
 * Represents a server instance which is capable of reloading its configuration on the fly.
 *
 * While all server instances may be initially configured, only some may switch their
 * configuration (or aspects of their configuration) on the fly. This interface acts as a marker for
 * this functionality and exposes the necessary additional functions.
 *
 * Note: When this interface is not implemented by a server instance, the configuration is expected
 * to be changed by restarting the server.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/04/2020
 */
interface Reloadable : ServerInstance {

  /**
   * Triggers a configuration or plugin reload on a running server instance.
   *
   * @throws IllegalStateException when the server is not running.
   */
  fun reload()
}
