<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <el-card class="welcome-card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ userInfo.username }}！</h2>
            <p>今天是个美好的一天，开始您的工作吧</p>
          </div>
          <div class="welcome-actions">
            <el-button type="primary" @click="testConnection">
              <el-icon><Connection /></el-icon>
              测试连接
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon primary">
                <el-icon size="24"><User /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">1,234</div>
                <div class="stats-label">用户总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon success">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">567</div>
                <div class="stats-label">文件总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon warning">
                <el-icon size="24"><UserFilled /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">89</div>
                <div class="stats-label">角色总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stats-card">
            <div class="stats-content">
              <div class="stats-icon danger">
                <el-icon size="24"><Lock /></el-icon>
              </div>
              <div class="stats-info">
                <div class="stats-value">234</div>
                <div class="stats-label">权限总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- API 测试区域 -->
    <div class="api-test-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>API 接口测试</span>
            <el-button type="text" @click="showApiDoc">
              <el-icon><Document /></el-icon>
              查看文档
            </el-button>
          </div>
        </template>
        
        <div class="api-test-content">
          <div class="test-description">
            <p>测试后端 API 接口的连通性和功能。确保后端服务正在 <code>http://localhost:8080</code> 上运行。</p>
          </div>
          
          <div class="test-buttons">
            <el-button 
              type="primary" 
              :loading="testing.connection"
              @click="testConnection"
            >
              <el-icon><Connection /></el-icon>
              测试连接
            </el-button>
            
            <el-button 
              type="success"
              :loading="testing.login"
              @click="testLogin"
            >
              <el-icon><User /></el-icon>
              测试登录
            </el-button>
            
            <el-button 
              type="warning"
              :loading="testing.userInfo"
              @click="testUserInfo"
            >
              <el-icon><UserFilled /></el-icon>
              测试用户信息
            </el-button>
          </div>
          
          <div v-if="testResults.length > 0" class="test-results">
            <h4>测试结果：</h4>
            <div class="results-list">
              <div 
                v-for="(result, index) in testResults" 
                :key="index"
                class="result-item"
                :class="result.status"
              >
                <el-icon>
                  <Check v-if="result.status === 'success'" />
                  <Close v-else-if="result.status === 'error'" />
                  <Warning v-else />
                </el-icon>
                <span>{{ result.message }}</span>
                <small>{{ result.time }}</small>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions-section">
      <el-card>
        <template #header>
          <span>快速操作</span>
        </template>
        
        <div class="quick-actions">
          <el-button-group>
            <el-button @click="$router.push('/system/user')">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button @click="$router.push('/system/role')">
              <el-icon><UserFilled /></el-icon>
              角色管理
            </el-button>
            <el-button @click="$router.push('/system/permission')">
              <el-icon><Lock /></el-icon>
              权限管理
            </el-button>
            <el-button @click="$router.push('/file/upload')">
              <el-icon><Upload /></el-icon>
              文件上传
            </el-button>
          </el-button-group>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Connection,
  User,
  Document,
  UserFilled,
  Lock,
  Check,
  Close,
  Warning,
  Upload
} from '@element-plus/icons-vue'
import { testApi } from '../api/test'
import type { UserDetailDTO } from '../types/api'

// 响应式数据
const userInfo = ref<UserDetailDTO>({
  id: 0,
  username: '用户',
  enabled: true,
  createdTime: '',
  updatedTime: '',
  roleIds: [],
  roles: []
})

const testing = reactive({
  connection: false,
  login: false,
  userInfo: false
})

const testResults = ref<Array<{
  status: 'success' | 'error' | 'warning'
  message: string
  time: string
}>>([])

// 方法
const addTestResult = (status: 'success' | 'error' | 'warning', message: string) => {
  testResults.value.unshift({
    status,
    message,
    time: new Date().toLocaleTimeString()
  })
  
  // 只保留最近10条结果
  if (testResults.value.length > 10) {
    testResults.value.pop()
  }
}

const testConnection = async () => {
  testing.connection = true
  try {
    const success = await testApi.testConnection()
    addTestResult(success ? 'success' : 'error', success ? '后端连接成功' : '后端连接失败')
  } catch (error) {
    addTestResult('error', '连接测试异常')
  } finally {
    testing.connection = false
  }
}

const testLogin = async () => {
  testing.login = true
  try {
    await testApi.testLogin()
    addTestResult('success', '登录接口测试完成')
  } catch (error) {
    addTestResult('warning', '登录测试出现问题')
  } finally {
    testing.login = false
  }
}

const testUserInfo = async () => {
  testing.userInfo = true
  try {
    await testApi.testUserInfo()
    addTestResult('success', '用户信息接口测试完成')
  } catch (error) {
    addTestResult('warning', '用户信息测试出现问题')
  } finally {
    testing.userInfo = false
  }
}

const showApiDoc = () => {
  ElMessage.info('API文档功能待实现')
}

const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo)
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
}

// 生命周期
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.welcome-text h2 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.welcome-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stats-section {
  margin-bottom: 20px;
}

.stats-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stats-icon.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.success {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.danger {
  background: linear-gradient(135deg, #f56c6c 0%, #f093fb 100%);
}

.stats-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stats-label {
  color: #666;
  font-size: 14px;
}

.api-test-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-description {
  margin-bottom: 20px;
}

.test-description code {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.test-buttons {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.test-results {
  margin-top: 20px;
}

.test-results h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.results-list {
  max-height: 200px;
  overflow-y: auto;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  margin-bottom: 4px;
  border-radius: 4px;
  font-size: 14px;
}

.result-item.success {
  background: #f0f9ff;
  color: #059669;
  border: 1px solid #a7f3d0;
}

.result-item.error {
  background: #fef2f2;
  color: #dc2626;
  border: 1px solid #fecaca;
}

.result-item.warning {
  background: #fffbeb;
  color: #d97706;
  border: 1px solid #fed7aa;
}

.result-item small {
  margin-left: auto;
  color: #666;
}

.quick-actions-section {
  margin-bottom: 20px;
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .test-buttons {
    flex-direction: column;
  }
  
  .quick-actions {
    flex-direction: column;
  }
}
</style> 