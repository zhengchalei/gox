package com.zhengchalei.gox.util

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {

    fun encode(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun matches(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }

    fun defaultPassword(): String {
        return encode("123456")
    }

}
