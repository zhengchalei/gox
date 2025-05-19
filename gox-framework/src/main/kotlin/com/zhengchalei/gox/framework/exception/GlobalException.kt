package com.zhengchalei.gox.framework.exception

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.exception.NotPermissionException
import com.zhengchalei.gox.R
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    /** 全局异常处理 */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): R<Void> {
        return R.error(500, e.message ?: "未知错误")
    }

    /** 未登录异常处理 */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException::class)
    fun handleNotLoginException(e: NotLoginException): R<Void> {
        return R.error(401, e.message ?: "未登录")
    }

    /** 无权限异常处理 */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotPermissionException::class)
    fun handleNotPermissionException(e: NotPermissionException): R<Void> {
        return R.error(403, e.message ?: "无权限")
    }

    /** 未找到异常处理 */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(e: NoSuchElementException): R<Void> {
        return R.error(404, e.message ?: "未找到")
    }
}
