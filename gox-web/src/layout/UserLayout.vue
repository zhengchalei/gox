<template>
  <div class="layout-container">
    <el-container class="layout-wrapper">
      <!-- 侧边栏 -->
      <el-aside :width="collapsed ? '80px' : '256px'" class="layout-aside">
        <div class="logo-container">
          <div class="logo">
            <div class="logo-icon">
              <el-icon v-if="!collapsed" size="32"><Grid /></el-icon>
              <el-icon v-else size="24"><Grid /></el-icon>
            </div>
            <div v-if="!collapsed" class="logo-text">
              <div class="logo-title">GOX</div>
              <div class="logo-subtitle">管理系统</div>
            </div>
          </div>
        </div>

        <el-scrollbar class="menu-scrollbar">
          <el-menu
            :default-active="activeMenu"
            :collapse="collapsed"
            :collapse-transition="false"
            class="layout-menu"
            mode="vertical"
            router
          >
            <el-menu-item index="/" class="menu-item">
              <el-icon><Grid /></el-icon>
              <template #title>仪表盘</template>
            </el-menu-item>

            <el-sub-menu index="system" class="sub-menu">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/system/user" class="menu-item">
                <el-icon><User /></el-icon>
                <template #title>用户管理</template>
              </el-menu-item>
              <el-menu-item index="/system/role" class="menu-item">
                <el-icon><UserFilled /></el-icon>
                <template #title>角色管理</template>
              </el-menu-item>
              <el-menu-item index="/system/permission" class="menu-item">
                <el-icon><Lock /></el-icon>
                <template #title>权限管理</template>
              </el-menu-item>
            </el-sub-menu>

            <el-menu-item index="/file" class="menu-item">
              <el-icon><FolderOpened /></el-icon>
              <template #title>文件管理</template>
            </el-menu-item>

            <el-menu-item index="/profile" class="menu-item">
              <el-icon><User /></el-icon>
              <template #title>个人设置</template>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="layout-main">
        <!-- 顶部导航 -->
        <el-header class="layout-header">
          <div class="header-left">
            <el-button type="text" class="collapse-btn" @click="toggleCollapse">
              <el-icon size="18">
                <Fold v-if="!collapsed" />
                <Expand v-else />
              </el-icon>
            </el-button>

            <!-- 面包屑 -->
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item
                v-for="item in breadcrumbList"
                :key="item.path"
                :to="item.path"
              >
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <!-- 搜索 -->
            <div class="search-wrapper">
              <el-input
                v-model="searchValue"
                placeholder="搜索菜单"
                class="search-input"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>

            <!-- 全屏按钮 -->
            <el-tooltip content="全屏" placement="bottom">
              <el-button
                type="text"
                class="header-btn"
                @click="toggleFullscreen"
              >
                <el-icon size="16"><FullScreen /></el-icon>
              </el-button>
            </el-tooltip>

            <!-- 通知 -->
            <el-badge
              :value="notificationCount"
              :hidden="notificationCount === 0"
              class="notification-badge"
            >
              <el-button type="text" class="header-btn">
                <el-icon size="16"><Bell /></el-icon>
              </el-button>
            </el-badge>

            <!-- 用户信息 -->
            <el-dropdown class="user-dropdown" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">
                  {{ userInfo.nickname?.charAt(0).toUpperCase() }}
                </el-avatar>
                <div v-if="!collapsed" class="user-details">
                  <div class="username">{{ userInfo.nickname }}</div>
                  <div class="user-role">{{ getUserRoleName() }}</div>
                </div>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人设置
                  </el-dropdown-item>
                  <el-dropdown-item command="password">
                    <el-icon><Lock /></el-icon>
                    修改密码
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主体内容 -->
        <el-main class="layout-content">
          <div class="content-wrapper">
            <router-view v-slot="{ Component }">
              <transition name="fade-slide" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { loginApi } from "../api/auth/auth";
import type { UserDetailDTO } from "../api/system/user";
import { userLayoutRoutes } from '../router/routes.ts'

const route = useRoute();
const router = useRouter();

