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

/**
 * Provides functions which simplify the interaction with various thread related properties and
 * functions.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */

/**
 * Retrieves the thread instance from which the current method is invoked.
 */
val callingThread: Thread
  get() = Thread.currentThread()

/**
 * Retrieves the identifier of the calling process.
 *
 * This value is highly implementation specific. Refer to [Thread.getId] for more information.
 */
val pid: Long
  get() = callingThread.id

/**
 * Retrieves or adjusts the calling thread's context class loader.
 */
var contextClassLoader: ClassLoader
  get() = callingThread.contextClassLoader
  set(value) {
    callingThread.contextClassLoader = value
  }
