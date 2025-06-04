<template>
  <div class="profile">
    <el-card>
      <template #header>
        <span>个人设置</span>
      </template>

      <div class="profile-content">
        <el-form :model="userForm" label-width="120px">
          <el-form-item label="用户名">
            <el-input v-model="userForm.username" disabled />
          </el-form-item>

          <el-form-item label="状态">
            <el-tag :type="userForm.enabled ? 'success' : 'danger'">
              {{ userForm.enabled ? "启用" : "禁用" }}
            </el-tag>
          </el-form-item>

          <el-form-item label="创建时间">
            <span>{{ formatTime(userForm.createdTime) }}</span>
          </el-form-item>

          <el-form-item label="更新时间">
            <span>{{ formatTime(userForm.updatedTime) }}</span>
          </el-form-item>

          <el-form-item label="角色">
            <el-tag
              v-for="role in userForm.roles"
              :key="role.id"
              class="role-tag"
            >
              {{ role.name }}
            </el-tag>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import type { UserDetailDTO } from "../api/system/user";

const userForm = ref<UserDetailDTO>({
  id: 0,
  username: "",
  nickname: "",
  avatar: "",
  email: "",
  phone: "",
  enabled: true,
  createdTime: "",
  updatedTime: "",
  roleIds: [],
  roles: [],
});

const formatTime = (timeStr: string) => {
  if (!timeStr) return "-";
  return new Date(timeStr).toLocaleString();
};

const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem("userInfo");
  if (storedUserInfo) {
    try {
      userForm.value = JSON.parse(storedUserInfo);
    } catch (error) {
      console.error("解析用户信息失败:", error);
    }
  }
};

onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
.profile {
  max-width: 600px;
}

.profile-content {
  padding: 20px 0;
}

.role-tag {
  margin-right: 8px;
}
</style>
