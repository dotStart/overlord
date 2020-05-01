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
package tv.dotstart.overlord.shared.repository

import java.net.URI

/**
 * Provides functions which simplify the interaction with repositories.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 29/04/2020
 */

const val pluginSchemePrefix = "overlord"
const val pluginSchemeSeparator = '+'

/**
 * Retrieves a matching repository (if defined) from a list of known repositories.
 */
fun Iterable<Repository>.getMatching(scheme: String) =
    this.sortedByDescending { it.priority }
        .firstOrNull { it.scheme == scheme }

/**
 * Retrieves a matcing repository (if defined) from a list of known repositories.
 */
fun Iterable<Repository>.getMatching(uri: URI): Repository? {
  val separatorOffset = uri.scheme.indexOf(pluginSchemeSeparator)
  require(separatorOffset != -1) { "Expected Overlord prefix in plugin URI" }
  require(uri.scheme.substring(0, separatorOffset) == pluginSchemePrefix) {
    "Expected Overlord prefix in plugin URI"
  }

  val scheme = uri.scheme.substring(separatorOffset + 1)
  require(scheme.isNotBlank()) { "Expected plugin URI scheme to be non-empty" }

  return this.getMatching(scheme)
}

/**
 * Strips the overlord URI scheme.
 */
fun String.stripOverlordScheme() =
    this.removePrefix("$pluginSchemePrefix$pluginSchemeSeparator")
