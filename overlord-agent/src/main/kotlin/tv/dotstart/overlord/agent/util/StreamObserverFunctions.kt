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
package tv.dotstart.overlord.agent.util

import com.google.protobuf.Empty
import io.grpc.stub.StreamObserver

/**
 * Provides functions which simplify the interaction with stream observers.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 02/05/2020
 */

/**
 * Completes a given stream observer using a single value.
 */
fun <V : Any> StreamObserver<V>.complete(value: V) {
  this.onNext(value)
  this.onCompleted()
}

/**
 * Completes a given stream observer using an empty value.
 */
fun StreamObserver<Empty>.complete() {
  this.complete(Empty.getDefaultInstance())
}

/**
 * Generates a no-op observer.
 */
fun <V : Any> noopObserver(): StreamObserver<V> {
  return object : StreamObserver<V> {
    override fun onNext(p0: V) = Unit
    override fun onError(p0: Throwable?) = Unit
    override fun onCompleted() = Unit
  }
}

/**
 * Provides an observer which ignores all incoming messages and executes a given code block when
 * the stream is completed.
 */
fun completeObserver(block: (Throwable?) -> Unit): StreamObserver<Empty> {
  return object : StreamObserver<Empty> {
    override fun onNext(p0: Empty?) = Unit

    override fun onError(p0: Throwable) {
      block(p0)
    }

    override fun onCompleted() {
      block(null)
    }
  }
}
