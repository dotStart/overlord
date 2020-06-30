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
package tv.dotstart.overlord.server.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler
import tv.dotstart.overlord.server.security.session.SessionTokenAuthenticationManager
import tv.dotstart.overlord.server.security.token.TokenAuthenticationConverter

/**
 * Configures Spring's security module to prevent unauthorized access to endpoints which haven't
 * been explicitly whitelisted.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

  @Bean
  fun authenticationManager(): ReactiveAuthenticationManager = SessionTokenAuthenticationManager()

  @Bean
  fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http
      .csrf().disable()
      .httpBasic().disable()
      .formLogin().disable()
      .logout().disable()
      .also { chain ->
        val entryPoint = HttpStatusServerEntryPoint(HttpStatus.FORBIDDEN)

        val filter = AuthenticationWebFilter(this.authenticationManager())
        filter.setServerAuthenticationConverter(TokenAuthenticationConverter())
        filter.setAuthenticationFailureHandler(
            ServerAuthenticationEntryPointFailureHandler(entryPoint))

        chain.addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
      }
      .authorizeExchange {
        it.pathMatchers("/v1").permitAll()
        it.pathMatchers("/v1/login").permitAll()

        it.anyExchange().authenticated()
      }
      .build()
}
