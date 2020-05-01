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
package tv.dotstart.overlord.shared.util

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.BufferedReader
import kotlin.concurrent.thread

/**
 * Provides functions which simplify the interaction with processes.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 26/04/2020
 */

/**
 * Creates a supervisor for a given process which re-evaluates whether the process remains alive
 * and completes once the thread exits.
 */
fun supervise(process: Process, pollFrequency: Long = 100): Mono<Int> {
  return Mono.create { sink ->
    thread(name = "process-supervisor-${process.pid()}", isDaemon = true) {
      while (true) {
        if (!process.isAlive) {
          sink.success(process.exitValue())
          return@thread
        }

        Thread.sleep(pollFrequency)
      }
    }
  }
}

fun BufferedReader.lineFlux(): Flux<String> {
  return Flux.push { emitter ->
    thread(isDaemon = true) {
      while (true) {
        val line = this.readLine() ?: break
        emitter.next(line)
      }

      emitter.complete()
    }
  }
}
