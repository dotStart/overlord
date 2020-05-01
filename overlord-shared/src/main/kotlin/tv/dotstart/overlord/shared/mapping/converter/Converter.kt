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
package tv.dotstart.overlord.shared.mapping.converter

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

/**
 * Provides logic for converting between two values of arbitrary type.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 22/04/2020
 */
interface Converter<I : Any, O> {

  /**
   * Identifies the input type accepted by this converter.
   */
  val inputType: KClass<I>

  operator fun invoke(input: I): O
}

/**
 * Applies converters to all annotated parameters within a given call map.
 */
fun Sequence<Pair<KParameter, Any?>>.applyParameterConverters(): Sequence<Pair<KParameter, Any?>> {
  return map { (key, value) ->
    val annotation = key.findAnnotation<Convert>()

    if (value == null || annotation == null) {
      key to value
    } else {
      @Suppress("UNCHECKED_CAST")
      val converter = annotation.converter.createInstance() as Converter<Any, Any?>
      if (!converter.inputType.isInstance(value)) {
        throw IllegalArgumentException(
            "Illegal argument for parameter ${key.name}: Expected ${converter.inputType} but got ${value::class}")
      }

      key to converter(value)
    }
  }
}
