plugins {
	kotlin("jvm") version "2.1.20" apply false
	kotlin("plugin.spring") version "2.1.20" apply false
	id("org.springframework.boot") version "3.4.5" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.google.devtools.ksp") version "2.1.20-2.0.1" apply false
}

allprojects {
	group = "com.zhengchalei"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}

}

subprojects {
	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.jetbrains.kotlin.plugin.spring")
		plugin("io.spring.dependency-management")
		plugin("com.google.devtools.ksp")
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
