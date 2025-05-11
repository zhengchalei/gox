package com.zhengchalei.gox.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    
    private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun format(dateTime: LocalDateTime): String {
        return dateTime.format(DEFAULT_FORMATTER)
    }
    
    fun parse(dateTimeStr: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER)
    }
} 