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
import kotlinx.dnq.XdNaturalEntityType
import kotlinx.dnq.xdBooleanProp
import kotlinx.dnq.xdRequiredStringProp
import kotlinx.dnq.xdStringProp
import org.springframework.security.crypto.bcrypt.BCrypt
import tv.dotstart.overlord.server.entity.audit.AbstractAuditedEntity
import tv.dotstart.overlord.server.entity.security.audit.UserAuditLogEntry

/**
 * Represents a user which has been registered with the system and is able to access the
 * application.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/06/2020
 */
class User(entity: Entity) : AbstractAuditedEntity<UserAuditLogEntry>(
    UserAuditLogEntry, entity) {

  companion object : XdNaturalEntityType<User>()

  /**
   * Provides a login name for this user which will be used as a primary identification factor when
   * logging in.
   *
   * This value is expected to be unique within the application database.
   */
  var name by xdRequiredStringProp(unique = true, trimmed = true)

  /**
   * Provides a display name for this user which is displayed as a replacement to [name] if
   * specified.
   *
   * This field is primarily provided for organizational purposes in systems where many users are
   * given access to game server instances.
   */
  var displayName by xdStringProp(trimmed = true)

  /**
   * Provides a BCrypt password hash which encodes the user's actual password.
   */
  private var passwordHash by xdRequiredStringProp()

  /**
   * Identifies whether this user has been locked (e.g. is not permitted to access the application
   * even with their correct password).
   */
  var locked by xdBooleanProp()

  /**
   * Evaluates whether a given password matches the known hashed version of the user password.
   *
   * This method may take a while to complete as BCrypt is purposefully designed to be slow while
   * generating hashes to prevent brute force attacks on user passwords.
   */
  fun checkPassword(password: String): Boolean = BCrypt.checkpw(password, this.passwordHash)

  /**
   * Replaces the previously set user password with the given value.
   *
   * Note: This method generates a new salt and replaces the password hash accordingly. Similar
   * to [checkPassword], this method may take some time to complete as BCrypt hashing is
   * purposefully expensive.
   */
  fun updatePassword(password: String) {
    val salt = BCrypt.gensalt()
    this.passwordHash = BCrypt.hashpw(password, salt)
  }
}
