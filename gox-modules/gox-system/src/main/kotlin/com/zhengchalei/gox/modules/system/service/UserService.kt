package com.zhengchalei.gox.modules.system.service

import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * 根据用户名查询用户
     */
    fun findByUsername(username: String): UserDetailDTO {
        return userRepository.findByUsername(username)
    }

    /**
     * 根据 id 查询用户
     */
    fun findById(id: Long): UserDetailDTO {
        return userRepository.findById(id)
    }

    /**
     * 根据 id 删除用户
     */
    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }

    /**
     * 创建用户
     */
    fun create(userCreateDTO: UserCreateDTO) {
        userRepository.save(userCreateDTO)
    }

    /**
     * 更新用户
     */
    fun update(userUpdateDTO: UserUpdateDTO) {
        userRepository.updateById(userUpdateDTO)
    }

    /**
     * 分页查询用户
     */
    fun findPage(pageRequest: PageRequest, userSpecification: UserSpecification): Page<UserListDTO> {
        return userRepository.findPage(pageRequest, userSpecification)
    }
}