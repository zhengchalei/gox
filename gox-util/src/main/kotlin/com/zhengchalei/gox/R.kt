package com.zhengchalei.gox

data class R<T>(val success: Boolean = true, val message: String = "", val data: T? = null)