// 响应式数据
const collapsed = ref(false);
const searchValue = ref("");
const notificationCount = ref(0);
const userInfo = ref<UserDetailDTO>({
  id: 0,
  username: '',
  nickname: '加载中...',
  avatar: '',
  email: '',
  phone: '',
  enabled: true,
  createdTime: '',
  updatedTime: '',
  roleIds: [],
  roles: []
});

// 计算属性
const activeMenu = computed(() => route.path);

const breadcrumbList = computed(() => {
  const matched = route.matched.filter((item) => item.meta && item.meta.title);
  return matched.map((item) => ({
    path: item.path,
    title: item.meta?.title || "",
  }));
});

// 方法
const toggleCollapse = () => {
  collapsed.value = !collapsed.value;
};

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
  } else {
    document.exitFullscreen();
  }
};

const getUserRoleName = () => {
  if (userInfo.value.roles && userInfo.value.roles.length > 0) {
    return userInfo.value.roles[0].name;
  }
  return "用户";
};

const handleUserCommand = (command: string) => {
  switch (command) {
    case "profile":
      router.push("/profile");
      break;
    case "password":
      // 打开修改密码对话框
      ElMessage.info("修改密码功能待实现");
      break;
    case "logout":
      handleLogout();
      break;
  }
};

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 调用退出登录接口
    await loginApi.logout();

    // 清除本地存储
    localStorage.removeItem("token");
    localStorage.removeItem("userInfo");

    ElMessage.success("退出成功");
    router.push("/login");
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("退出登录失败:", error);
    }
  }
};

const loadUserInfo = async () => {
  try {
    const response = await loginApi.getUserInfo();
    if (response.success) {
      userInfo.value = response.data;
      localStorage.setItem("userInfo", JSON.stringify(response.data));
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
    // 尝试从本地存储获取
    const storedUserInfo = localStorage.getItem("userInfo");
    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo);
      } catch (parseError) {
        console.error("解析用户信息失败:", parseError);
      }
    }
  }
};

// 生命周期
onMounted(() => {
  loadUserInfo();
});
</script>

<style scoped>
/* ==================== 基础布局 ==================== */
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #f5f5f5;
}

.layout-wrapper {
  height: 100%;
  width: 100%;
  background: #f5f5f5;
}

/* ==================== 侧边栏样式 ==================== */
.layout-aside {
  background: #001529;
  transition: width 0.3s ease;
  overflow: hidden;
  box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.08);
  height: 100vh;
  position: relative;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #001529;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  color: #ffffff;
  font-size: 20px;
  font-weight: 600;
  z-index: 2;
  position: relative;
}

.logo-icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.logo-text {
  white-space: nowrap;
  overflow: hidden;
}

.logo-title {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 1px;
}

.logo-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 2px;
}

.menu-scrollbar {
  height: calc(100vh - 64px);
}

.layout-menu {
  border: none;
  background: #001529;
  height: calc(100vh - 64px);
  padding: 8px 0;
}

/* ==================== 主内容区样式 ==================== */
.layout-main {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.layout-header {
  background: #ffffff;
  padding: 0 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  height: 64px;
  flex-shrink: 0;
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 18px;
  margin-right: 20px;
  color: #666;
  padding: 8px;
  transition: all 0.3s ease;
  border-radius: 6px;
  cursor: pointer;
}

.collapse-btn:hover {
  background-color: #f5f5f5;
  color: #409eff;
}

.breadcrumb {
  margin-left: 8px;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-wrapper {
  margin-right: 8px;
}

.search-input {
  width: 240px;
}

.header-btn {
  padding: 8px;
  color: #666;
  font-size: 16px;
  transition: all 0.3s ease;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-btn:hover {
  background-color: #f5f5f5;
  color: #409eff;
}

.notification-badge {
  cursor: pointer;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.3s ease;
  cursor: pointer;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 2px solid #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  line-height: 1.2;
}

.user-role {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.arrow-icon {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
}

.layout-content {
  flex: 1;
  overflow: auto;
  background: #f5f5f5;
  position: relative;
}

.content-wrapper {
  padding: 24px;
  min-height: calc(100vh - 64px - 48px);
  background: #f5f5f5;
}

/* ==================== 页面过渡动画 ==================== */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* ==================== 菜单样式 ==================== */
:deep(.el-menu) {
  border-right: none;
  background: transparent;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.85) !important;
  border-radius: 8px;
  margin: 4px 12px;
  width: calc(100% - 24px);
  transition: all 0.3s ease;
  background-color: transparent !important;
  border: none !important;
  height: 48px;
  line-height: 48px;
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #ffffff !important;
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transform: translateX(4px);
}

:deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: #ffffff;
  border-radius: 0 2px 2px 0;
}

:deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.85) !important;
  border-radius: 8px;
  margin: 4px 12px;
  width: calc(100% - 24px);
  transition: all 0.3s ease;
  background-color: transparent !important;
  height: 48px;
  line-height: 48px;
}

