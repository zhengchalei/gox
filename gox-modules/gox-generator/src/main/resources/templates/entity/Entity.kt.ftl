package ${packageName}.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/**
 * ${entityComment}
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "${tableName}")
interface ${entityName} : BaseEntity {
    <#list fields as field>
    /**
     * ${field.comment}
     */
    val ${field.name}: ${field.type}<#if field.nullable>?</#if>
    </#list>
}
