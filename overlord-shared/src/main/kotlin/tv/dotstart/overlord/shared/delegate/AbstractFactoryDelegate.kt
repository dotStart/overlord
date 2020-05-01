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
package tv.dotstart.overlord.shared.delegate

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Provides an abstract base for delegates which wish to initialize the content of a property upon
 * first access.
 *
 * Note: This implementation does not provide any thread safety logic and may thus invoke its
 * factory implementation multiple times.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
abstract class AbstractFactoryDelegate<R : Any, T : Any> : ReadOnlyProperty<R, T> {

  private var content: T? = null

  override fun getValue(thisRef: R, property: KProperty<*>): T =
      this.content ?: this.initialize(thisRef, property).also { this.content = it }

  /**
   * Initializes the contents of this delegate.
   */
  protected abstract fun initialize(thisRef: R, property: KProperty<*>): T
}
