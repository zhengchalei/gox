package com.zhengchalei.gox.modules.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zhengchalei.gox.modules.system.entity.dto.RoleCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.RoleDetailDTO
import com.zhengchalei.gox.modules.system.entity.dto.RoleListDTO
import com.zhengchalei.gox.modules.system.entity.dto.RoleUpdateDTO
import com.zhengchalei.gox.modules.system.service.RoleService
import com.zhengchalei.gox.starter.GoxApplication
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@AutoConfigureMockMvc
@SpringBootTest(classes = [GoxApplication::class])
class RoleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var roleService: RoleService

    @Test
    fun `should find role by id`() {
        val roleId = 1L
        Mockito.`when`(roleService.findById(roleId)).thenReturn(
            RoleDetailDTO(
                id = roleId,
                name = "Test Role",
                code = "TEST_ROLE",
                enabled = true,
                description = "Test Description",
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now(),
                permissionIds = listOf(),
                permissions = listOf(),
                routePermissions = listOf(),
                routePermissionIds = listOf()
            )
        )

        mockMvc.perform(get("/api/v1/sys/role/$roleId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(roleId))
            .andExpect(jsonPath("$.name").value("Test Role"))
    }

    @Test
    fun `should delete role by id`() {
        val roleId = 1L
        Mockito.doNothing().`when`(roleService).deleteById(roleId)

        mockMvc.perform(delete("/api/v1/sys/role/$roleId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should create role`() {
        val roleCreateDTO = RoleCreateDTO(
            name = "New Role",
            code = "NEW_ROLE",
            description = "New Role Description",
            permissionIds = listOf(),
        )
        Mockito.doNothing().`when`(roleService).create(roleCreateDTO)

        mockMvc.perform(
            post("/api/v1/sys/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleCreateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should update role`() {
        val roleUpdateDTO = RoleUpdateDTO(
            id = 1L,
            name = "Updated Role",
            code = "UPDATED_ROLE",
            description = "Updated Description",
            permissionIds = listOf()
        )
        Mockito.doNothing().`when`(roleService).update(roleUpdateDTO)

        mockMvc.perform(
            put("/api/v1/sys/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleUpdateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should find role page`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page, size)
        val roleList = listOf(
            RoleListDTO(
                id = 1L,
                name = "Test Role",
                code = "TEST_ROLE",
                enabled = true,
                description = "Test Description",
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now(),
                permissionIds = listOf(),
                routePermissionIds = listOf()
            )
        )
        val pageResult = PageImpl(roleList, pageable, roleList.size.toLong())
        Mockito.`when`(roleService.findPage(Mockito.any(), Mockito.any())).thenReturn(pageResult)

        mockMvc.perform(
            get("/api/v1/sys/role/page")
                .param("page", page.toString())
                .param("size", size.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].name").value("Test Role"))
    }
}