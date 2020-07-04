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
import kotlinx.dnq.*
import org.joda.time.DateTime
import tv.dotstart.overlord.server.entity.security.User

/**
 * Represents an audit log entry regarding a single entity.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
abstract class AbstractAuditLogEntry<A : XdEnumEntity>(entity: Entity) : XdEntity(entity) {

  companion object : XdNaturalEntityType<AbstractAuditLogEntry<*>>()

  /**
   * Identifies the type of action which is represented by this particular log entry.
   */
  abstract var action: A

  /**
   * Identifies the date and time at which this audit log entry has been initially constructed.
   */
  var createdAt by xdRequiredDateTimeProp()
    private set

  /**
   * References the user which caused this log entry to be generated.
   *
   * When a change is created by the system itself (e.g. through an automated task) which has not
   * been caused by an action taken by a known user, this field will simply be omitted (e.g. returns
   * null).
   */
  var user by xdLink0_1(User)

  override fun constructor() {
    this.createdAt = DateTime.now()
  }
}
