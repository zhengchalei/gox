package com.zhengchalei.gox.modules.generator

fun main(args: Array<String>) {
    val projectRoot = System.getProperty("user.dir")
    val generator = CodeGenerator(
        projectRoot = projectRoot,
        moduleName = "gox-iot/gox-iot-device",
        packageName = "com.zhengchalei.gox.modules.iot.device",
        entityName = "Device",
        tableName = "iot_device",
        fields = listOf(
            // code
            FieldDefinition("code", "String", false, "设备编号"),
            // name
            FieldDefinition("name", "String", false, "设备名称"),
            // description
            FieldDefinition("description", "String", true, "设备描述"),
            // status
            FieldDefinition("status", "Integer", false, "设备状态"),
            // online
        )
    )
    generator.generate()
}