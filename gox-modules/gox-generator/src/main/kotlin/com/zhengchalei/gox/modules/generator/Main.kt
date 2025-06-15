package com.zhengchalei.gox.modules.generator

val projectRoot = System.getProperty("user.dir")

fun main(args: Array<String>) {
    listOf(
        mqttUserGenerator,
        mqttAclGenerator
    ).forEach {
        it.generate()
    }
}

val mqttUserGenerator = CodeGenerator(
    projectRoot = projectRoot,
    entityComment = "MQTT用户",
    moduleName = "iot",
    modulePath = "gox-iot/gox-iot-device",
    packageName = "com.zhengchalei.gox.modules.iot.device",
    entityName = "MqttUser",
    tableName = "iot_mqtt_user",
    fields = listOf(
        FieldDefinition("isSuperuser", FieldType.BOOLEAN.type, false, "是否超级用户"),
        FieldDefinition("username", FieldType.STRING.type, false, "用户名"),
        FieldDefinition("passwordHash", FieldType.STRING.type, false, "密码"),
        FieldDefinition("salt", FieldType.STRING.type, false, "盐"),
    )
)

// mqtt_acl
val mqttAclGenerator = CodeGenerator(
    projectRoot = projectRoot,
    entityComment = "MQTT访问控制",
    moduleName = "iot",
    modulePath = "gox-iot/gox-iot-device",
    packageName = "com.zhengchalei.gox.modules.iot.device",
    entityName = "MqttAcl",
    tableName = "iot_mqtt_acl",
    fields = listOf(
        FieldDefinition("ipaddress", FieldType.STRING.type, false, "IP地址"),
        FieldDefinition("username", FieldType.STRING.type, false, "用户名"),
        FieldDefinition("clientId", FieldType.STRING.type, false, "客户端ID"),
        FieldDefinition("action", FieldType.STRING.type, false, "操作"),
        FieldDefinition("permission", FieldType.STRING.type, false, "权限"),
    )
)

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
