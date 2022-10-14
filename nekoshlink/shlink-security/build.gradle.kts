import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {

    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.apache.tomcat.embed:tomcat-embed-core")
    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.aspectj:aspectjweaver")
    implementation("org.hibernate:hibernate-core")
    implementation("org.hibernate:hibernate-envers")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")

    implementation("info.picocli:picocli:4.6.3")

}

tasks.withType<BootJar>().configureEach() {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
