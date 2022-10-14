package org.nekosoft.shlink.oauth2server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShlinkOAuth2Server {

    public static final String VERSION_STRING = "1";

    public static void main(String[] args) {
        SpringApplication.run(ShlinkOAuth2Server.class, args);
    }

}
