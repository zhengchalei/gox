package ${packageName}.service

import ${packageName}.entity.dto.*
import ${packageName}.repository.${entityName}Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class ${entityName}Service(private val repository: ${entityName}Repository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(${entityName}Service::class.java)

    fun findById(id: Long): ${entityName}DetailDTO {
        logger.info("查询${entityName}，ID: {}", id)
        return repository.sql.findById(${entityName}DetailDTO::class, id).also {
            logger.info("查询${entityName}成功，ID: {}", id)
        } ?: throw RuntimeException("${entityName}不存在")
    }

    fun deleteById(id: Long) {
        logger.info("删除${entityName}，ID: {}", id)
        repository.deleteById(id)
        logger.info("删除${entityName}成功，ID: {}", id)
    }

    fun create(dto: ${entityName}CreateDTO) {
        logger.info("创建${entityName}，数据: {}", dto)
        repository.save(dto)
        logger.info("创建${entityName}成功，数据: {}", dto)
    }

    fun update(dto: ${entityName}UpdateDTO) {
        logger.info("更新${entityName}，数据: {}", dto)
        repository.updateById(dto)
        logger.info("更新${entityName}成功，数据: {}", dto)
    }

    fun findPage(
        pageRequest: PageRequest,
        specification: ${entityName}Specification
    ): Page<${entityName}ListDTO> {
        logger.info("分页查询${entityName}，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return repository.findPage(pageRequest, specification).also {
            logger.info("分页查询${entityName}成功，总记录数: {}", it.totalElements)
        }
    }
}
