<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- 左侧图片区域 -->
      <div class="login-left">
        <div class="brand-section">
          <div class="brand-logo">
            <el-icon size="48" color="#ffffff"><Grid /></el-icon>
          </div>
          <h1 class="brand-title">GOX 管理系统</h1>
          <p class="brand-subtitle">现代化的后台管理解决方案</p>
        </div>
        
        <div class="illustration">
          <div class="geometric-shape shape-1"></div>
          <div class="geometric-shape shape-2"></div>
          <div class="geometric-shape shape-3"></div>
          <div class="floating-elements">
            <div class="element element-1"></div>
            <div class="element element-2"></div>
            <div class="element element-3"></div>
          </div>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <el-icon><Shield /></el-icon>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <el-icon><Lightning /></el-icon>
            <span>高效快速</span>
          </div>
          <div class="feature-item">
            <el-icon><Star /></el-icon>
            <span>现代化设计</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2>欢迎登录</h2>
            <p>请输入您的账号信息</p>
          </div>
          
          <el-form
            ref="formRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
                class="form-input"
              />
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                prefix-icon="Lock"
                show-password
                class="form-input"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            
            <el-form-item class="login-options">
              <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
            </el-form-item>
            
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-button"
                :loading="loading"
                @click="handleLogin"
              >
                <span v-if="!loading">登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-footer">
            <p>© 2024 GOX 管理系统. All rights reserved.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm } from 'element-plus'
import { loginApi } from '../api'
import type { LoginRequest } from '../types/api'

const router = useRouter()

// 表单引用
const formRef = ref<InstanceType<typeof ElForm>>()

// 登录表单
const loginForm = reactive<LoginRequest>({
  username: '',
  password: '',
  rememberMe: false
})

// 加载状态
const loading = ref(false)

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 50, message: '用户名长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 1, max: 50, message: '密码长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  
  try {
    const response = await loginApi.login(loginForm)
    
    if (response.success) {
      // 保存token
      localStorage.setItem('token', response.data.token)
      
      // 获取用户信息
      const userInfoResponse = await loginApi.getUserInfo()
      if (userInfoResponse.success) {
        localStorage.setItem('userInfo', JSON.stringify(userInfoResponse.data))
      }
      
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    // API拦截器已经处理了错误消息
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  width: 100vw;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-wrapper {
  width: 100vw;
  height: 100vh;
  background: white;
  display: flex;
  overflow: hidden;
}

/* 左侧图片区域 */
.login-left {
  flex: 1.2;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 60px;
  color: white;
}

.brand-section {
  z-index: 2;
  position: relative;
}

.brand-logo {
  margin-bottom: 24px;
}

.brand-title {
  font-size: 36px;
  font-weight: bold;
  margin: 0 0 12px 0;
  line-height: 1.2;
}

.brand-subtitle {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
  font-weight: 300;
}

.illustration {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  height: 400px;
}

.geometric-shape {
  position: absolute;
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.1);
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.shape-2 {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.15);
  top: 60%;
  right: 30%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.08);
  bottom: 20%;
  left: 40%;
  animation-delay: 4s;
}

.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
}

.element {
  position: absolute;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  animation: drift 8s linear infinite;
}

.element-1 {
  width: 20px;
  height: 20px;
  top: 30%;
  left: 10%;
  animation-delay: 0s;
}

.element-2 {
  width: 16px;
  height: 16px;
  top: 70%;
  right: 20%;
  animation-delay: 3s;
}

.element-3 {
  width: 24px;
  height: 24px;
  bottom: 40%;
  right: 10%;
  animation-delay: 6s;
}

.features {
  display: flex;
  gap: 32px;
  z-index: 2;
  position: relative;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

/* 右侧登录表单 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: #fafbfc;
}

.login-box {
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #1a202c;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  color: #718096;
  margin: 0;
  font-size: 16px;
}

.login-form {
  margin-bottom: 32px;
}

.login-form .el-form-item {
  margin-bottom: 24px;
}

.form-input {
  border-radius: 8px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-options .el-form-item__content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-password {
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.login-footer {
  text-align: center;
  margin-top: 32px;
}

.login-footer p {
  color: #a0aec0;
  font-size: 14px;
  margin: 0;
}

/* 动画 */
@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes drift {
  0% {
    transform: translateX(0px) rotate(0deg);
  }
  25% {
    transform: translateX(20px) rotate(90deg);
  }
  50% {
    transform: translateX(0px) rotate(180deg);
  }
  75% {
    transform: translateX(-20px) rotate(270deg);
  }
  100% {
    transform: translateX(0px) rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .login-wrapper {
    width: 90%;
    height: auto;
    flex-direction: column;
  }
  
  .login-left {
    flex: none;
    height: 300px;
    padding: 40px;
  }
  
  .brand-title {
    font-size: 28px;
  }
  
  .brand-subtitle {
    font-size: 16px;
  }
  
  .features {
    gap: 16px;
    justify-content: center;
  }
  
  .illustration {
    display: none;
  }
}

@media (max-width: 768px) {
  .login-container {
    padding: 20px;
  }
  
  .login-wrapper {
    width: 100%;
    border-radius: 12px;
  }
  
  .login-left {
    height: 200px;
    padding: 30px;
  }
  
  .login-right {
    padding: 40px 30px;
  }
  
  .brand-title {
    font-size: 24px;
  }
  
  .features {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }
}

/* Element Plus 样式覆盖 */
:deep(.form-input .el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e2e8f0;
  transition: all 0.2s;
  padding: 12px 16px;
  height: auto;
}

:deep(.form-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.form-input .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

:deep(.form-input .el-input__inner) {
  font-size: 16px;
  line-height: 1.5;
}

:deep(.el-checkbox) {
  color: #4a5568;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}
</style> 