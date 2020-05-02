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
package tv.dotstart.overlord.plugin.api.server.instance

import reactor.core.publisher.Flux
import java.time.Duration

/**
 * Represents a plugin allocated server instance.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 23/04/2020
 */
interface ServerInstance {

  /**
   * Provides a stream of updates which indicate whether the server instance is alive (represented
   * by the value true) or dead (represented by false).
   *
   * Upon connecting to this flux, the current value will be replayed in order to initialize the
   * state of the listener.
   */
  val alive: Flux<Boolean>

  /**
   * Provides a stream of console messages which were written to the process's standard output.
   */
  val stdout: Flux<String>

  /**
   * Adjusts the server configuration.
   *
   * Unless [tv.dotstart.overlord.shared.plugin.server.extensions.Reloadable] is implemented as
   * well, configurations will be applied upon server restart. For more information refer to the
   * reloadable interface and your respective server implementation.
   *
   * By default, the implementation of this method is left empty to simplify the implementation of
   * server instances which do not provide a configuration.
   */
  fun reconfigure(configuration: Map<String, Any?>) {
  }

  /**
   * Starts a configured server instance.
   */
  fun start()

  /**
   * Performs a graceful shutdown of the server instance.
   *
   * When the given timeout is exceeded during shutdown, the method is expected to raise a
   * [java.util.concurrent.TimeoutException] if the server has not been confirmed to be shut down
   * in its entirety.
   *
   * If the server is not running at the time of invocation, the method is expected to return
   * immediately without altering the state.
   *
   * By default, this implementation will simply invoke [kill] to simplify the implementation of
   * server instances which do not support or require graceful shutdowns.
   *
   * @return true if the server has been stopped, false otherwise.
   */
  fun stop(timeout: Duration): Boolean {
    this.kill()
    return true
  }

  /**
   * Performs a forced shutdown of the server instance.
   *
   * This function is expected to complete as fast as possible without regard for the server state
   * and is typically invoked when the server fails to shutdown within a reasonable amount of time.
   */
  fun kill()
}
