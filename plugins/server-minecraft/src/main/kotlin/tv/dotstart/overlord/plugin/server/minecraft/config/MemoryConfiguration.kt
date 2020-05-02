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
package tv.dotstart.overlord.plugin.server.minecraft.config

/**
 * Provides configuration parameters related to the memory utilization and garbage collection.
 *
 * These parameter are passed last even if other JVM parameters are given via the command line
 * option and will thus override any prior parameters.
 *
 * Note: All memory values within this configuration are expressed in mibibytes (as implemented by
 * the JVM using the "M" suffix). The minimum for these values is thus 1 MiB. Note, however, that
 * this value is not typically a practical choice as the JVM cannot operate on such small amounts
 * of memory.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/04/2020
 */
data class MemoryConfiguration(

    /**
     * Identifies the minimum amount of memory which is requested by the JVM upon server startup.
     *
     * This amount of memory will be allocated immediately by the JVM and must thus always be
     * available. For long running server instances it is recommended to set [minimum] and [maximum]
     * to the same value (as no dynamic allocation will occur in this case thus potentially reducing
     * issues later on).
     *
     * The default value for this property is 1024 (e.g. 1 GiB of memory).
     */
    val minimum: Long = 1024,

    /**
     * Identifies the maximum amount of memory which may be requested by the JVM at any point in
     * time.
     *
     * If the server exceeds the given amount of memory and is incapable of freeing up previously
     * requested memory, the server will crash.
     *
     * The default value for this property is 2048 (e.g. 2 GiB of memory).
     */
    val maximum: Long = 2048)
