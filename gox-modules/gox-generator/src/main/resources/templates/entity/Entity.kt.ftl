package ${packageName}.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "${tableName}")
interface ${entityName} {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    <#list fields as field>
    val ${field.name}: ${field.type}<#if field.nullable>?</#if>
    </#list>

    val createdTime: LocalDateTime
    val updatedTime: LocalDateTime
}
