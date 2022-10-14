package org.nekosoft.shlink.oauth2server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html

@SpringBootApplication
class ShlinkOAuth2Server {

    companion object {
        const val VERSION_STRING = "1"
    }

}

fun main(args: Array<String>) {
    runApplication<ShlinkOAuth2Server>(*args)
}
