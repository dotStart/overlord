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
package tv.dotstart.overlord.shared.util

import java.nio.file.Path

/**
 * Provides functions which simplify the interaction with paths.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 01/05/2020
 */

/**
 * Retrieves the extension of a file name.
 *
 * When no extension is present on a given file, null is returned instead. If the extension is
 * empty (e.g. ends in a dot), an empty string is returned.
 */
val Path.fileExtension: String?
  get() {
    val fileName = this.fileName.toString()
    val extensionOffset = fileName.lastIndexOf('.')

    return if (extensionOffset != -1) {
      fileName.substring(extensionOffset + 1)
    } else {
      null
    }
  }
