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

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KProperty

/**
 * Provides a delegate implementation which allocates a logger for its containing type upon first
 * access.
 *
 * If a logger property is declared in a companion object, its parent will be chosen as a basis for
 * instead.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 21/04/2020
 */
class LoggerFactoryDelegate : AbstractFactoryDelegate<Any, Logger>() {

  override fun initialize(thisRef: Any, property: KProperty<*>): Logger {
    val targetType = if (thisRef::class.isCompanion) {
      thisRef::class.java.enclosingClass ?: thisRef::class.java
    } else {
      thisRef::class.java
    }

    return LogManager.getLogger(targetType)
  }
}

/**
 * Shorthand method for assigning an instance of [LoggerFactoryDelegate].
 */
fun log() = LoggerFactoryDelegate()
