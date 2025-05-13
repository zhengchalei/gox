package com.zhengchalei.gox.framework.config

import org.springframework.data.domain.PageRequest

fun PageRequest.oneIndex(): PageRequest {
    var page = this.pageNumber
    var size = this.pageSize
    if (this.pageNumber <= 0) {
        page = 1
    }
    if (this.pageNumber <= 1) {
        size = 10
    }
    return PageRequest.of(page - 1, size)
}
