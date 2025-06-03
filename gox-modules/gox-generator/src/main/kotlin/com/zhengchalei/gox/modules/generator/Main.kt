package com.zhengchalei.gox.modules.generator

val projectRoot = System.getProperty("user.dir")

fun main(args: Array<String>) {
    listOf(
        deviceGenerator,
        productGenerator,
    ).forEach {
        it.generate()
    }
}

val deviceGenerator = CodeGenerator(
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
        FieldDefinition("online", "Boolean", false, "设备状态"),
        // online
    )
)

val productGenerator = CodeGenerator(
    projectRoot = projectRoot,
    moduleName = "gox-iot/gox-iot-device",
    packageName = "com.zhengchalei.gox.modules.iot.device",
    entityName = "Product",
    tableName = "iot_product",
    fields = listOf(
        // code
        FieldDefinition("code", "String", false, "产品编号"),
        // name
        FieldDefinition("name", "String", false, "产品名称"),
        // description
        FieldDefinition("description", "String", true, "产品描述"),
        // status
        FieldDefinition("status", "Boolean", false, "产品状态"),
    )
)
