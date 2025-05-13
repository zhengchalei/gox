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
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    api("org.apache.commons:commons-lang3")
    api("org.mindrot:jbcrypt:0.4")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // 可按需添加更多工具类依赖
} 