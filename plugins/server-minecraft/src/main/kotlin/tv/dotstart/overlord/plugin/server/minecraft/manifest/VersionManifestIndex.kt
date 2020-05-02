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
import tv.dotstart.overlord.plugin.server.minecraft.manifest.version.ReleaseMetadata
import tv.dotstart.overlord.plugin.server.minecraft.util.getEntity
import tv.dotstart.overlord.shared.util.createHttpClient
import java.net.URI

/**
 * Represents the entire index of available version manifests.
 *
 * In addition to the version listing, this object encodes information regarding the latest stable
 * and snapshot release respectively.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
data class VersionManifestIndex(

    /**
     * Identifies the latest snapshot and stable release version id respectively.
     *
     * Note: This object only encodes the identifications meaning that callers will have to perform
     * a separate lookup via the provided utility properties or [get].
     */
    val latest: ReleaseMetadata,

    /**
     * Provides a listing of all recognized versions.
     *
     * Note: This list may include old releases which may not provide full support for all
     * configuration properties included within the plugin specification.
     */
    val versions: List<VersionManifest>) {

  /**
   * Retrieves the manifest of the latest stable release.
   *
   * @throws IllegalStateException when the manifest points to a version which is not present within
   * the version index.
   */
  @get:JsonIgnore
  val latestStableManifest: VersionManifest by lazy {
    val release = this.latest.release

    this[release] ?: throw IllegalStateException(
        "Malformed manifest: Cannot resolve latest stable \"$release\"")
  }

  /**
   * Retrieves the manifest of the latest snapshot release.
   *
   * @throws IllegalStateException when the manifest points to a version which is not present within
   * the version index.
   */
  @get:JsonIgnore
  val latestSnapshotManifest: VersionManifest by lazy {
    val release = this.latest.snapshot

    this[release] ?: throw IllegalStateException(
        "Malformed manifest: Cannot resolve latest snapshot: \"$release\"")
  }

  /**
   * Retrieves the manifest of a specific version based on its human readable version identifier.
   *
   * When no version with the given identifier is present within the manifest (as may be the case
   * for revisions that have been removed due to security vulnerabilities), null is returned
   * instead.
   */
  operator fun get(id: String): VersionManifest? = when (id) {
    latestMarker -> this.latestStableManifest
    latestSnapshotMarker -> this.latestSnapshotManifest
    else -> this.versions.find { it.id == id }
  }

  companion object {

    const val latestMarker = "latest"
    const val latestSnapshotMarker = "latest-snapshot"

    /**
     * Identifies the location at which the version manifest index may be downloaded.
     */
    private const val documentUri = "https://launchermeta.mojang.com/mc/game/version_manifest.json"

    /**
     * Retrieves the latest version of the version manifest.
     */
    fun getManifest(): VersionManifestIndex = createHttpClient()
        .getEntity(URI.create(
            documentUri))
  }
}
