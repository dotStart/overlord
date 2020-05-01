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
package tv.dotstart.overlord.shared.mapping

import tv.dotstart.overlord.shared.mapping.converter.applyParameterConverters
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

/**
 * Provides mapping related functions.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 22/04/2020
 */

/**
 * Converts a map into a compatible POJO.
 *
 * Passed types are expected to declare a compatible primary constructor. Values marked as optional
 * may be omitted from the source map. Otherwise an [IllegalArgumentException] is raised instead.
 *
 * This function will also decode nested objects as long as they match the outlined requirements.
 */
fun <T : Any> Map<String, Any?>.toObject(type: KClass<T>): T {
  val constructor = type.primaryConstructor
      ?: throw IllegalArgumentException("No primary constructor declared for ${type.qualifiedName}")

  val callParameterMap = constructor.parameters.asSequence()
      .mapNotNull { param ->
        val annotation = param.findAnnotation<Named>()
        val paramName = annotation?.name ?: param.name

        paramName?.let { param to it }
      }
      .filter { (param, paramName) -> this.containsKey(paramName) || !param.isOptional }
      .map { (param, paramName) ->
        if (!this.containsKey(paramName)) {
          throw IllegalArgumentException("Missing parameter: ${type.qualifiedName}#${param.name}")
        }

        val paramType = param.type

        val value = this[paramName]
        if (value == null) {
          param to null
        } else if (value is Map<*, *> && !paramType.jvmErasure.isSubclassOf(Map::class)) {
          @Suppress("UNCHECKED_CAST")
          param to (value as Map<String, Any>).toObject(paramType.jvmErasure)
        } else {
          param to value
        }
      }
      .applyParameterConverters()
      .toMap()

  callParameterMap.forEach { (param, value) ->
    val paramType = param.type

    if (value == null && !paramType.isMarkedNullable) {
      throw IllegalArgumentException(
          "Illegal value for parameter: ${type.qualifiedName}#${param.name}: Cannot be null")
    }

    if (value != null && !param.type.jvmErasure.isInstance(value)) {
      throw IllegalArgumentException(
          "Illegal value for parameter: ${type.qualifiedName}#${param.name}: Expected $paramType")
    }
  }

  return constructor.callBy(callParameterMap)
}

inline fun <reified T : Any> Map<String, Any?>.toObject() = this.toObject(T::class)
