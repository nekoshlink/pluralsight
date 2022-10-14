import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
}

dependencies {

	implementation(project(":shlink-core"))

	// https://docs.spring.io/spring-authorization-server/docs/current/reference/html/getting-started.html
	api("org.springframework.security:spring-security-oauth2-authorization-server:0.3.1")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}

tasks.withType<BootJar>().configureEach() {
	launchScript()
	mainClass.set("org.nekosoft.shlink.oauth2server.ShlinkOAuth2ServerKt")
}

tasks.getByName<Jar>("jar") {
	enabled = false
}
