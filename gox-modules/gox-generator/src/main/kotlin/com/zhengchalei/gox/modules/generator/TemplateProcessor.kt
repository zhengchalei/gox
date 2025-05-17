package com.zhengchalei.gox.modules.generator

import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.nio.file.Files

/**
 * 模板处理器，负责处理模板渲染和文件写入
 */
class TemplateProcessor {
    
    /**
     * 处理模板并写入文件
     *
     * @param template 模板
     * @param outputFile 输出文件
     * @param dataModel 数据模型
     */
    fun process(template: Template, outputFile: File, dataModel: Map<String, Any>) {
        Files.createDirectories(outputFile.parentFile.toPath())
        outputFile.bufferedWriter().use { writer ->
            template.process(dataModel, writer)
        }
    }
} 