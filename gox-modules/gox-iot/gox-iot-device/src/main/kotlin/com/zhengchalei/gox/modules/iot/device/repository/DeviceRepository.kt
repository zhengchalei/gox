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
 * 设备Repository
 *
 * @author zhengchalei
 */
@Repository
interface DeviceRepository : KRepository<Device, Long> {

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

}
