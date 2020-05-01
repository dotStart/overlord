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
package tv.dotstart.overlord.plugin.minecraft.config

import tv.dotstart.overlord.shared.mapping.Named

/**
 * Formally specifies the configuration of plugin parameters.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
data class MinecraftPluginConfiguration(

    /**
     * Identifies the server version which is to be installed.
     */
    val version: String = LATEST_MARKER,

    /**
     * Defines configuration parameters related to caching within the plugin.
     */
    val cache: CacheConfiguration = CacheConfiguration(),

    /**
     * Defines configuration parameters related to the JVM memory utilization.
     */
    val memory: MemoryConfiguration = MemoryConfiguration(),

    /**
     * Defines a set of parameters which are passed to the JVM in addition to the standard memory
     * related parameters.
     */
    val jvmParameters: String = "",

    /**
     * Stores a raw map of properties which will be passed to the server.
     */
    // TODO: Type safe configuration
    @Named("server.properties")
    val serverProperties: Map<String, Any?>) {

  companion object {
    const val LATEST_MARKER = "latest"
    const val LATEST_SNAPSHOT_MARKER = "latest-snapshot"
  }
}
