import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
}

dependencies {

	api(project(":shlink-security"))

	api("org.jetbrains.kotlin:kotlin-reflect")
	api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	api("org.springframework.boot:spring-boot-starter-web")
	api("org.springframework.boot:spring-boot-starter-data-jpa")
	api("com.fasterxml.jackson.module:jackson-module-kotlin")

	api("org.javers:javers-spring-boot-starter-sql:6.6.5")

	api("com.h2database:h2:2.1.212")

	api("com.maxmind.geoip2:geoip2:3.0.1")

	api("org.hashids:hashids:1.0.3")

	api("info.picocli:picocli:4.6.3")

	api("io.github.g0dkar:qrcode-kotlin-jvm:3.1.0")

	api("io.github.microutils:kotlin-logging-jvm:2.1.23")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

}

tasks.withType<BootJar>().configureEach() {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = true
}
