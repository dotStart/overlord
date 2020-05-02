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

import com.fasterxml.jackson.annotation.JsonProperty
import tv.dotstart.overlord.shared.delegate.log
import tv.dotstart.overlord.shared.util.createHttpClient
import tv.dotstart.overlord.shared.util.fetchEntity
import tv.dotstart.overlord.shared.util.fetchTo
import tv.dotstart.overlord.shared.util.newHttpRequest
import java.net.URI
import java.nio.file.Path

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 01/05/2020
 */
class GitHubRepository : Repository {

  override val scheme = "github"

  companion object {
    private val logger by log()
  }

  override fun fetch(uri: URI, target: Path) {
    val ownerName = uri.authority
    require(ownerName.isNotBlank() && !ownerName.contains('/')) { "Illegal owner" }

    val path = uri.path.removePrefix("/")
    val pathElements = path.split('/', limit = 3)
    require(pathElements.size >= 2) { "Missing asset name" }

    val repositoryName = pathElements[0]
    val assetVersion = if (pathElements.size == 3) {
      pathElements[1]
    } else {
      null
    }
    val assetName = if (pathElements.size == 2) {
      pathElements[1]
    } else {
      pathElements[2]
    }

    require(repositoryName.isNotBlank() && !repositoryName.contains('/')) {
      "Illegal repository name"
    }

    val releaseUri = if (assetVersion != null) {
      URI.create(
          "https://api.github.com/repos/$ownerName/$repositoryName/releases/tags/$assetVersion")
    } else {
      URI.create("https://api.github.com/repos/$ownerName/$repositoryName/releases/latest")
    }

    val client = createHttpClient()

    val releaseRequest = newHttpRequest()
        .GET()
        .uri(releaseUri)
        .header("Accept", "application/vnd.github.v3+json")
        .build()

    logger.debug("Fetching release information from $releaseUri")
    val release = client.fetchEntity<Release>(releaseRequest)
        ?: throw IllegalStateException(
            """No such release: $ownerName/$repositoryName (${assetVersion ?: "latest"})""")

    val asset = release.assets
        .firstOrNull { it.name == assetName }
        ?: throw IllegalStateException(
            """No such asset: $ownerName/$repositoryName/$assetName (${assetVersion ?: "latest"}""")

    val assetRequest = newHttpRequest()
        .GET()
        .uri(asset.url)
        .header("Accept", "application/vnd.github.v3+json")
        .build()

    logger.debug("Fetching asset from ${asset.url}")
    client.fetchTo(assetRequest, target)
  }

  data class Release(
      @JsonProperty("tag_name")
      val tagName: String,
      val assets: List<Asset>)

  data class Asset(
      val url: URI,
      val name: String)
}
