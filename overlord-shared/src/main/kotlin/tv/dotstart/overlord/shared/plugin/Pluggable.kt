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
package tv.dotstart.overlord.shared.plugin

import tv.dotstart.overlord.shared.util.contextClassLoader
import java.util.*
import kotlin.reflect.KClass

/**
 * Provides a basis for pluggable interfaces which may be located via the service loader API.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 22/04/2020
 */
interface Pluggable {

  /**
   * Identifies whether this implementation is available within the current execution environment.
   *
   * This property may be used when some JVM implementations or operating systems are unsupported or
   * need to be specially handled.
   *
   * The default implementation will always return true.
   */
  val available: Boolean
    get() = true

  /**
   * Identifies the relative priority of this pluggable implementation.
   *
   * This value is meaningless on its own and will be used to determine relative execution priority
   * when multiple implementations are provided. Higher numbers are thereby more likely to be chosen
   * when multiple instances are available.
   *
   * The default implementation will always return zero (e.g. no special priority).
   */
  val priority: Int
    get() = 0

  /**
   * Provides a base for pluggable definitions (typically companion objects).
   */
  abstract class Definition<P : Pluggable> {

    protected abstract val type: KClass<P>

    /**
     * Retrieves a listing of installed pluggable implementations within the context class loader.
     */
    val installed: List<P>
      get() = ServiceLoader.load(this.type.java,
                                 contextClassLoader)
          .toList()

    /**
     * Retrieves a listing of installed pluggable implementations which are compatible with the
     * current execution environment.
     */
    val available: List<P>
      get() = this.installed
          .filter { it.available }
          .toList()

    /**
     * Retrieves the most optimal installed pluggable implementation.
     */
    val optimal: P?
      get() = this.installed
          .maxBy { it.priority }
  }
}
