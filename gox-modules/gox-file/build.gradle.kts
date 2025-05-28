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

    // 文件存储相关依赖
    // MinIO
    implementation("io.minio:minio:latest.release")

    // 阿里云OSS
    implementation("com.aliyun.oss:aliyun-sdk-oss:latest.release")

    // 腾讯云COS
    implementation("com.qcloud:cos_api:latest.release")
    ksp("org.babyfish.jimmer:jimmer-ksp:${rootProject.extra["jimmerVersion"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}