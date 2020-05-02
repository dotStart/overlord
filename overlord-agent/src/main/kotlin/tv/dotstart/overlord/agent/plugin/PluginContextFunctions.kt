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
package tv.dotstart.overlord.agent.plugin

import tv.dotstart.overlord.plugin.api.Pluggable

/**
 * Provides functions which simplify interactions with plugin contexts.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 29/04/2020
 */

/**
 * Retrieves a listing of compatible plugin instances from a set of plugin contexts.
 */
fun <P : Pluggable> Iterable<PluginContext>.getInstances(definition: Pluggable.Definition<P>) =
    this
        .flatMap { it.using { definition.available } }
        .toList()

/**
 * Retrieves a listing of compatible plugin instances from a set of plugin contexts.
 */
fun <P : Pluggable> Iterable<PluginContext>.getUniqueInstances(
    definition: Pluggable.Definition<P>) =
    this
        .mapNotNull { it[definition] }
        .toList()
