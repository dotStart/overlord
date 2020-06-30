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
package tv.dotstart.overlord.server.security.token

import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import tv.dotstart.overlord.server.security.AnonymousAuthentication

/**
 * Performs basic conversion of authentication tokens supplied via the HTTP Authorization header.
 *
 * Note: This implementation is currently designed to explicitly handle session tokens but may be
 * altered in the future to permit the use of alternative token types where applicable.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
class TokenAuthenticationConverter : ServerAuthenticationConverter {

  companion object {
    private val sessionTokenPrefix = "Session"
  }

  override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
    val request = exchange.request

    return request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        ?.takeIf { it.startsWith("$sessionTokenPrefix ") }
        ?.removePrefix("$sessionTokenPrefix ")
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?.let { TokenAuthentication(it) }
        ?.let { Mono.just<Authentication>(it) }
        ?: return Mono.just(AnonymousAuthentication)
  }
}
