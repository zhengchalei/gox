rootProject.name = "gox"

// 定义所有模块
include(
    "gox-starter",
    "gox-framework",
    "gox-util",
    "gox-modules:gox-system",
    "gox-modules:gox-generator",
    "gox-modules:gox-auth",
    "gox-modules:gox-file",
    "gox-modules:gox-iot",
    "gox-modules:gox-iot:gox-iot-device",
)
