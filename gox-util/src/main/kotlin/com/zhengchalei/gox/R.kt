package com.zhengchalei.gox

data class R<T>(
        val success: Boolean = true,
        val message: String = "",
        val data: T? = null,
        val code: Int = 0,
        val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(): R<T> {
            return R(success = true, message = "操作成功", data = null, code = 0)
        }

        fun <T> data(data: T): R<T> {
            return R(success = true, message = "操作成功", data = data, code = 0)
        }

        fun <T> error(): R<T> {
            return R(success = false, message = "操作失败", data = null, code = 1)
        }

        fun <T> error(message: String): R<T> {
            return R(success = false, message = message, data = null, code = 1)
        }

        fun <T> error(code: Int, message: String): R<T> {
            return R(success = false, message = message, data = null, code = code)
        }

        fun <T> error(code: Int, message: String, data: T): R<T> {
            return R(success = false, message = message, data = data, code = code)
        }
    }
}
