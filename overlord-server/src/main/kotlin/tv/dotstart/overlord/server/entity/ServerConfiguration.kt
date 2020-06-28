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
package tv.dotstart.overlord.server.entity

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdEntity
import kotlinx.dnq.singleton.XdSingletonEntityType
import kotlinx.dnq.xdRequiredDateTimeProp
import kotlinx.dnq.xdStringProp
import org.joda.time.DateTime
import tv.dotstart.overlord.server.entity.ServerConfiguration.Companion.get

/**
 * Persists basic server configuration parameters.
 *
 * This entity is configured as a singleton and is thus primarily accessed via the [get] method
 * provided by its companion object.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
class ServerConfiguration(entity: Entity) : XdEntity(entity) {

  /**
   * Identifies the date and time at which the server has been initially installed.
   */
  var installedAt by xdRequiredDateTimeProp()
    private set

  /**
   * Identifies the currently installed application revision.
   *
   * This value is checked upon application startup in order to perform basic migration tasks and
   * keep a log of updates for debugging purposes.
   *
   * Upon entity creation, this value will be set to null (indicating no previously installed
   * version).
   */
  var currentVersion by xdStringProp()

  companion object : XdSingletonEntityType<ServerConfiguration>() {
    override fun ServerConfiguration.initSingleton() {
      this.installedAt = DateTime.now()
    }
  }
}
