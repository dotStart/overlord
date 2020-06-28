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
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.link.OnDeletePolicy
import kotlinx.dnq.xdLink0_N

/**
 * Provides a base for entities which maintain a full log of all operations applied to them.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
abstract class AbstractAuditedEntity<L : AbstractAuditLogEntry<*>>(
    logEntryType: XdNaturalEntityType<L>,
    entity: Entity) : AbstractTimestampedEntity(entity) {

  companion object : XdNaturalEntityType<AbstractAuditedEntity<*>>()

  /**
   * Provides a coherent audit log which provides a chronological listing of operations applied to
   * this entity.
   */
  val auditLog by xdLink0_N(logEntryType, onDelete = OnDeletePolicy.CASCADE)
}
