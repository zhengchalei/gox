package com.zhengchalei.gox.starter

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableJimmerRepositories(basePackages = ["com.zhengchalei.gox"])
@SpringBootApplication(scanBasePackages = ["com.zhengchalei.gox"])
class GoxApplication

fun main(args: Array<String>) {
    runApplication<GoxApplication>(*args)
} 