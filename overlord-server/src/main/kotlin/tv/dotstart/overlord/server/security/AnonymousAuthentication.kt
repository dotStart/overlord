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
package tv.dotstart.overlord.server.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 * Provides a truly anonymous authentication object which is used as a substitute when no compatible
 * header is given.
 *
 * Note that this authentication implementation differs slightly from Spring's standard
 * implementation as it is guaranteed to never be considered a valid authentication object for
 * the purposes of application security.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 01/07/2020
 */
object AnonymousAuthentication : Authentication {

  override fun getName() = "Anonymous"
  override fun getCredentials(): Any? = null
  override fun getPrincipal() = this.name
  override fun getDetails(): Any? = null

  override fun isAuthenticated() = false
  override fun setAuthenticated(isAuthenticated: Boolean) = throw IllegalArgumentException()

  override fun getAuthorities() = emptyList<GrantedAuthority>()
}
