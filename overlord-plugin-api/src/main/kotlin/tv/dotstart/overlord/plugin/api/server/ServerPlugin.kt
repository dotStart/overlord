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
package tv.dotstart.overlord.plugin.api.server

import tv.dotstart.overlord.plugin.api.Pluggable
import tv.dotstart.overlord.plugin.api.server.instance.ServerFactoryContext
import tv.dotstart.overlord.plugin.api.server.instance.ServerInstanceFactory

/**
 * Provides an entry point for plugin integrations.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/04/2020
 */
interface ServerPlugin : Pluggable {

  /**
   * Provides a human readable identification for this server plugin implementation.
   */
  val displayName: String

  /**
   * Provides a human readable version number for this server plugin implementation.
   */
  val version: String

  /**
   * Retrieves a listing of EULAs which users will be required to accept before the plugin may be
   * executed.
   *
   * This value is used for license prompts upon startup in interactive mode and indicate whether
   * an accept flag is required to start a server instance in batch mode.
   *
   * Note: Typically this listing will reflect the actual license agreements for servers which are
   * downloaded automatically upon server startup.
   */
  val eulaUrls: List<String>
    get() = emptyList()

  /**
   * Provides a factory capable of constructing and provisioning new server instances.
   *
   * This property will only be accessed when the plugin indicates compatibility via the [available]
   * property. As such, dependency related errors may be avoided by initializing this property
   * contents lazily.
   */
  fun createInstanceFactory(ctx: ServerFactoryContext): ServerInstanceFactory

  companion object : Pluggable.Definition<ServerPlugin>() {
    override val type = ServerPlugin::class
  }
}
