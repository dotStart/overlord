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
package tv.dotstart.overlord.server.controller.v1.security

import jetbrains.exodus.entitystore.EntityRemovedInDatabaseException
import kotlinx.dnq.util.findById
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tv.dotstart.overlord.server.entity.security.User
import tv.dotstart.overlord.server.error.NoSuchEntityException
import tv.dotstart.overlord.server.model.v1.security.UserInfo
import tv.dotstart.overlord.server.security.session.SessionAuthentication

/**
 * Provides endpoints for the purposes of retrieving or updating users.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 04/07/2020
 */
@RestController
@RequestMapping("/v1/security/user")
class UserController {

  /**
   * Retrieves a user profile for the currently authenticated user.
   */
  @GetMapping
  @Transactional(readOnly = true)
  fun userInfo(authentication: SessionAuthentication): UserInfo {
    val session = authentication.session
    return UserInfo(session.owner)
  }

  /**
   * Retrieves a specific user profile.
   */
  @GetMapping("/{userId}")
  @Transactional(readOnly = true)
  fun userInfo(@PathVariable("userId") userId: String): UserInfo {
    val user = try {
      User.findById(userId)
    } catch (ex: EntityRemovedInDatabaseException) {
      throw NoSuchEntityException("No such user: $userId", ex)
    }

    return UserInfo(user)
  }
}
