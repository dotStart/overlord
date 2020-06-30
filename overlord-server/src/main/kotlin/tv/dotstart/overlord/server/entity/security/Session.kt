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
package tv.dotstart.overlord.server.entity.security

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.*
import org.joda.time.DateTime
import tv.dotstart.overlord.server.entity.audit.AbstractAuditLogEntry
import tv.dotstart.overlord.server.entity.audit.AbstractAuditedEntity

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
class Session(entity: Entity) : AbstractAuditedEntity<Session.AuditLogEntry>(
    AuditLogEntry, entity) {

  companion object : XdNaturalEntityType<Session>()

  /**
   * Provides a secret with which this session is authenticated.
   *
   * This value is passed along with all requests created by the frontend in order to establish
   * authentication.
   */
  val secret by xdRequiredStringProp(unique = true)

  /**
   * References the user to which this session was allocated. This information is used primarily
   * to establish permissions within the application and to establish
   */
  val owner by xdLink1(User)

  /**
   * Identifies the date and time at which the session is set to expire.
   *
   * This value is automatically moved by the configured session timeout duration every time a token
   * is used to authenticate a user's request. As such, sessions expire automatically when not in
   * use.
   */
  val expiresAt by xdRequiredDateTimeProp()

  /**
   * Identifies the date and time at which the session has been revoked by its user.
   *
   * This value will remain unset until a user explicitly revokes a session at which point the
   * current date and time is recorded and the session is considered invalidated regardless of its
   * current [expiresAt] timestamp.
   *
   * Note: This value may also be used in combination with [expiresAt] to determine the point in
   * time at which the garbage collector is permitted to remove a session from the database.
   */
  val revokedAt by xdDateTimeProp()

  /**
   * Evaluates whether a given session is valid at the current time.
   *
   * @see checkValidity
   */
  val isValid: Boolean
    get() = this.checkValidity(DateTime.now())

  /**
   * Evaluates the validity of this session at a given time.
   *
   * Note: When [revokedAt] is set, sessions are considered invalid regardless of the field's
   * value. As such, [at] is ignored once a session is revoked.
   */
  fun checkValidity(at: DateTime) =
      this.revokedAt == null && this.expiresAt.isAfter(at)

  /**
   * Provides a listing of recognized audited actions on session objects.
   */
  class AuditAction(entity: Entity) : XdEnumEntity(entity) {

    companion object : XdEnumEntityType<AuditAction>() {
      val CREATED by enumField {}
      val REVOKED by enumField {}
    }
  }

  /**
   * Provides a representation for session related audit log entries.
   */
  class AuditLogEntry(entity: Entity) : AbstractAuditLogEntry<AuditAction>(AuditAction, entity) {

    companion object : XdNaturalEntityType<AuditLogEntry>()
  }
}
