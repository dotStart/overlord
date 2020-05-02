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
package tv.dotstart.overlord.agent.plugin

import tv.dotstart.overlord.plugin.api.Pluggable
import tv.dotstart.overlord.shared.util.contextClassLoader
import tv.dotstart.overlord.shared.util.using
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.asSequence

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 22/04/2020
 */
class PluginContext(val location: Path) {

  private val classLoader = URLClassLoader(
      "server-plugin-loader",
      arrayOf(this.location.toUri().toURL()),
      contextClassLoader)

  companion object {

    private val jarPathMatcher = FileSystems.getDefault()
        .getPathMatcher("glob:*.jar")

    /**
     * Loads multiple plugins from a given directory.
     *
     * If the specified path points to a non-existing directory, an empty list is returned instead.
     */
    fun loadAll(directory: Path): List<PluginContext> = if (Files.exists(directory)) {
      Files.list(directory).asSequence()
          .filter { jarPathMatcher.matches(it) }
          .map { PluginContext(it) }
          .toList()
    } else {
      emptyList()
    }
  }

  /**
   * Executes a given code block within the Class-Loader context of this plugin.
   */
  fun <R> using(block: (ClassLoader) -> R): R {
    return this.classLoader.using { block(this.classLoader) }
  }

  /**
   * Retrieves the most optimal implementation of a given pluggable definition.
   */
  operator fun <P : Pluggable> get(definition: Pluggable.Definition<P>): P? {
    return this.classLoader.using { definition.optimal }
  }
}
