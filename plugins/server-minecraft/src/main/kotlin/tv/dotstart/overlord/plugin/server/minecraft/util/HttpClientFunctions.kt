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
package tv.dotstart.overlord.plugin.server.minecraft.util

import com.fasterxml.jackson.module.kotlin.readValue
import tv.dotstart.overlord.shared.util.createObjectMapper
import tv.dotstart.overlord.shared.util.newHttpRequest
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Provides functions which simplify interactions with the HTTP client.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */

/**
 * Constructs a new HTTP request builder using a standard set of properties.
 */
fun newRequest(uri: URI): HttpRequest.Builder =
    newHttpRequest("Minecraft", "https://github.com/dotStart/Overlord")
        .uri(uri)
        .header("Accept", "application/json,application/json;Charset=UTF-8")

inline fun <reified T> HttpClient.sendForEntity(request: HttpRequest): T {
  val mapper = createObjectMapper()

  val response = this.send(request, HttpResponse.BodyHandlers.ofString())
  check(response.statusCode() == 200) { "Illegal response code: ${response.statusCode()}" }

  return mapper.readValue(response.body())
}

inline fun <reified T> HttpClient.sendForOptionalEntity(request: HttpRequest): T? {
  val mapper = createObjectMapper()

  val response = this.send(request, HttpResponse.BodyHandlers.ofString())

  return when (val statusCode = response.statusCode()) {
    304 -> null
    200 -> mapper.readValue(response.body())
    else -> throw IllegalStateException("Illegal response code: $statusCode")
  }
}

inline fun <reified T> HttpClient.getEntity(uri: URI): T {
  val request = newRequest(uri)
      .GET()
      .build()

  return this.sendForEntity(request)
}
