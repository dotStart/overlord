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

import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketAddress

/**
 * Provides functions which simplify interactions with socket addresses.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 06/05/2020
 */

/**
 * Parses an agent socket address.
 */
fun parseSocketAddress(arg: String, defaultPort: Int): SocketAddress {
  if (arg.startsWith("unix:")) {
    return DomainSocketAddress(arg.removePrefix("unix:"))
  }
  val portOffset = arg.lastIndexOf(':')

  val address = when {
    portOffset > 0 -> arg.substring(0, portOffset)
    portOffset == 0 -> null
    else -> arg
  }

  val port = if (portOffset != -1) {
    arg.substring(portOffset + 1).toInt(10)
  } else {
    defaultPort
  }

  val resolvedAddress = address?.let {
    if (it.startsWith("[") && it.endsWith("]")) {
      Inet6Address.getByName(it.removePrefix("[").removeSuffix("]"))
    } else {
      InetAddress.getByName(it)
    }
  }

  return resolvedAddress
      ?.let { InetSocketAddress(it, port) }
      ?: InetSocketAddress(port)
}
