package com.zhengchalei.gox.modules.generator

fun main(args: Array<String>) {
    val projectRoot = System.getProperty("user.dir")
    val generator = CodeGenerator(
        projectRoot = projectRoot,
        moduleName = "system",
        packageName = "com.zhengchalei.gox.modules.system",
        entityName = "LoginLog",
        tableName = "sys_login_log",
        fields = listOf(
            FieldDefinition("username", "String"),
            FieldDefinition("password", "String"),
            FieldDefinition("status", "Integer"),
            FieldDefinition("ip", "String"),
            FieldDefinition("remark", "String")
        )
    )
    generator.generate()
}