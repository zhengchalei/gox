<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog https://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">


    <changeSet id="创建${entityComment}表" author="zhengchalei">
        <createTable tableName="${tableName}" remarks="系统${entityComment}表">
            <column name="id" type="BIGSERIAL" autoIncrement="true" remarks="主键">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <#list fields as field>
            <column name="${field.name}" type="<#if field.type=="String">Text</#if><#if field.type=="Boolean">Boolean</#if><#if field.type=="LocalDateTime">TIMESTAMP WITH TIME ZONE</#if>" remarks="${entityName}名">
                <constraints nullable="${field.nullable?c}" unique="true"/>
            </column>
            </#list>
            <column name="created_time" type="TIMESTAMP WITH TIME ZONE"
                    defaultValueComputed="CURRENT_TIMESTAMP"
                    remarks="创建时间">
                <constraints nullable="false"/>
            </column>
            <column name="modified_time" type="TIMESTAMP WITH TIME ZONE"
                    defaultValueComputed="CURRENT_TIMESTAMP"
                    remarks="更新时间" />
            <column name="created_by" type="BIGINT" remarks="创建人" />
            <column name="modified_by" type="BIGINT" remarks="更新人" />
        </createTable>
    </changeSet>

</databaseChangeLog>