package ${packageName}.repository

import ${packageName}.entity.*
import ${packageName}.entity.dto.*
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
interface ${entityName}Repository : KRepository<${entityName}, Long> {

    fun findPage(
        pageRequest: PageRequest,
        specification: ${entityName}Specification
    ): Page<${entityName}ListDTO> {
        return this.sql
                .createQuery(${entityName}::class) {
                    where(specification)
                    orderBy(table.id.asc())
                    select(table.fetch(${entityName}ListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

}
