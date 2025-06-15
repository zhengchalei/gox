package com.zhengchalei.gox.modules.file.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoListDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoSpecification
import com.zhengchalei.gox.modules.file.service.FileService
import com.zhengchalei.gox.modules.file.service.impl.LocalFileStorageServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration

/** 文件控制器 */
@Tag(name = "文件管理", description = "文件上传、下载、查询等操作")
@Validated
@RestController
@RequestMapping("/api/file")
class FileController(
    private val fileService: FileService, private val localFileStorageService: LocalFileStorageServiceImpl
) {

    /** 上传文件 */
    @Operation(summary = "上传文件", description = "上传单个文件")
    @PostMapping(value = ["/upload"])
    fun upload(
        @Parameter(description = "文件", required = true) @RequestParam("file") file: MultipartFile
    ): R<FileInfoDetailDTO> {
        val fileInfo = fileService.upload(file)
        return R.success(fileInfo)
    }

    /** 批量上传文件 */
    @Operation(summary = "批量上传文件", description = "批量上传多个文件")
    @PostMapping(value = ["/batch-upload"])
    fun batchUpload(
        @Parameter(description = "文件列表", required = true) @RequestParam("files") files: List<MultipartFile>
    ): R<List<FileInfoDetailDTO>> {
        val fileInfos = fileService.batchUpload(files)
        return R.success(fileInfos)
    }

    /** 分页查询文件 */
    @Operation(summary = "分页查询文件", description = "根据条件分页查询文件")
    @GetMapping(value = ["/page"])
    fun page(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        fileInfoSpecification: FileInfoSpecification
    ): R<Page<FileInfoListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = fileService.findPage(pageRequest, fileInfoSpecification)
        return R.success(pageResult)
    }

    /** 根据ID查询文件详情 */
    @Operation(summary = "根据ID查询文件详情", description = "根据文件ID获取文件详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "文件ID", required = true) @PathVariable id: Long
    ): R<FileInfoDetailDTO> {
        val fileInfo = fileService.findById(id)
        return R.success(fileInfo)
    }

    /** 根据存储名称查询文件详情 */
    @Operation(summary = "根据存储名称查询文件详情", description = "根据文件存储名称获取文件详细信息")
    @GetMapping(value = ["/storage/{fileKey}"])
    fun findByFileKey(
        @Parameter(description = "文件存储名称", required = true) @PathVariable fileKey: String
    ): R<FileInfoDetailDTO> {
        val fileInfo = fileService.findByFileKey(fileKey)
        return R.success(fileInfo)
    }

    /** 根据原始名称查询文件信息 */
    @Operation(summary = "按原始名称搜索文件", description = "根据文件原始名称模糊搜索文件列表")
    @GetMapping(value = ["/search"])
    fun search(
        @Parameter(description = "文件原始名称", required = true) @RequestParam("name") originalName: String
    ): R<List<FileInfoListDTO>> {
        val fileInfos = fileService.findByOriginalName(originalName)
        return R.success(fileInfos)
    }

    /** 获取文件下载URL */
    @Operation(summary = "获取文件下载URL", description = "根据文件fileKey获取文件的下载URL")
    @GetMapping(value = ["/url/{fileKey}"])
    fun getDownloadUrl(
        @Parameter(description = "文件名", required = true) @PathVariable fileKey: String
    ): R<Map<String, String>> {
        val fileInfo = fileService.findByFileKey(fileKey)
        val url = fileService.getDownloadUrl(fileInfo)
        return R.success(mapOf("url" to url))
    }

    /** 获取文件临时访问URL */
    @Operation(summary = "获取文件临时访问URL", description = "根据文件fileKey获取文件的临时访问URL，有效期可自定义")
    @GetMapping(value = ["/temp-url/{fileKey}"])
    fun getTemporaryUrl(
        @Parameter(description = "文件ID", required = true) @PathVariable fileKey: String,
        @Parameter(description = "有效期(分钟)", required = false) @RequestParam(
            "minutes",
            defaultValue = "5"
        ) minutes: Long
    ): R<Map<String, String>> {
        val fileInfo = fileService.findByFileKey(fileKey)
        val duration = Duration.ofMinutes(minutes)
        val url = fileService.getTemporaryUrl(fileInfo, duration)
        return R.success(mapOf("url" to url, "expiresIn" to "${minutes}分钟"))
    }

    /** 下载文件 */
    @Operation(summary = "下载文件", description = "根据文件存储名称下载文件")
    @GetMapping(value = ["/download/{fileKey}"])
    fun download(
        @Parameter(description = "文件存储名称", required = true) @PathVariable fileKey: String
    ): ResponseEntity<Resource> {
        val fileInfo = fileService.findByFileKey(fileKey)

        // 只支持本地文件直接下载，其他类型通过重定向到临时URL
        if (fileInfo.storageType == StorageType.LOCAL) {
            val file = localFileStorageService.getPhysicalPath(fileInfo).toFile()
            val resource = UrlResource(file.toURI())

            val encodedFilename =
                URLEncoder.encode(fileInfo.originalName, StandardCharsets.UTF_8.toString()).replace("+", "%20")

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInfo.mimeType)).header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''$encodedFilename"
            ).body(resource)
        } else {
            // 非本地文件，重定向到临时URL
            val tempUrl = fileService.getTemporaryUrl(fileInfo, Duration.ofMinutes(5))
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, tempUrl).build()
        }
    }

    /** 预览 */
    @GetMapping(value = ["/preview/{fileKey}"])
    fun preview(
        @PathVariable fileKey: String
    ): ResponseEntity<Resource> {
        val fileInfo = fileService.findByFileKey(fileKey)
        if (fileInfo.storageType == StorageType.LOCAL) {
            val file = localFileStorageService.getPhysicalPath(fileInfo).toFile()
            val resource = UrlResource(file.toURI())
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInfo.mimeType)).body(resource)
        } else {
            return ResponseEntity.badRequest().body(null)
        }
    }

    /** 临时访问文件（通过token） */
    @GetMapping(value = ["/temp/{fileKey}"])
    fun tempAccess(
        @PathVariable fileKey: String, @RequestParam token: String, @RequestParam expires: Long
    ): ResponseEntity<Resource> {
        // 验证token和过期时间
        if (System.currentTimeMillis() > expires) {
            return ResponseEntity.badRequest().body(null)
        }

        val fileInfo = fileService.findByFileKey(fileKey)

        // 只支持本地文件，其他类型应该已经生成了对应云服务的临时URL
        if (fileInfo.storageType == StorageType.LOCAL) {
            val file = localFileStorageService.getPhysicalPath(fileInfo).toFile()
            val resource = UrlResource(file.toURI())

            return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInfo.mimeType)).body(resource)
        } else {
            return ResponseEntity.badRequest().body(null)
        }
    }

    /** 删除文件 */
    @Operation(summary = "删除文件", description = "根据文件ID删除文件")
    @DeleteMapping(value = ["/{id}"])
    fun delete(@Parameter(description = "文件ID", required = true) @PathVariable id: Long): R<Void> {
        fileService.delete(id)
        return R.success()
    }

    /** 批量删除文件 */
    @Operation(summary = "批量删除文件", description = "批量删除多个文件")
    @DeleteMapping(value = ["/batch"])
    fun batchDelete(
        @Parameter(description = "文件ID列表", required = true) @RequestBody ids: List<Long>
    ): R<Void> {
        fileService.batchDelete(ids)
        return R.success()
    }
}
