package com.zhengchalei.gox.modules.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zhengchalei.gox.modules.system.entity.dto.PermissionCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.PermissionUpdateDTO
import com.zhengchalei.gox.modules.system.service.PermissionService
import com.zhengchalei.gox.starter.GoxApplication
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest(classes = [GoxApplication::class])
class PermissionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var permissionService: PermissionService

    @Test
    fun `should find permission by id`() {
        val permissionId = 1L
        Mockito.`when`(permissionService.findById(permissionId)).thenReturn(
            com.zhengchalei.gox.modules.system.entity.dto.PermissionDetailDTO(
                id = permissionId,
                name = "Test Permission",
                code = "TEST_PERMISSION",
                description = "Test Description",
                createdTime = java.time.LocalDateTime.now(),
                updatedTime = java.time.LocalDateTime.now(),
                roleIds = listOf(),
                roles = listOf()
            )
        )

        mockMvc.perform(get("/api/v1/sys/permission/$permissionId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(permissionId))
            .andExpect(jsonPath("$.name").value("Test Permission"))
    }

    @Test
    fun `should delete permission by id`() {
        val permissionId = 1L
        Mockito.doNothing().`when`(permissionService).deleteById(permissionId)

        mockMvc.perform(delete("/api/v1/sys/permission/$permissionId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should create permission`() {
        val permissionCreateDTO = PermissionCreateDTO(
            name = "New Permission",
            code = "NEW_PERMISSION",
            description = "New Permission Description",
            roleIds = listOf()
        )
        Mockito.doNothing().`when`(permissionService).create(permissionCreateDTO)

        mockMvc.perform(
            post("/api/v1/sys/permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionCreateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should update permission`() {
        val permissionUpdateDTO = PermissionUpdateDTO(
            id = 1L,
            name = "Updated Permission",
            code = "UPDATED_PERMISSION",
            description = "Updated Description",
            roleIds = listOf()
        )
        Mockito.doNothing().`when`(permissionService).update(permissionUpdateDTO)

        mockMvc.perform(
            put("/api/v1/sys/permission")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionUpdateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should find permission page`() {
        val page = 1
        val size = 10
        val pageable = org.springframework.data.domain.PageRequest.of(page, size)
        val permissionList = listOf(
            com.zhengchalei.gox.modules.system.entity.dto.PermissionListDTO(
                id = 1L,
                name = "Test Permission",
                code = "TEST_PERMISSION",
                description = "Test Description",
                createdTime = java.time.LocalDateTime.now(),
                updatedTime = java.time.LocalDateTime.now(),
                roleIds = listOf()
            )
        )
        val pageResult =
            org.springframework.data.domain.PageImpl(permissionList, pageable, permissionList.size.toLong())
        Mockito.`when`(permissionService.findPage(Mockito.any(), Mockito.any())).thenReturn(pageResult)

        mockMvc.perform(
            get("/api/v1/sys/permission/page")
                .param("page", page.toString())
                .param("size", size.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].name").value("Test Permission"))
    }
}