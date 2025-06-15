package com.zhengchalei.gox.modules.iot.device.service

import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.repository.MqttAclRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class MqttAclService(private val repository: MqttAclRepository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(MqttAclService::class.java)

    fun findById(id: Long): MqttAclDetailDTO {
        logger.info("查询MqttAcl，ID: {}", id)
        return repository.sql.findById(MqttAclDetailDTO::class, id).also {
            logger.info("查询MqttAcl成功，ID: {}", id)
        } ?: throw RuntimeException("MqttAcl不存在")
    }

    fun deleteById(id: Long) {
        logger.info("删除MqttAcl，ID: {}", id)
        repository.deleteById(id)
        logger.info("删除MqttAcl成功，ID: {}", id)
    }

    fun create(dto: MqttAclCreateDTO) {
        logger.info("创建MqttAcl，数据: {}", dto)
        repository.save(dto, SaveMode.INSERT_ONLY)
        logger.info("创建MqttAcl成功，数据: {}", dto)
    }

    fun update(dto: MqttAclUpdateDTO) {
        logger.info("更新MqttAcl，数据: {}", dto)
        repository.save(dto, SaveMode.UPDATE_ONLY)
        logger.info("更新MqttAcl成功，数据: {}", dto)
    }

    fun findPage(
        pageRequest: PageRequest,
        specification: MqttAclSpecification
    ): Page<MqttAclListDTO> {
        logger.info("分页查询MqttAcl，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return repository.findPage(pageRequest, specification).also {
            logger.info("分页查询MqttAcl成功，总记录数: {}", it.totalElements)
        }
    }
}
