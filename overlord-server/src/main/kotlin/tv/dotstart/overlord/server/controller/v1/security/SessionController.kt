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
package tv.dotstart.overlord.server.controller.v1.security

import kotlinx.dnq.query.eq
import kotlinx.dnq.query.firstOrNull
import kotlinx.dnq.query.query
import org.joda.time.DateTime
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tv.dotstart.overlord.server.configuration.properties.SecurityConfigurationProperties
import tv.dotstart.overlord.server.entity.security.Session
import tv.dotstart.overlord.server.entity.security.User
import tv.dotstart.overlord.server.error.security.IllegalCredentialsException
import tv.dotstart.overlord.server.model.v1.security.SessionToken
import tv.dotstart.overlord.server.model.v1.security.UserCredentials

/**
 * Provides session related endpoints.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 01/07/2020
 */
@RestController
@RequestMapping("/v1/security")
class SessionController(private val securityProperties: SecurityConfigurationProperties) {

  /**
   * Allocates a new session for a given user for use with an Overlord compatible client.
   */
  @Transactional
  @PutMapping("/create-session")
  fun createSession(@RequestBody credentials: UserCredentials): SessionToken {
    val user = User.query(User::name eq credentials.username)
        .firstOrNull()
        ?: throw IllegalCredentialsException()

    if (!user.checkPassword(credentials.password)) {
      throw IllegalCredentialsException()
    }

    val session = Session.new {
      this.owner = user
      this.expiresAt = DateTime.now().plus(securityProperties.sessionValidityDuration)
    }

    return SessionToken(session.secret, session.expiresAt)
  }
}