:deep(.el-sub-menu__title:hover) {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #ffffff !important;
  transform: translateX(4px);
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: #409eff !important;
  background-color: rgba(64, 158, 255, 0.1) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: transparent !important;
  margin: 2px 20px;
  width: calc(100% - 40px);
  color: rgba(255, 255, 255, 0.7) !important;
  height: 40px;
  line-height: 40px;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.15) !important;
  color: #ffffff !important;
  transform: translateX(8px);
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transform: translateX(8px);
}

/* ==================== 其他组件样式 ==================== */
:deep(.el-breadcrumb__inner) {
  color: #606266;
  font-weight: 400;
  font-size: 14px;
}

:deep(.el-breadcrumb__inner.is-link) {
  color: #409eff;
  transition: color 0.3s ease;
}

:deep(.el-breadcrumb__inner.is-link:hover) {
  color: #66b1ff;
}

:deep(.el-badge__content) {
  background-color: #f56c6c;
  border: none;
  font-size: 11px;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
}

:deep(.el-scrollbar__bar) {
  opacity: 0.4;
  transition: opacity 0.3s ease;
}

:deep(.el-scrollbar__bar:hover) {
  opacity: 0.8;
}

:deep(.el-scrollbar__thumb) {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
}

:deep(.search-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
}

:deep(.search-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
  background-color: #ffffff;
}

:deep(.search-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
  background-color: #ffffff;
}

:deep(.search-input .el-input__inner) {
  background: transparent;
  color: #606266;
  font-size: 14px;
}

/* ==================== 菜单图标样式 ==================== */
:deep(.el-menu-item .el-icon) {
  color: inherit !important;
  margin-right: 8px;
  font-size: 16px;
  width: 16px;
  transition: all 0.3s ease;
}

:deep(.el-sub-menu__title .el-icon) {
  color: inherit !important;
  margin-right: 8px;
  font-size: 16px;
  width: 16px;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

:deep(.el-sub-menu__title:hover .el-icon) {
  transform: scale(1.1);
}

/* ==================== 响应式设计 ==================== */
@media (max-width: 768px) {
  .layout-header {
    padding: 0 16px;
  }

  .content-wrapper {
    padding: 16px;
  }

  .search-input {
    width: 180px;
  }

  .header-right {
    gap: 8px;
  }

  .user-details {
    display: none;
  }
}

@media (max-width: 480px) {
  .breadcrumb {
    display: none;
  }

  .search-wrapper {
    display: none;
  }

  .header-right {
    gap: 4px;
  }
}

/* ==================== 暗色模式支持 ==================== */
@media (prefers-color-scheme: dark) {
  .layout-header {
    background: #1f1f1f;
    border-bottom-color: #333;
  }

  .collapse-btn {
    color: #ccc;
  }

  .collapse-btn:hover {
    background-color: #333;
    color: #409eff;
  }

  .header-btn {
    color: #ccc;
  }

  .header-btn:hover {
    background-color: #333;
    color: #409eff;
  }

  .user-info:hover {
    background-color: #333;
  }

  .username {
    color: #ffffff;
  }

  .user-role {
    color: #ccc;
  }
}

/* ==================== 动画效果 ==================== */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.layout-aside {
  animation: slideIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.layout-header {
  animation: fadeIn 0.4s ease-out;
}

.layout-content {
  animation: fadeIn 0.5s ease-out;
}
</style> 