package com.zhengchalei.gox.framework.jimmer.filter


import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import com.zhengchalei.gox.framework.jimmer.entity.BaseEntityDraft
import com.zhengchalei.gox.framework.jimmer.entity.modifiedBy
import com.zhengchalei.gox.framework.jimmer.entity.modifiedTime
import com.zhengchalei.gox.framework.jimmer.service.UserService

@Component
class BaseEntityDraftInterceptor(
    private val userService: UserService
) : DraftInterceptor<BaseEntity, BaseEntityDraft> {

    override fun beforeSave(draft: BaseEntityDraft, original: BaseEntity?) {
        if (!isLoaded(draft, BaseEntity::modifiedTime)) {
            draft.modifiedTime = LocalDateTime.now()
        }

        if (!isLoaded(draft, BaseEntity::modifiedBy)) {
            draft.modifiedBy {
                id = userService.currentUserId
            }
        }

        if (original === null) {
            if (!isLoaded(draft, BaseEntity::createdTime)) {
                draft.createdTime = LocalDateTime.now()
            }

            if (!isLoaded(draft, BaseEntity::createdBy)) {
                draft.createdBy {
                    id = userService.currentUserId
                }
            }
        }
    }
}