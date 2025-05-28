package com.zhengchalei.gox.modules.system.auth.repository

import com.zhengchalei.gox.modules.system.auth.entity.SocialUser
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.stereotype.Repository

/** 社会化用户仓库 */
@Repository
interface SocialUserRepository : KRepository<SocialUser, Long> {

}
