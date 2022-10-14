import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.7" apply false
	id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
	kotlin("jvm") version "1.6.21" apply false
	kotlin("plugin.spring") version "1.6.21" apply false
	kotlin("plugin.jpa") version "1.6.21" apply false
}

allprojects {

	group = "org.nekosoft.shlink"
	version = "1.0.0"

	tasks.withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "17"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

}

subprojects {

	repositories {
		mavenCentral()
	}

	apply {
		plugin("io.spring.dependency-management")
	}

}
