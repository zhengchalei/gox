package com.zhengchalei.gox.modules.system.auth.filter

import com.zhengchalei.gox.modules.system.auth.service.impl.AuthServiceImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class TokenFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (!request.requestURI.startsWith("/api")) {
            // 放行
            filterChain.doFilter(request, response)
            return
        }
        if (request.requestURI == "/api/public/auth/login") {
            // 放行
            filterChain.doFilter(request, response)
            return
        }

        // 获取 Header/Cookie/Param 中的 token
        val token = request.getHeader("Authorization") ?: request.parameterMap["token"]?.get(0)
        ?: request.cookies.find { it.name == "token" }?.value
        if (token != null) {
            // 验证 token
            val isValid = AuthServiceImpl.Companion.tokenMap.containsKey(token)
            if (isValid) {
                // 放行
                filterChain.doFilter(request, response)
                return
            }
        }

        // 返回 401
        response.status = 401
        response.writer.write("Invalid token")
        return
    }
}