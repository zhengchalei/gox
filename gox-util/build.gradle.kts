plugins {
    id("org.springframework.boot") apply false
}

dependencies {
    api("org.apache.commons:commons-lang3:3.12.0")
    api("commons-io:commons-io:2.11.0")
    api("cn.hutool:hutool-all:5.8.20")
    // 可按需添加更多工具类依赖
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
} 