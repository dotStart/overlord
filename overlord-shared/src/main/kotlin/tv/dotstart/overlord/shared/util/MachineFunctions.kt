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

import tv.dotstart.overlord.shared.Platform
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Provides functions which simplify interactions with VM functionality.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */

/**
 * Exposes the location of the Java home directory (e.g. the location at which the executing runtime
 * stores its libraries and executables).
 */
val javaHome: Path
  get() {
    val home = System.getProperty("java.home")
        ?: throw IllegalStateException("Unsupported JVM: Failed to retrieve JAVA_HOME");

    return Paths.get(home)
  }

/**
 * Exposes the location at which the executing Java installation stores its binaries.
 */
val javaBinaryDir: Path
  get() = javaHome.resolve("bin")

/**
 * Exposes the location at which the executing Java binary is located.
 */
val javaBinary: Path
  get() {
    val platform = Platform.current
    val fileName = "java${platform.executableExtension}"

    return javaBinaryDir.resolve(fileName)
  }
