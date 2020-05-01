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
package tv.dotstart.overlord.shared.model

import tv.dotstart.overlord.shared.mapping.converter.Convert
import tv.dotstart.overlord.shared.mapping.converter.StringToURIConverter
import tv.dotstart.overlord.shared.model.error.UnsupportedServerDefinitionException
import tv.dotstart.overlord.shared.mapping.toObject
import java.io.Reader
import java.net.URI
import org.snakeyaml.engine.v2.api.Load as YamlLoader
import org.snakeyaml.engine.v2.api.LoadSettings as YamlLoaderSettings

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
data class ServerDefinition(

    /**
     * Identifies the model revision.
     *
     * This value is incremented when a new revision of the model is released and primarily used as
     * a compatibility marker. In some cases, multiple revisions may be supported by a given agent
     * implementation.
     */
    val version: Int,

    /**
     * Provides identifying information.
     */
    val metadata: ServerDefinitionMetadata,

    /**
     * Describes the location from which the server plugin may be loaded.
     *
     * Plugins may be retrieved from various different locations as defined by plugin repositories
     * within the agent Class-Path.
     */
    @Convert(StringToURIConverter::class)
    val plugin: URI,

    /**
     * Provides additional configuration parameters which are passed to the server plugin which
     * provisions and manages the server.
     */
    val configuration: Map<String, Any?>) {

  companion object {

    /**
     * Decodes a given human readable server definition.
     */
    fun load(reader: Reader): ServerDefinition {
      val settings = YamlLoaderSettings.builder()
          .build()
      val loader = YamlLoader(settings);

      val documents = loader.loadAllFromReader(reader)
          .filterIsInstance<Map<String, Any?>>()
          .sortedByDescending { it["version"] as? Int }

      val supportedMap = documents
          .firstOrNull { it["version"] as? Int == 1 }
      if (supportedMap == null) {
        val maxVersion = documents
            .map { it["version"] as? Int }
            .filterNotNull()
            .max()
            ?: 0

        throw UnsupportedServerDefinitionException(
            maxVersion,
            "Unsupported definition revision: $maxVersion")
      }

      return supportedMap.toObject()
    }
  }
}
