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
package tv.dotstart.overlord.plugin.server.minecraft.manifest

import com.fasterxml.jackson.annotation.JsonIgnore
import tv.dotstart.overlord.plugin.server.minecraft.manifest.version.ReleaseType
import tv.dotstart.overlord.plugin.server.minecraft.manifest.version.VersionSpecification
import tv.dotstart.overlord.plugin.server.minecraft.util.getEntity
import tv.dotstart.overlord.shared.util.createHttpClient
import java.net.URI

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
data class VersionManifest(
    val id: String,
    val type: ReleaseType,
    val url: URI) {

  @get:JsonIgnore
  val specification: VersionSpecification by lazy {
    // explicit specification of return type due to compiler bug
    createHttpClient().getEntity<VersionSpecification>(this.url)
  }
}
