plugins {
    id("org.springframework.boot") apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation(project(":gox-framework"))
    implementation(project(":gox-util"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    ksp("org.babyfish.jimmer:jimmer-ksp:${rootProject.extra["jimmerVersion"]}")

    // groovy test + spock
    testImplementation("org.spockframework:spock-core:2.4-M6-groovy-4.0")
    testImplementation("org.spockframework:spock-spring:2.4-M6-groovy-4.0")

    testImplementation(project(":gox-starter"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}