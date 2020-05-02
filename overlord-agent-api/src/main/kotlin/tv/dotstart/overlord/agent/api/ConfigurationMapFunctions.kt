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
package tv.dotstart.overlord.agent.api

import com.google.protobuf.ByteString
import tv.dotstart.overlord.agent.api.AgentService.ConfigurationMap
import tv.dotstart.overlord.agent.api.AgentService.ConfigurationValue
import tv.dotstart.overlord.agent.api.AgentService.ConfigurationValue.ValueCase
import java.nio.ByteBuffer

/**
 * Provides functions which simplify the interaction with configuration maps.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */

/**
 * Converts a protocol configuration map into its respective map representation.
 */
fun ConfigurationMap.toMap(): Map<String, Any?> =
    this.pairsList
        ?.map { it.toPair() }
        ?.toMap()
        ?: emptyMap()

/**
 * Converts a single configuration value into its pair representation.
 */
fun ConfigurationValue.toPair(): Pair<String, Any?> {
  val key = this.key
      ?: throw IllegalStateException("Illegal configuration value: Must specify key")

  val value: Any? = when (this.valueCase) {
    ValueCase.DOUBLE_VALUE -> this.doubleValue
    ValueCase.FLOAT_VALUE -> this.floatValue
    ValueCase.INT_VALUE -> this.intValue
    ValueCase.LONG_VALUE -> this.longValue
    ValueCase.BOOLEAN_VALUE -> this.booleanValue
    ValueCase.STRING_VALUE -> this.stringValue
    ValueCase.BYTE_ARRAY_VALUE -> this.byteArrayValue
    ValueCase.MAP_VALUE -> this.mapValue.toMap()
    else -> null
  }

  return key to value
}

/**
 * Converts a map into its protocol representation.
 */
fun Map<String, Any?>.toConfigurationMap(): ConfigurationMap = ConfigurationMap.newBuilder()
    .also { builder ->
      this.map { (key, value) -> key to value }
          .map { it.toConfigurationValue() }
          .forEach {
            builder.addPairs(it)
          }
    }
    .build()

/**
 * Converts a pair into its protocol representation.
 */
fun Pair<String, Any?>.toConfigurationValue(): ConfigurationValue =
    ConfigurationValue.newBuilder()
        .setKey(this.first)
        .also {
          @Suppress("UNCHECKED_CAST") // TODO: Validate Map
          when (val value = this.second) {
            is Double -> it.doubleValue = value
            is Float -> it.floatValue = value
            is Int -> it.intValue = value
            is Long -> it.longValue = value
            is Boolean -> it.booleanValue = value
            is String -> it.stringValue = value
            is ByteArray -> it.byteArrayValue = ByteString.copyFrom(value)
            is ByteBuffer -> it.byteArrayValue = ByteString.copyFrom(value)
            is Map<*, *> -> it.mapValue = (value as Map<String, Any?>).toConfigurationMap()
            else -> {
              if (value != null) {
                throw IllegalStateException("Unsupported value type: ${value::class}")
              }
            }
          }
        }
        .build()
