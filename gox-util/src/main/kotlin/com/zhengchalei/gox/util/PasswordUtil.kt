package com.zhengchalei.gox.util

object PasswordUtil {

    fun encode(password: String): String {
        return password
    }

    fun matches(password: String, encodedPassword: String): Boolean {
        return password == encodedPassword
    }

}