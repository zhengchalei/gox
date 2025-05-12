package com.zhengchalei.gox.modules.system.service

import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.repository.UserRepository
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
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    /**
     * 根据 id 查询用户
     */
    fun findById(id: Long): User? {
        return userRepository.findById(id)
    }


}