package com.zhengchalei.gox.modules.generator

fun main(args: Array<String>) {
    val generator = CodeGenerator(
        packageName = "com.zhengchalei.gox.modules.system.entity",
        entityName = "LoginLog",
        tableName = "sys_login_log",
        fields = listOf(
            FieldDefinition("username", "String"),
            FieldDefinition("password", "String"),
            FieldDefinition("status", "Integer"),
            FieldDefinition("ip", "String"),
            FieldDefinition("loginTime", "Date"),
            FieldDefinition("remark", "String")
        )
    )
    generator.generate()
}