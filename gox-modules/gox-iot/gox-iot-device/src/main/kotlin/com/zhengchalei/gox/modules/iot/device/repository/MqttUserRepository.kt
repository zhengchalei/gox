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
 * MQTT用户Repository
 *
 * @author zhengchalei
 */
@Repository
interface MqttUserRepository : KRepository<MqttUser, Long> {

    fun findPage(
        pageRequest: PageRequest,
        specification: MqttUserSpecification
    ): Page<MqttUserListDTO> {
        return this.sql
                .createQuery(MqttUser::class) {
                    where(specification)
                    orderBy(table.id.asc())
                    select(table.fetch(MqttUserListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

}
