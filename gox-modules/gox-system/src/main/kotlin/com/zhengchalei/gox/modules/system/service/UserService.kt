package com.zhengchalei.gox.modules.system.service

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.Consts
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class UserService(private val userRepository: UserRepository) {

    private val logger = org.slf4j.LoggerFactory.getLogger(UserService::class.java)

    /** 根据用户名查询用户 */
    fun findByUsername(username: String): UserDetailDTO {
        logger.info("查询用户，用户名: {}", username)
        return userRepository.findByUsername(username).also {
            logger.info("查询用户成功，用户名: {}", username)
        }
    }

    /** 根据 id 查询用户 */
    fun findById(id: Long): UserDetailDTO {
        logger.info("查询用户，ID: {}", id)
        return userRepository.findById(id).also { logger.info("查询用户成功，ID: {}", id) }
    }

    /** 根据 id 删除用户 */
    fun deleteById(id: Long) {
        logger.info("删除用户，ID: {}", id)
        // 不允许删除管理员
        this.findById(id).roles.firstOrNull { it.name == Consts.ADMIN_ROLE_NAME }?.let {
            throw RuntimeException("不允许删除管理员")
        }
        userRepository.deleteById(id)
        logger.info("删除用户成功，ID: {}", id)
    }

    /** 创建用户 */
    fun create(userCreateDTO: UserCreateDTO) {
        logger.info("创建用户，用户名: {}", userCreateDTO.username)
        userRepository.save(userCreateDTO)
        logger.info("创建用户成功，用户名: {}", userCreateDTO.username)
    }

    /** 更新用户 */
    fun update(userUpdateDTO: UserUpdateDTO) {
        logger.info("更新用户，用户名: {}", userUpdateDTO.username)
        if (StpUtil.getLoginIdAsLong() != Consts.ADMIN_ID) {
            this.findById(userUpdateDTO.id!!)
                    .roles
                    .firstOrNull { it.name == Consts.ADMIN_ROLE_NAME }
                    ?.let { throw RuntimeException("不允许更新管理员") }
        }
        userRepository.updateById(userUpdateDTO)
        logger.info("更新用户成功，用户名: {}", userUpdateDTO.username)
    }

    /** 分页查询用户 */
    fun findPage(
            pageRequest: PageRequest,
            userSpecification: UserSpecification
    ): Page<UserListDTO> {
        logger.info("分页查询用户，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return userRepository.findPage(pageRequest, userSpecification).also {
            logger.info("分页查询用户成功，总记录数: {}", it.totalElements)
        }
    }
}
