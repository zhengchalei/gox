# Gox文件模块

Gox文件模块是一个支持多种存储方式的文件管理模块，可以实现文件的上传、下载、查询等功能。

## 功能特性

- 支持多种存储方式：本地存储、阿里云OSS、腾讯云COS、MinIO
- 支持文件下载和临时URL生成（支持自定义有效期）
- 按原始文件名查询
- 批量删除和查询
- 文件以日期分文件夹存储，使用UUID命名避免冲突

## 如何创建新模块

创建新的Gox模块需要遵循以下步骤，以保持与项目的统一风格：

### 1. 创建模块目录结构

```bash
mkdir -p gox-modules/新模块名/src/main/{kotlin,dto,resources}/com/zhengchalei/gox/modules/新模块名/{entity,repository,service/impl,controller}
mkdir -p gox-modules/新模块名/src/main/resources/db/migration
mkdir -p gox-modules/新模块名/src/test/kotlin/com/zhengchalei/gox/modules/新模块名
```

### 2. 配置Gradle构建文件

在`gox-modules/新模块名/`目录下创建`build.gradle.kts`文件：

```kotlin
plugins {
    id("org.springframework.boot") apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation(project(":gox-framework"))
    implementation(project(":gox-util"))
    ksp("org.babyfish.jimmer:jimmer-ksp:${rootProject.extra["jimmerVersion"]}")

    // 添加所需依赖
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
```

### 3. 添加到项目配置

在项目根目录的`settings.gradle.kts`文件中添加新模块：

```kotlin
include(
    // 其他已存在的模块
    "gox-modules:新模块名"
)
```

### 4. 创建实体类

在`entity`目录中创建实体类，使用Jimmer的接口式实体：

```kotlin
package com.zhengchalei.gox.modules.新模块名.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "表名")
interface 实体名 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    // 实体属性
    val 属性名: 属性类型

    val createdTime: LocalDateTime
    val updatedTime: LocalDateTime
}
```

### 5. 创建DTO文件

在`src/main/dto`目录下创建`com/zhengchalei/gox/modules/新模块名/entity/实体名.dto`文件：

```
export com.zhengchalei.gox.modules.新模块名.entity.实体名
    -> package com.zhengchalei.gox.modules.新模块名.entity.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

实体名ListDTO {
    id
    // 列表需要显示的字段
    createdTime
    updatedTime
}

实体名DetailDTO {
    id
    // 详情需要显示的所有字段
    createdTime
    updatedTime
}

input 实体名CreateDTO {
    @NotBlank(message = "xxx不能为空")
    属性名
    // 创建时需要的字段
}

input 实体名UpdateDTO {
    @NotNull(message = "ID不能为空")
    id
    @NotBlank(message = "xxx不能为空")
    属性名
    // 更新时需要的字段
}

specification 实体名Specification {
    id
    // 可作为查询条件的字段
    createdTime
    updatedTime
}
```

### 6. 创建Repository

在`repository`目录下创建仓库接口：

```kotlin
package com.zhengchalei.gox.modules.新模块名.repository

import com.zhengchalei.gox.modules.新模块名.entity.实体名
import com.zhengchalei.gox.modules.新模块名.entity.dto.*
import com.zhengchalei.gox.modules.新模块名.entity.id
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class 实体名Repository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        specification: 实体名Specification
    ): Page<实体名ListDTO> {
        val query =
            this.sqlClient.createQuery(实体名::class) {
                where(specification)
                orderBy(table.id.asc())
                select(table.fetch(实体名ListDTO::class))
            }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findById(id: Long): 实体名DetailDTO {
        return this.sqlClient
            .createQuery(实体名::class) {
                where(table.id eq id)
                select(table.fetch(实体名DetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("数据不存在")
    }

    fun save(createDTO: 实体名CreateDTO): 实体名 {
        val saveResult = this.sqlClient.save(createDTO)
        if (!saveResult.isModified) {
            log.error("创建失败: {}", createDTO)
            throw IllegalArgumentException("创建失败")
        }
        return saveResult.modifiedEntity
    }

    fun updateById(updateDTO: 实体名UpdateDTO): 实体名 {
        val saveResult = this.sqlClient.save(updateDTO)
        if (!saveResult.isModified) {
            log.error("更新失败: {}", updateDTO)
            throw IllegalArgumentException("更新失败")
        }
        return saveResult.modifiedEntity
    }

    fun deleteById(id: Long) {
        val deleteCount = this.sqlClient.executeDelete(实体名::class) { 
            where(table.id eq id) 
        }
        if (deleteCount == 0) {
            log.error("删除失败: {}", id)
            throw IllegalArgumentException("删除失败")
        }
    }
}
```

### 7. 创建Service接口

在`service`目录下创建服务接口：

```kotlin
package com.zhengchalei.gox.modules.新模块名.service

import com.zhengchalei.gox.modules.新模块名.entity.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest

interface 实体名Service {

    fun findById(id: Long): 实体名DetailDTO
    
    fun deleteById(id: Long)
    
    fun create(createDTO: 实体名CreateDTO)
    
    fun update(updateDTO: 实体名UpdateDTO)
    
    fun findPage(pageRequest: PageRequest, specification: 实体名Specification): Page<实体名ListDTO>
}
```

