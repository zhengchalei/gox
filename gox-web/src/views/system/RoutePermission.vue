<template>
  <div class="route-permission-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>路由权限管理</span>
          <el-button type="primary">
            <el-icon><Plus /></el-icon>
            配置权限
          </el-button>
        </div>
      </template>
      
      <div class="page-content">
        <el-empty description="路由权限管理功能开发中...">
          <el-button type="primary">返回首页</el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-content {
  padding: 40px 0;
}
</style> 