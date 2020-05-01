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

import com.fasterxml.jackson.module.kotlin.readValue
import java.net.http.HttpClient
import java.net.http.HttpClient.Redirect
import java.net.http.HttpClient.Version
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Duration

/**
 * Provides functions which simplify the interaction with and creation of HTTP Clients.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 29/04/2020
 */

/**
 * Defines the default connection timeout.
 */
val connectTimeout: Duration = Duration.ofMinutes(1)

/**
 * Defines the default request timeout.
 */
val requestTimeout: Duration = Duration.ofMinutes(2)

/**
 * Creates a new HTTP client using a standardized configuration.
 *
 * Clients created via this method will attempt to establish HTTP 2.x communication with web servers
 * and follow redirects as long as their scheme does not differ.
 */
fun createHttpClient(): HttpClient = HttpClient.newBuilder()
    .version(Version.HTTP_2)
    .followRedirects(Redirect.NORMAL)
    .connectTimeout(connectTimeout)
    .build()

/**
 * Creates a new HTTP request using the default configuration for standard implementations.
 */
fun newHttpRequest(): HttpRequest.Builder = HttpRequest.newBuilder()
    .header("User-Agent", "Overlord (+https://github.com/dotStart/Overlord)")
    .timeout(requestTimeout)

/**
 * Creates a new HTTP request using the default configuration for plugin implementations.
 */
fun newHttpRequest(pluginName: String, pluginUri: String): HttpRequest.Builder =
    HttpRequest.newBuilder()
        .header("User-Agent", "Overlord/$pluginName (+$pluginUri)")
        .timeout(requestTimeout)

/**
 * Fetches a given resource to disk.
 */
fun HttpClient.fetchTo(request: HttpRequest, target: Path) {
  val response = this.send(request, BodyHandlers.ofByteArray())
  check(response.statusCode() == 200) { "Expected code 200 but got ${response.statusCode()}" }

  Files.write(target, response.body(), StandardOpenOption.TRUNCATE_EXISTING)
}

/**
 * Fetches a given JSON entity.
 */
inline fun <reified T : Any> HttpClient.fetchEntity(request: HttpRequest): T? {
  val response = this.send(request, BodyHandlers.ofString())

  return when (val code = response.statusCode()) {
    200 -> createObjectMapper().readValue<T>(response.body())
    204, 404 -> null
    else -> throw IllegalStateException("Expected code 200, 204, or 404 or $code")
  }
}
