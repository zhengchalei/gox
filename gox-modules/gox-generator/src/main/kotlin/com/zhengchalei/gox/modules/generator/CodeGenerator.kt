package com.zhengchalei.gox.modules.generator

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.nio.file.Files

class CodeGenerator(
    private val outputDir: String = "C:\\Users\\xiaoshitou\\IdeaProjects\\gox\\gox-modules\\gox-system",
    val packageName: String,
    val entityName: String,
    val tableName: String,
    val fields: List<FieldDefinition>,
) {
    private val cfg = Configuration(Configuration.VERSION_2_3_32).apply {
        setClassLoaderForTemplateLoading(this.javaClass.classLoader, "templates")
        defaultEncoding = "UTF-8"
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
    }

    fun generate() {
        generateEntity()
        generateDto()
        generateService()
        generateController()
        generateRepository()
    }

    private fun generateDto() {
        val template = cfg.getTemplate("dto/Entity.dto.ftl")
        val outputFile = File("$outputDir/dto/${packageName.replace('.', '/')}/$entityName.dto")
        writeToFile(outputFile, template)
    }

    private fun generateEntity() {
        val template = cfg.getTemplate("entity/Entity.kt.ftl")
        val outputFile = File("$outputDir/kotlin/${packageName.replace('.', '/')}/$entityName.kt")
        writeToFile(outputFile, template)
    }

    private fun generateService() {
        val template = cfg.getTemplate("service/Service.kt.ftl")
        val outputFile = File("$outputDir/kotlin/${packageName.replace('.', '/')}/${entityName}Service.kt")
        writeToFile(outputFile, template)
    }

    private fun generateController() {
        val template = cfg.getTemplate("controller/Controller.kt.ftl")
        val outputFile = File("$outputDir/kotlin/${packageName.replace('.', '/')}/${entityName}Controller.kt")
        writeToFile(outputFile, template)
    }

    private fun generateRepository() {
        val template = cfg.getTemplate("repository/Repository.kt.ftl")
        val outputFile = File("$outputDir/kotlin/${packageName.replace('.', '/')}/${entityName}Repository.kt")
        writeToFile(outputFile, template)
    }

    private fun writeToFile(outputFile: File, template: Template) {
        Files.createDirectories(outputFile.parentFile.toPath())
        outputFile.bufferedWriter().use { writer ->
            template.process(
                mapOf(
                    "packageName" to packageName,
                    "entityName" to entityName,
                    "tableName" to tableName,
                    "fields" to fields
                ), writer
            )
        }
    }
}

data class FieldDefinition(
    val name: String,
    val type: String,
    val nullable: Boolean = false
)