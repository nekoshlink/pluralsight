package org.nekosoft.shlink.cli

import org.nekosoft.shlink.ShlinkCoreConfiguration
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess

@SpringBootApplication
@Import(ShlinkCoreConfiguration::class)
class ShlinkCliApplication

fun main(args: Array<String>) {
	try {
		SpringApplicationBuilder(ShlinkCliApplication::class.java)
			.web(WebApplicationType.NONE)
			.run(*args)
	} catch (e: Exception) {
		System.err.println("An internal system error has occurred!")
		System.err.println("${e.javaClass.name}: ${e.message}")
		exitProcess(-10)
	}
}
