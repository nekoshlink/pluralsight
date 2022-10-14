import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {

    api("org.springframework.security:spring-security-core")
    implementation("org.apache.tomcat.embed:tomcat-embed-core")

    implementation("info.picocli:picocli:4.6.3")

}

tasks.withType<BootJar>().configureEach() {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
