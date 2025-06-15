package com.zhengchalei.gox.modules.generator

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File

// 文件生成器接口
interface FileGenerator {
    fun generate(outputDir: String, packagePath: String)
}

// 模板配置工厂
class TemplateConfigFactory {
    companion object {
        fun createConfig(): Configuration {
            return Configuration(Configuration.VERSION_2_3_32).apply {
                setClassLoaderForTemplateLoading(this.javaClass.classLoader, "templates")
                defaultEncoding = "UTF-8"
                templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
            }
        }
    }
}

// 抽象文件生成器，提供通用功能
abstract class AbstractFileGenerator(
    val cfg: Configuration,
    val entityName: String,
    val tableName: String,
    val fields: List<FieldDefinition>,
    val packageName: String,
    val entityComment: String,
    val moduleName: String,
) : FileGenerator {

    private val templateProcessor = TemplateProcessor()

    protected fun getTemplate(templatePath: String): Template {
        return cfg.getTemplate(templatePath)
    }

    protected fun createDataModel(): Map<String, Any> {
        return mapOf(
            "packageName" to packageName,
            "entityName" to entityName,
            "tableName" to tableName,
            "fields" to fields,
            "entityComment" to entityComment,
            "moduleName" to moduleName,
        )
    }

    protected fun processTemplate(template: Template, outputFile: File) {
        templateProcessor.process(template, outputFile, createDataModel())
    }
}

// 实体类生成器
class EntityFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("entity/Entity.kt.ftl")
        val outputFile = File("$outputDir/kotlin/$packagePath/entity/$entityName.kt")
        processTemplate(template, outputFile)
    }
}

// DTO生成器
class DtoFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("dto/Entity.dto.ftl")
        val outputFile = File("$outputDir/dto/$packagePath/entity/$entityName.dto")
        processTemplate(template, outputFile)
    }
}

// 服务类生成器
class ServiceFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("service/Service.kt.ftl")
        val outputFile = File("$outputDir/kotlin/$packagePath/service/${entityName}Service.kt")
        processTemplate(template, outputFile)
    }
}

// 控制器生成器
class ControllerFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("controller/Controller.kt.ftl")
        val outputFile = File("$outputDir/kotlin/$packagePath/controller/${entityName}Controller.kt")
        processTemplate(template, outputFile)
    }
}

// 存储库生成器
class RepositoryFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("repository/Repository.kt.ftl")
        val outputFile = File("$outputDir/kotlin/$packagePath/repository/${entityName}Repository.kt")
        processTemplate(template, outputFile)
    }
}

// 生成 SQL
class SQLFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("sql/liquibase.xml.ftl")
        val outputFile = File("$projectRoot/gox-starter/src/main/resources/db/changelog/${moduleName}/${tableName}.xml")
        processTemplate(template, outputFile)
    }
}

// 生成前端 Vue 文件
class VueFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {

    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("view/index.vue.ftl")
        val outputFile = File("$projectRoot/gox-web/src/views/${moduleName}/${entityName}.vue")
        processTemplate(template, outputFile)
    }
}

// 生成前端 API 文件
class ApiFileGenerator(
    cfg: Configuration,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    packageName: String,
    entityComment: String,
    moduleName: String,
) : AbstractFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName) {
    override fun generate(outputDir: String, packagePath: String) {
        val template = getTemplate("view/api.ts.ftl")
        val outputFile = File("$projectRoot/gox-web/src/api/${moduleName}/${entityName.replaceFirstChar { it.lowercase() }}.ts")
        processTemplate(template, outputFile)
    }
}

// 代码生成协调器
class CodeGenerator(
    projectRoot: String,
    modulePath: String,
    moduleName: String,
    packageName: String,
    entityName: String,
    tableName: String,
    fields: List<FieldDefinition>,
    entityComment: String,
) {
    private val outputDir: String = "$projectRoot/gox-modules/$modulePath/src/main"
    private val packagePath: String = packageName.replace('.', '/')
    private val cfg = TemplateConfigFactory.createConfig()

    private val generators = listOf(
        EntityFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        DtoFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        ServiceFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        ControllerFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        RepositoryFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        SQLFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        VueFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
        ApiFileGenerator(cfg, entityName, tableName, fields, packageName, entityComment, moduleName),
    )

    fun generate() {
        generators.forEach { generator ->
            generator.generate(outputDir, packagePath)
        }
    }
}

data class FieldDefinition(
    val name: String,
    val type: String,
    val nullable: Boolean = false,
    val comment: String? = null
)

enum class FieldType(val type: String) {
    STRING("String"),
    INTEGER("Integer"),
    LONG("Long"),
    DOUBLE("Double"),
    FLOAT("Float"),
    BOOLEAN("Boolean"),
    DATE("Date"),
    TIMESTAMP("Timestamp"),
    BLOB("Blob"),
    CLOB("Clob"),
    ARRAY("Array"),
    STRUCT("Struct"),
    REF("Ref"),
    OTHER("Other"),
    ;

    companion object {
        fun fromType(type: String): FieldType? {
            return entries.find { it.type == type }
        }
    }
}
