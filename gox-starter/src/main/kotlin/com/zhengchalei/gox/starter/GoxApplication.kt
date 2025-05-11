package com.zhengchalei.gox.starter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.zhengchalei.gox"])
class GoxApplication

fun main(args: Array<String>) {
	runApplication<GoxApplication>(*args)
} 