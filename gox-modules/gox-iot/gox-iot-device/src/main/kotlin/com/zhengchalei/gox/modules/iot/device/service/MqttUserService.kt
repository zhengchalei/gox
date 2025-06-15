package com.zhengchalei.gox.modules.iot.device.service

import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.repository.MqttUserRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class MqttUserService(private val repository: MqttUserRepository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(MqttUserService::class.java)

    fun findById(id: Long): MqttUserDetailDTO {
        logger.info("查询MqttUser，ID: {}", id)
        return repository.sql.findById(MqttUserDetailDTO::class, id).also {
            logger.info("查询MqttUser成功，ID: {}", id)
        } ?: throw RuntimeException("MqttUser不存在")
    }

    fun deleteById(id: Long) {
        logger.info("删除MqttUser，ID: {}", id)
        repository.deleteById(id)
        logger.info("删除MqttUser成功，ID: {}", id)
    }

    fun create(dto: MqttUserCreateDTO) {
        logger.info("创建MqttUser，数据: {}", dto)
        repository.save(dto)
        logger.info("创建MqttUser成功，数据: {}", dto)
    }

    fun update(dto: MqttUserUpdateDTO) {
        logger.info("更新MqttUser，数据: {}", dto)
        repository.save(dto, SaveMode.UPDATE_ONLY)
        logger.info("更新MqttUser成功，数据: {}", dto)
    }

    fun findPage(
        pageRequest: PageRequest,
        specification: MqttUserSpecification
    ): Page<MqttUserListDTO> {
        logger.info("分页查询MqttUser，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return repository.findPage(pageRequest, specification).also {
            logger.info("分页查询MqttUser成功，总记录数: {}", it.totalElements)
        }
    }
}
