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
    api("org.apache.commons:commons-lang3:3.12.0")
    api("commons-io:commons-io:2.11.0")
    api("cn.hutool:hutool-all:5.8.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // 可按需添加更多工具类依赖
} 