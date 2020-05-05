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
