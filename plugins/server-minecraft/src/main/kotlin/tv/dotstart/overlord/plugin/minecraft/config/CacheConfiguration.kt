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

import java.time.Duration

/**
 * Represents configuration properties which affect the caching behavior of the plugin.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/04/2020
 */
data class CacheConfiguration(

    /**
     * Identifies the amount of time to retain the version manifest.
     *
     * If a given version is not present within the cached version of the manifest, a newer version
     * will be retrieved disregarding this property in order to prevent accidental deployment
     * failures shortly after a new version has been released.
     *
     * Note: When set to zero, no time based caching will be used. Instead, the E-Tag value will be
     * used in order to validate whether a previous version of the manifest remains valid.
     *
     * The default value of this property is 2 days (e.g. 48 hours).
     */
    val manifestCacheDuration: Duration = Duration.ofDays(2))
