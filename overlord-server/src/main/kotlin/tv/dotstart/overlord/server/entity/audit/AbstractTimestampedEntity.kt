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
package tv.dotstart.overlord.server.entity.audit

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdEntity
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdRequiredDateTimeProp
import org.joda.time.DateTime

/**
 * Provides a base for entities which are aware of their respective time of creation as well as last
 * update.
 *
 * Note: This implementation automatically keeps track of the creation and update dates and does
 * not require any additional steps within its implementations.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
abstract class AbstractTimestampedEntity(entity: Entity) : XdEntity(entity) {

  companion object : XdNaturalEntityType<AbstractTimestampedEntity>()

  /**
   * Identifies the date and time at which the entity has been initially created.
   */
  var createdAt by xdRequiredDateTimeProp()
    private set

  /**
   * Identifies the date and time at which the entity has last been flushed.
   */
  var updatedAt by xdRequiredDateTimeProp()
    private set

  override fun constructor() {
    this.createdAt = DateTime.now()
  }

  override fun beforeFlush() {
    this.updatedAt = DateTime.now()
  }
}
