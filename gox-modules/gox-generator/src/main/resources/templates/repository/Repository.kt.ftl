package ${packageName}.repository

import ${packageName}.entity.${entityName}
import ${packageName}.entity.dto.*
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class ${entityName}Repository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        specification: ${entityName}Specification
    ): Page<${entityName}ListDTO> {
        val query = sqlClient.createQuery(${entityName}::class) {
            where(specification)
            orderBy(table.id.asc())
            select(table.fetch(${entityName}ListDTO::class))
        }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findById(id: Long): ${entityName}DetailDTO {
        return sqlClient
            .createQuery(${entityName}::class) {
                where(table.id eq id)
                select(table.fetch(${entityName}DetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("${entityName}不存在")
    }

    fun save(dto: ${entityName}CreateDTO): ${entityName} {
        val saveResult = sqlClient.save(dto)
        if (!saveResult.isModified) {
            log.error("创建失败: {}", dto)
            throw IllegalArgumentException("创建失败")
        }
        return saveResult.modifiedEntity
    }

    fun updateById(dto: ${entityName}UpdateDTO): ${entityName} {
        val saveResult = sqlClient.save(dto)
        if (!saveResult.isModified) {
            log.error("更新失败: {}", dto)
            throw IllegalArgumentException("更新失败")
        }
        return saveResult.modifiedEntity
    }

    fun deleteById(id: Long) {
        val count = sqlClient.executeDelete(${entityName}::class) { where(table.id eq id) }
        if (count == 0) {
            log.error("删除失败: {}", id)
            throw IllegalArgumentException("删除失败")
        }
    }
}
