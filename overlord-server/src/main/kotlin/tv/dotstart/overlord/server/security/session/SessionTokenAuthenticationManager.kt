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
package tv.dotstart.overlord.server.security.session

import kotlinx.dnq.query.eq
import kotlinx.dnq.query.firstOrNull
import kotlinx.dnq.query.query
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import tv.dotstart.overlord.server.entity.security.Session
import tv.dotstart.overlord.server.error.security.ExpiredCredentialsException
import tv.dotstart.overlord.server.security.token.TokenAuthentication

/**
 * Manages the authentication of session tokens which have previously been parsed by an
 * authentication converter.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 30/06/2020
 */
class SessionTokenAuthenticationManager : ReactiveAuthenticationManager {

  @Transactional
  override fun authenticate(authentication: Authentication): Mono<Authentication> {
    if (authentication !is TokenAuthentication) {
      return Mono.just(authentication)
    }

    val candidate = Session.query(Session::secret eq authentication.credentials)
        .firstOrNull()

    return candidate
        ?.takeIf(Session::isValid)
        ?.let(::SessionAuthentication)
        ?.let { Mono.just<Authentication>(it) }
        ?: Mono.error(ExpiredCredentialsException("Session has expired"))
  }
}
