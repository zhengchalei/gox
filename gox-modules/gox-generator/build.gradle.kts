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

    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    testImplementation(project(":gox-starter"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
