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
package tv.dotstart.overlord.shared.repository

import tv.dotstart.overlord.shared.plugin.Pluggable
import java.net.URI
import java.nio.file.Path

/**
 * Provides facilities to retrieve plugins automatically from a
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 22/04/2020
 */
interface Repository : Pluggable {

  /**
   * Provides a scheme with which compatible plugin URIs are recognized.
   *
   * Plugin URIs are provided in the format of "overlord+scheme://plugin-path" in which the
   * plugin-path is defined by the repository implementation. For instance, the GitHub plugin will
   * use URIs similar to this: "overlord+github://owner/repository/fileName"
   */
  val scheme: String

  /**
   * Retrieves a given plugin via this repository.
   */
  fun fetch(uri: URI, target: Path)

  companion object : Pluggable.Definition<Repository>() {
    override val type = Repository::class
  }
}
