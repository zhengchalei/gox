plugins {
    id("org.springframework.boot") apply false
}

dependencies {
    implementation(project(":gox-framework"))
    implementation(project(":gox-util"))
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
} 