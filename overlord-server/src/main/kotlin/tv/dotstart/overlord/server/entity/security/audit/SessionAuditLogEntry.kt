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
package tv.dotstart.overlord.server.entity.security.audit

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdLink1
import tv.dotstart.overlord.server.entity.audit.AbstractAuditLogEntry

/**
 * Provides a representation for session related audit log entries.
 */
class SessionAuditLogEntry(entity: Entity) : AbstractAuditLogEntry<SessionAuditAction>(entity) {

  companion object : XdNaturalEntityType<SessionAuditLogEntry>()

  override var action by xdLink1(SessionAuditAction)
}
