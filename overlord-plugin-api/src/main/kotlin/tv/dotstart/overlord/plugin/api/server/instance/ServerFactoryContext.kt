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
package tv.dotstart.overlord.plugin.api.server.instance

import java.nio.file.Path

/**
 * Provides contextual information for server plugins.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 23/04/2020
 */
interface ServerFactoryContext {

  /**
   * Identifies the location at which resources such as executables or results of expensive
   * calculations may be stored.
   *
   * Note: Files stored within the server cache are not included in backup archives and must be
   * restored by the plugin upon startup.
   */
  val cacheLocation: Path

  /**
   * Identifies the location at which server data (such as user configurations, extensions and save
   * data) may be stored.
   */
  val dataLocation: Path
}
