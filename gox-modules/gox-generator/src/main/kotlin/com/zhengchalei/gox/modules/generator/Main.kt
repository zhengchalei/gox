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
    entityComment = "设备",
    moduleName = "iot",
    modulePath = "gox-iot/gox-iot-device",
    packageName = "com.zhengchalei.gox.modules.iot.device",
    entityName = "Device",
    tableName = "iot_device",
    fields = listOf(
        // code
        FieldDefinition("code", FieldType.STRING.type, false, "设备编号"),
        // name
        FieldDefinition("name", FieldType.STRING.type, false, "设备名称"),
        // description
        FieldDefinition("description", FieldType.STRING.type, true, "设备描述"),
        // status
        FieldDefinition("online", FieldType.BOOLEAN.type, false, "设备状态"),
        // online
    )
)

val productGenerator = CodeGenerator(
    projectRoot = projectRoot,
    entityComment = "产品",
    moduleName = "iot",
    modulePath = "gox-iot/gox-iot-device",
    packageName = "com.zhengchalei.gox.modules.iot.device",
    entityName = "Product",
    tableName = "iot_product",
    fields = listOf(
        // code
        FieldDefinition("code", FieldType.STRING.type, false, "产品编号"),
        // name
        FieldDefinition("name", FieldType.STRING.type, false, "产品名称"),
        // description
        FieldDefinition("description", FieldType.STRING.type, true, "产品描述"),
        // status
        FieldDefinition("status", FieldType.BOOLEAN.type, false, "产品状态"),
    )
)
