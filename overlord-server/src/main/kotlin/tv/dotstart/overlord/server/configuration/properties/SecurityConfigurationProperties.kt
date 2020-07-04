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
package tv.dotstart.overlord.server.configuration.properties

import org.joda.time.Period
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Provides configuration properties which alter the behavior of the security logic.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 04/07/2020
 */
@ConfigurationProperties("overlord.server.security")
class SecurityConfigurationProperties {

  /**
   * Defines the duration for which a session is valid.
   *
   * Note: This value does not define absoltue session validity but rather the period in which it
   * is refreshed by clients.
   */
  var sessionValidityDuration: Period = Period.minutes(2)
}
