plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":gox-framework"))
    implementation(project(":gox-util"))
    implementation(project(":gox-modules:gox-system"))
    implementation(project(":gox-modules:gox-system:gox-auth"))
    
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-devtools")
    
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    enabled = true
    mainClass.set("com.zhengchalei.gox.starter.GoxApplicationKt")
} 