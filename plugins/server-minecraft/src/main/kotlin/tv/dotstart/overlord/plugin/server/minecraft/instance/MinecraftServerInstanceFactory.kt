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
package tv.dotstart.overlord.plugin.server.minecraft.instance

import com.fasterxml.jackson.module.kotlin.readValue
import tv.dotstart.overlord.plugin.server.minecraft.config.MinecraftPluginConfiguration
import tv.dotstart.overlord.plugin.server.minecraft.manifest.VersionManifestIndex
import tv.dotstart.overlord.plugin.server.minecraft.manifest.version.VersionSpecification
import tv.dotstart.overlord.shared.delegate.log
import tv.dotstart.overlord.shared.mapping.toObject
import tv.dotstart.overlord.plugin.api.server.instance.ServerFactoryContext
import tv.dotstart.overlord.plugin.api.server.instance.ServerInstance
import tv.dotstart.overlord.plugin.api.server.instance.ServerInstanceFactory
import tv.dotstart.overlord.shared.util.createObjectMapper
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant

/**
 * Provides a server instance factory for vanilla Minecraft servers.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 23/04/2020
 */
class MinecraftServerInstanceFactory(
    private val ctx: ServerFactoryContext) : ServerInstanceFactory {

  companion object {
    private val logger by log()
  }

  override fun createInstance(configuration: Map<String, Any?>): ServerInstance {
    val config = configuration.toObject<MinecraftPluginConfiguration>()

    val jarDirectory = this.ctx.cacheLocation.resolve("artifacts")
    Files.createDirectories(jarDirectory)

    val jarPath = jarDirectory.resolve("${config.version}.jar")

    val latestRequested = VersionManifestIndex.latestMarker == config.version ||
        VersionManifestIndex.latestSnapshotMarker == config.version
    if (latestRequested || !Files.exists(jarPath)) {
      this.downloadArtifact(config.version, jarPath, config.cache.manifestCacheDuration)
    }

    return MinecraftServerInstance(
        ctx, jarPath, config)
  }

  private fun downloadArtifact(version: String, targetPath: Path, cacheLifetime: Duration) {
    val manifest = this.getVersionSpecification(version, cacheLifetime)

    logger.info("Downloading server archive for version $version")
    manifest.downloads.server.fetch(targetPath)
  }

  private fun getVersionSpecification(version: String,
                                      cacheLifetime: Duration): VersionSpecification {
    val mapper = createObjectMapper()

    val manifestPath = this.ctx.cacheLocation.resolve("version_manifest.json")
    if (Files.exists(manifestPath)) {
      val modificationTime = Files.getLastModifiedTime(manifestPath).toInstant()
      val lifetime = Duration.between(modificationTime, Instant.now())

      if (lifetime < cacheLifetime) {
        try {
          val manifestIndex = Files.newBufferedReader(manifestPath).use {
            mapper.readValue<VersionManifestIndex>(it)
          }

          val spec = try {
            manifestIndex[version]?.specification
          } catch (ex: IOException) {
            logger.warn(
                "Failed to retrieve version specification for \"$version\" from cached URL",
                ex
            )

            null
          }

          if (spec != null) {
            logger.debug("Using cached version specification")
            return spec
          }
        } catch (ex: IOException) {
          logger.warn("Failed to read cached manifest index", ex)
        }
      } else {
        logger.debug("Cached manifest index has expired")
      }
    }

    logger.debug("Updating version manifest index")
    val manifestIndex = VersionManifestIndex.getManifest()
    try {
      Files.newBufferedWriter(manifestPath).use {
        mapper.writeValue(it, manifestIndex)
      }
    } catch (ex: IOException) {
      logger.warn("Failed to write cached manifest index", ex)
    }

    logger.debug("Retrieving version manifest")
    return manifestIndex[version]
        ?.specification
        ?: throw IllegalArgumentException("Illegal server version: $version")
  }
}
