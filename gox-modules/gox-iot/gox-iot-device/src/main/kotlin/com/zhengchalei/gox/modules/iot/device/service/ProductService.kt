package com.zhengchalei.gox.modules.iot.device.service

import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.repository.ProductRepository
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class ProductService(private val repository: ProductRepository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(ProductService::class.java)

    fun findById(id: Long): ProductDetailDTO {
        logger.info("查询Product，ID: {}", id)
        return repository.sql.findById(ProductDetailDTO::class, id).also {
            logger.info("查询Product成功，ID: {}", id)
        } ?: throw RuntimeException("Product不存在")
    }

    fun deleteById(id: Long) {
        logger.info("删除Product，ID: {}", id)
        repository.deleteById(id)
        logger.info("删除Product成功，ID: {}", id)
    }

    fun create(dto: ProductCreateDTO) {
        logger.info("创建Product，数据: {}", dto)
        repository.save(dto)
        logger.info("创建Product成功，数据: {}", dto)
    }

    fun update(dto: ProductUpdateDTO) {
        logger.info("更新Product，数据: {}", dto)
        repository.save(dto, SaveMode.UPDATE_ONLY)
        logger.info("更新Product成功，数据: {}", dto)
    }

    fun findPage(
        pageRequest: PageRequest,
        specification: ProductSpecification
    ): Page<ProductListDTO> {
        logger.info("分页查询Product，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return repository.findPage(pageRequest, specification).also {
            logger.info("分页查询Product成功，总记录数: {}", it.totalElements)
        }
    }
}
