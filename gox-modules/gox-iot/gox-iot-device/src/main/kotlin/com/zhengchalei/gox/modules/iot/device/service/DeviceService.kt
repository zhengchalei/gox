package com.zhengchalei.gox.modules.iot.device.service

import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.repository.DeviceRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class DeviceService(private val repository: DeviceRepository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(DeviceService::class.java)

    fun findById(id: Long): DeviceDetailDTO {
        logger.info("查询Device，ID: {}", id)
        return repository.sql.findById(DeviceDetailDTO::class, id).also {
            logger.info("查询Device成功，ID: {}", id)
        } ?: throw RuntimeException("Device不存在")
    }

    fun deleteById(id: Long) {
        logger.info("删除Device，ID: {}", id)
        repository.deleteById(id)
        logger.info("删除Device成功，ID: {}", id)
    }

    fun create(dto: DeviceCreateDTO) {
        logger.info("创建Device，数据: {}", dto)
        repository.save(dto)
        logger.info("创建Device成功，数据: {}", dto)
    }

    fun update(dto: DeviceUpdateDTO) {
        logger.info("更新Device，数据: {}", dto)
        repository.save(dto, SaveMode.UPDATE_ONLY)
        logger.info("更新Device成功，数据: {}", dto)
    }

    fun findPage(
        pageRequest: PageRequest,
        specification: DeviceSpecification
    ): Page<DeviceListDTO> {
        logger.info("分页查询Device，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return repository.findPage(pageRequest, specification).also {
            logger.info("分页查询Device成功，总记录数: {}", it.totalElements)
        }
    }
}