### 8. 创建Service实现

在`service/impl`目录下创建服务实现类：

```kotlin
package com.zhengchalei.gox.modules.新模块名.service.impl

import com.zhengchalei.gox.modules.新模块名.entity.dto.*
import com.zhengchalei.gox.modules.新模块名.repository.实体名Repository
import com.zhengchalei.gox.modules.新模块名.service.实体名Service
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class 实体名ServiceImpl(
    private val 实体名Repository: 实体名Repository
) : 实体名Service {

    private val logger = LoggerFactory.getLogger(实体名ServiceImpl::class.java)

    override fun findById(id: Long): 实体名DetailDTO {
        logger.info("查询详情，ID: {}", id)
        return 实体名Repository.findById(id).also {
            logger.info("查询详情成功，ID: {}", id)
        }
    }

    override fun deleteById(id: Long) {
        logger.info("删除数据，ID: {}", id)
        实体名Repository.deleteById(id)
        logger.info("删除数据成功，ID: {}", id)
    }

    override fun create(createDTO: 实体名CreateDTO) {
        logger.info("创建数据")
        val entity = 实体名Repository.save(createDTO)
        logger.info("创建数据成功，ID: {}", entity.id)
    }

    override fun update(updateDTO: 实体名UpdateDTO) {
        logger.info("更新数据，ID: {}", updateDTO.id)
        实体名Repository.updateById(updateDTO)
        logger.info("更新数据成功，ID: {}", updateDTO.id)
    }

    override fun findPage(pageRequest: PageRequest, specification: 实体名Specification): Page<实体名ListDTO> {
        logger.info("分页查询，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return 实体名Repository.findPage(pageRequest, specification).also {
            logger.info("分页查询成功，总记录数: {}", it.totalElements)
        }
    }
}
```

### 9. 创建Controller

在`controller`目录下创建控制器：

```kotlin
package com.zhengchalei.gox.modules.新模块名.controller

import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.新模块名.entity.dto.*
import com.zhengchalei.gox.modules.新模块名.service.实体名Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "模块名称", description = "模块相关操作")
@Validated
@RestController
@RequestMapping("/api/v1/模块路径")
class 实体名Controller(private val 实体名Service: 实体名Service) {

    @Operation(summary = "根据ID查询", description = "通过ID获取详细信息")
    @GetMapping("/{id}")
    fun findById(
        @Parameter(description = "ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<实体名DetailDTO> {
        val entity = 实体名Service.findById(id)
        return ResponseEntity.ok(entity)
    }

    @Operation(summary = "删除", description = "根据ID删除")
    @DeleteMapping("/{id}")
    fun deleteById(
        @Parameter(description = "ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        实体名Service.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建", description = "创建新数据")
    @PostMapping
    fun create(
        @Parameter(description = "创建信息", required = true)
        @Valid @RequestBody createDTO: 实体名CreateDTO
    ): ResponseEntity<Void> {
        实体名Service.create(createDTO)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新", description = "更新数据信息")
    @PutMapping
    fun update(
        @Parameter(description = "更新信息", required = true)
        @Valid @RequestBody updateDTO: 实体名UpdateDTO
    ): ResponseEntity<Void> {
        实体名Service.update(updateDTO)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询", description = "分页查询列表")
    @GetMapping("/page")
    fun findPage(
        @Parameter(description = "页码", required = false)
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "每页数量", required = false)
        @RequestParam(defaultValue = "10") size: Int,
        specification: 实体名Specification,
    ): ResponseEntity<Page<实体名ListDTO>> {
        val pageRequest: PageRequest = PageRequest.of(page, size).oneIndex()
        val pageResult = 实体名Service.findPage(pageRequest, specification)
        return ResponseEntity.ok(pageResult)
    }
}
```

### 10. 创建数据库迁移脚本

在`resources/db/migration`目录下创建数据库迁移脚本，文件名格式为`V版本号__描述.sql`：

```sql
CREATE TABLE `表名` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    -- 其他字段
    `created_time` DATETIME NOT NULL COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表描述';
```

## 学习方法

在开发新模块时，可以参考以下方法进行学习和开发：

1. **学习现有模块**：查看系统模块的实现方式，包括实体设计、DTO定义、仓库和服务实现等
2. **了解Jimmer ORM**：学习Jimmer的接口式实体定义和DTO使用方式
3. **遵循代码规范**：保持与项目一致的命名和风格
4. **参考模板**：使用项目中的模板文件和已有模块作为参考

## 重要代码模板

在创建新模块时，可以参考以下关键文件的模板：

1. **实体类模板**：`gox-modules/gox-generator/src/main/resources/templates/entity/Entity.kt.ftl`
2. **DTO文件模板**：参考`gox-modules/gox-system/src/main/dto/com/zhengchalei/gox/modules/system/entity/Role.dto`
3. **Repository模板**：参考`gox-modules/gox-system/src/main/kotlin/com/zhengchalei/gox/modules/system/repository/RoleRepository.kt`
4. **Service模板**：参考`gox-modules/gox-system/src/main/kotlin/com/zhengchalei/gox/modules/system/service/RoleService.kt`
5. **Controller模板**：参考`gox-modules/gox-system/src/main/kotlin/com/zhengchalei/gox/modules/system/controller/RoleController.kt` 