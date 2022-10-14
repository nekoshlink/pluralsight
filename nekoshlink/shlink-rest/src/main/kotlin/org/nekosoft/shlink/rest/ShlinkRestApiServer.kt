package org.nekosoft.shlink.rest

import org.nekosoft.shlink.ShlinkCoreConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ShlinkCoreConfiguration::class)
class ShlinkRestApiServer {

	companion object {
		const val VERSION_STRING = "1"
	}

}

fun main(args: Array<String>) {
	runApplication<ShlinkRestApiServer>(*args)
}
