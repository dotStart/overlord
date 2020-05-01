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
package tv.dotstart.overlord.plugin.minecraft

import tv.dotstart.overlord.plugin.minecraft.instance.MinecraftServerInstanceFactory
import tv.dotstart.overlord.shared.plugin.server.ServerPlugin
import tv.dotstart.overlord.shared.plugin.server.instance.ServerFactoryContext

/**
 * Provides Overlord integration with vanilla Minecraft servers.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 28/04/2020
 */
class MinecraftServerPlugin : ServerPlugin {

  override val displayName = "Minecraft (Vanilla)"
  override val version = "0.1.0"

  override val eulaUrls = listOf("https://account.mojang.com/documents/minecraft_eula")

  override fun createInstanceFactory(ctx: ServerFactoryContext) =
      MinecraftServerInstanceFactory(ctx)
}