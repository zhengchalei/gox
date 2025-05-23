package com.zhengchalei.gox.modules.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zhengchalei.gox.modules.system.entity.dto.UserCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserDetailDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserListDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserUpdateDTO
import com.zhengchalei.gox.modules.system.service.UserService
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
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    fun `should find user by username`() {
        val username = "testuser"
        Mockito.`when`(userService.findByUsername(username)).thenReturn(
            UserDetailDTO(
                id = 1L,
                username = username,
                enabled = true,
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now(),
                roles = listOf(),
                roleIds = listOf(),
                nickname = "",
                avatar = "",
                email = "",
                phone = "",
            )
        )

        mockMvc.perform(get("/api/v1/sys/user/username/$username"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value(username))
    }

    @Test
    fun `should find user by id`() {
        val userId = 1L
        Mockito.`when`(userService.findById(userId)).thenReturn(
            UserDetailDTO(
                id = userId,
                username = "testuser",
                enabled = true,
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now(),
                roles = listOf(),
                roleIds = listOf(),
                nickname = "",
                avatar = "",
                email = "",
                phone = "",
            )
        )

        mockMvc.perform(get("/api/v1/sys/user/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(userId))
    }

    @Test
    fun `should delete user by id`() {
        val userId = 1L
        Mockito.doNothing().`when`(userService).deleteById(userId)

        mockMvc.perform(delete("/api/v1/sys/user/$userId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should create user`() {
        val userCreateDTO = UserCreateDTO(username = "newuser")
        Mockito.doNothing().`when`(userService).create(userCreateDTO)

        mockMvc.perform(
            post("/api/v1/sys/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should update user`() {
        val userUpdateDTO = UserUpdateDTO(id = 1L, username = "updateduser", enabled = true)
        Mockito.doNothing().`when`(userService).update(userUpdateDTO)

        mockMvc.perform(
            put("/api/v1/sys/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should find user page`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page, size)
        val userList = listOf(
            UserListDTO(
                id = 1L,
                username = "testuser",
                enabled = true,
                createdTime = LocalDateTime.now(),
                updatedTime = LocalDateTime.now(),
                nickname = "",
                email = "",
                phone = "",
            )
        )
        val pageResult = PageImpl(userList, pageable, userList.size.toLong())
        Mockito.`when`(userService.findPage(Mockito.any(), Mockito.any())).thenReturn(pageResult)

        mockMvc.perform(
            get("/api/v1/sys/user/page")
                .param("page", page.toString())
                .param("size", size.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].username").value("testuser"))
    }
}