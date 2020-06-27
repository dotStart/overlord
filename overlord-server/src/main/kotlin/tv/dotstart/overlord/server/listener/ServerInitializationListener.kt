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
package tv.dotstart.overlord.server.listener

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.info.BuildProperties
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tv.dotstart.overlord.server.entity.ServerConfiguration
import tv.dotstart.overlord.shared.delegate.log

/**
 * Handles successful server startups.
 *
 * The primary purpose of this listener is to initialize the application for first use as well as to
 * log any version jumps (e.g. updates).
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
@Component
class ServerInitializationListener(
    private val buildInfo: BuildProperties?) : ApplicationListener<ApplicationReadyEvent> {

  companion object {
    private val logger by log()
  }

  @Transactional
  override fun onApplicationEvent(event: ApplicationReadyEvent) {
    val config = ServerConfiguration.get()

    logger.info("Server installation date: ${config.installedAt}")

    if (this.buildInfo != null) {
      if (config.currentVersion != this.buildInfo.version) {
        logger.info("Application version has been altered")
        config.currentVersion = this.buildInfo.version
      }
    } else {
      logger.warn("Build information is unavailable - Application version will not be updated")
    }
  }
}
