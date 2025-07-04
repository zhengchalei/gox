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
    implementation(project(":gox-util"))

    ksp("org.babyfish.jimmer:jimmer-ksp:${rootProject.extra["jimmerVersion"]}")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jdbc")
    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.babyfish.jimmer:jimmer-spring-boot-starter:${rootProject.extra["jimmerVersion"]}")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra["springdocVersion"]}")
    // sa-token
    api("cn.dev33:sa-token-spring-boot3-starter:1.43.0")
    api("cn.dev33:sa-token-spring-aop:1.43.0")
    api("cn.dev33:sa-token-redis-template:1.43.0")
    // 通用工具
    api("org.apache.commons:commons-lang3")
    api("commons-io:commons-io:latest.release")
    api("org.apache.commons:commons-pool2")
    // me.zhyd.oauth
    api("me.zhyd.oauth:JustAuth:${rootProject.extra["justAuthVersion"]}")
}