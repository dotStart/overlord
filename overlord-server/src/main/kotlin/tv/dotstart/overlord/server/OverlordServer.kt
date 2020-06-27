package tv.dotstart.overlord.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
@SpringBootApplication
class OverlordServer

fun main(args: Array<String>) {
  runApplication<OverlordServer>(*args)
}
