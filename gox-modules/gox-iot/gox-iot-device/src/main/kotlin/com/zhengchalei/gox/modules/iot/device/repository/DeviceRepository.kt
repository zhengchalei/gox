package com.zhengchalei.gox.modules.iot.device.repository

import com.zhengchalei.gox.modules.iot.device.entity.*
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository : KRepository<Device, Long> {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        specification: DeviceSpecification
    ): Page<DeviceListDTO> {
        return this.sql
                .createQuery(Device::class) {
                    where(specification)
                    orderBy(table.id.asc())
                    select(table.fetch(DeviceListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun save(dto: DeviceCreateDTO): Device {
        val saveResult = this.save(dto)
        return saveResult.modifiedEntity
    }

}
