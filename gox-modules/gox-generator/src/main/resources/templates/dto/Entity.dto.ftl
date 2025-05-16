export ${packageName}.entity.${entityName}
    -> package ${packageName}.entity.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

${entityName}ListDTO {
    id
    <#list fields as field>
    ${field.name}
    </#list>
    createdTime
    updatedTime
}

${entityName}DetailDTO {
    id
    <#list fields as field>
    ${field.name}
    </#list>
    createdTime
    updatedTime
}

input ${entityName}CreateDTO {
    <#list fields as field>
    <#if !field.nullable>
    @<#if field.type == "String">NotBlank<#else>NotNull</#if>(message = "${field.name}不能为空")
    </#if>
    ${field.name}
    </#list>
}

input ${entityName}UpdateDTO {
    @NotNull(message = "ID不能为空")
    id
    <#list fields as field>
    <#if !field.nullable>
    @<#if field.type == "String">NotBlank<#else>NotNull</#if>(message = "${field.name}不能为空")
    </#if>
    ${field.name}
    </#list>
}

specification ${entityName}Specification {
    <#list fields as field>
    ${field.name}
    </#list>
    createdTime
    updatedTime
}
