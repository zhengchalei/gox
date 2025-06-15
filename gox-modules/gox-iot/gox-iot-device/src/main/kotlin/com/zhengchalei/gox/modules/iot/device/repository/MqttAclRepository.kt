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
 * MQTT访问控制Repository
 *
 * @author zhengchalei
 */
@Repository
interface MqttAclRepository : KRepository<MqttAcl, Long> {

    fun findPage(
        pageRequest: PageRequest,
        specification: MqttAclSpecification
    ): Page<MqttAclListDTO> {
        return this.sql
                .createQuery(MqttAcl::class) {
                    where(specification)
                    orderBy(table.id.asc())
                    select(table.fetch(MqttAclListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

}
