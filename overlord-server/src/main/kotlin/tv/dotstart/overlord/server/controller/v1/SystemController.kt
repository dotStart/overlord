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
package tv.dotstart.overlord.server.controller.v1

import org.springframework.boot.info.BuildProperties
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tv.dotstart.overlord.server.entity.ServerConfiguration
import tv.dotstart.overlord.server.model.v1.SystemInfo

/**
 * Exposes basic system information such as the application revision and server state.
 *
 * This endpoint is primarily provided to display versioning information within the web interface
 * and is publicly available.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
@RestController
@RequestMapping("/v1")
class SystemController(
    private val buildInfo: BuildProperties?) {

  @GetMapping
  @Transactional(readOnly = true)
  fun info() = SystemInfo(
      this.buildInfo?.version ?: "0.0.0",
      ServerConfiguration.get().installedAt
  )
}
