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
package tv.dotstart.overlord.plugin.api.repository

import tv.dotstart.overlord.shared.util.createHttpClient
import tv.dotstart.overlord.shared.util.fetchTo
import tv.dotstart.overlord.shared.util.newHttpRequest
import java.net.URI
import java.nio.file.Path

/**
 * Fetches plugin files from an arbitrary HTTP URI.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 29/04/2020
 */
class HttpRepository : Repository {

  override val scheme = "http"

  override fun fetch(uri: URI, target: Path) {
    val strippedUri = uri.toASCIIString()
        .stripOverlordScheme()

    val request = newHttpRequest()
        .GET()
        .uri(URI.create(strippedUri))
        .build()

    createHttpClient().fetchTo(request, target)
  }
}
