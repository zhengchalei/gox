package com.zhengchalei.gox.framework.jimmer.filter


import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import com.zhengchalei.gox.framework.jimmer.entity.BaseEntityDraft
import org.babyfish.jimmer.kt.isLoaded
import org.babyfish.jimmer.sql.DraftInterceptor
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BaseEntityDraftInterceptor() : DraftInterceptor<BaseEntity, BaseEntityDraft> {

    override fun beforeSave(draft: BaseEntityDraft, original: BaseEntity?) {
        if (!isLoaded(draft, BaseEntity::modifiedTime)) {
            draft.modifiedTime = LocalDateTime.now()
        }

        if (!isLoaded(draft, BaseEntity::modifiedBy)) {
            draft.modifiedBy = if (StpUtil.isLogin()) StpUtil.getLoginIdAsLong() else null

        }

        if (original === null) {
            if (!isLoaded(draft, BaseEntity::createdTime)) {
                draft.createdTime = LocalDateTime.now()
            }

            if (!isLoaded(draft, BaseEntity::createdBy)) {
                draft.createdBy = if (StpUtil.isLogin()) StpUtil.getLoginIdAsLong() else null
            }
        }
    }
}