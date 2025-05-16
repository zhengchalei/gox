package com.zhengchalei.gox.modules.generator

fun main(args: Array<String>) {
    val generator = CodeGenerator(
        packageName = "com.zhengchalei.gox.modules.system.entity",
        entityName = "TestEntity",
        tableName = "sys_test",
        fields = listOf(
            FieldDefinition("name", "String"),
            FieldDefinition("code", "String"),
            FieldDefinition("description", "String", true)
        )
    )
    generator.generate()
}