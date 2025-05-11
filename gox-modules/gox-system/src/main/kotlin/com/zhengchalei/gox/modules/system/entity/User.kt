package com.zhengchalei.gox.modules.system.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val username: String,
    
    @Column(nullable = false)
    val password: String,
    
    @Column(nullable = false)
    val enabled: Boolean = true,
    
    @Column(name = "created_time")
    val createdTime: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_time")
    val updatedTime: LocalDateTime = LocalDateTime.now()
) 