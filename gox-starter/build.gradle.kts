plugins {
    id("org.springframework.boot")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation(project(":gox-framework"))
    implementation(project(":gox-util"))
    implementation(project(":gox-modules:gox-system"))
    implementation(project(":gox-modules:gox-file"))
    implementation(project(":gox-modules:gox-auth"))
    implementation(project(":gox-modules:gox-iot"))
    implementation(project(":gox-modules:gox-iot:gox-iot-device"))


    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.liquibase:liquibase-core")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
