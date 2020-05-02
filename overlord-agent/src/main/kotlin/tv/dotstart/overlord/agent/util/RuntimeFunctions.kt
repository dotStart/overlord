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
package tv.dotstart.overlord.agent.util

import kotlin.concurrent.thread

/**
 * Provides functions which simplify the interaction with [Runtime].
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */

/**
 * Shorthand for [Runtime.getRuntime]
 */
val runtime: Runtime
  get() = Runtime.getRuntime()

/**
 * Shorthand for [Runtime.addShutdownHook]
 */
fun addShutdownHook(thread: Thread) {
  runtime.addShutdownHook(thread)
}

/**
 * Shorthand for [Runtime.addShutdownHook]
 */
fun addShutdownHook(name: String? = null, block: () -> Unit) {
  addShutdownHook(thread(start = false, name = name, block = block))
}
