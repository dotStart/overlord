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

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * Represents an arbitrary token based authentication.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 30/06/2020
 */
data class TokenAuthentication(
    private val token: String,
    private var authenticated: Boolean = false) : Authentication {

  override fun getName() = "Token"
  override fun getCredentials() = this.token
  override fun getPrincipal() = "Token"
  override fun getDetails(): Any? = null
  override fun getAuthorities() = emptyList<GrantedAuthority>()

  override fun isAuthenticated() = this.authenticated

  override fun setAuthenticated(isAuthenticated: Boolean) {
    this.authenticated = isAuthenticated
  }
}
