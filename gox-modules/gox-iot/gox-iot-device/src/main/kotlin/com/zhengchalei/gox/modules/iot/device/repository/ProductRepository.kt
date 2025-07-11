package com.zhengchalei.gox.modules.iot.device.repository

import com.zhengchalei.gox.modules.iot.device.entity.*
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

/**
 * 产品Repository
 *
 * @author zhengchalei
 */
@Repository
interface ProductRepository : KRepository<Product, Long> {

    fun findPage(
        pageRequest: PageRequest,
        specification: ProductSpecification
    ): Page<ProductListDTO> {
        return this.sql
                .createQuery(Product::class) {
                    where(specification)
                    orderBy(table.id.asc())
                    select(table.fetch(ProductListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

}
