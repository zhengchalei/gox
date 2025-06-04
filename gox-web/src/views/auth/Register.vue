<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- 左侧图片区域 -->
      <div class="register-left">
        <div class="brand-section">
          <div class="brand-logo">
            <el-icon size="48" color="#ffffff"><Grid /></el-icon>
          </div>
          <h1 class="brand-title">GOX 管理系统</h1>
          <p class="brand-subtitle">加入我们，开启现代化的管理之旅</p>
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
            <el-icon><Lock /></el-icon>
            <span>安全保障</span>
          </div>
          <div class="feature-item">
            <el-icon><Promotion /></el-icon>
            <span>快速便捷</span>
          </div>
          <div class="feature-item">
            <el-icon><Star /></el-icon>
            <span>用户友好</span>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="register-right">
        <div class="register-box">
          <div class="register-header">
            <h2>创建账号</h2>
            <p>填写以下信息完成注册</p>
          </div>

          <el-form
            ref="formRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                prefix-icon="User"
                class="form-input"
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                size="large"
                prefix-icon="Message"
                class="form-input"
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item class="register-options">
              <el-checkbox v-model="registerForm.agreeTerms">
                我已阅读并同意
                <el-link type="primary" :underline="false">用户协议</el-link>
                和
                <el-link type="primary" :underline="false">隐私政策</el-link>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="register-button"
                :loading="loading"
                @click="handleRegister"
              >
                <span v-if="!loading">注册</span>
                <span v-else>注册中...</span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="register-footer">
            <p>
              已有账号？
              <el-link type="primary" :underline="false" @click="goToLogin">
                立即登录
              </el-link>
            </p>
            <p>© 2024 GOX 管理系统. All rights reserved.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElForm, ElMessage } from "element-plus";

const router = useRouter();

// 表单引用
const formRef = ref<InstanceType<typeof ElForm>>();

// 注册表单
const registerForm = reactive({
  username: "",
  email: "",
  agreeTerms: false,
});

// 加载状态
const loading = ref(false);

// 自定义验证规则
const validateAgreeTerms = (_rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error("请同意用户协议和隐私政策"));
  } else {
    callback();
  }
};

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度在 3 到 20 个字符",
      trigger: "blur",
    },
    {
      pattern: /^[a-zA-Z0-9_]+$/,
      message: "用户名只能包含字母、数字和下划线",
      trigger: "blur",
    },
  ],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email" as const,
      message: "请输入正确的邮箱地址",
      trigger: "blur",
    },
  ],
  agreeTerms: [{ validator: validateAgreeTerms, trigger: "change" }],
};

// 注册处理
const handleRegister = async () => {
  if (!formRef.value) return;

  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;

  try {
    // 这里应该调用注册 API
    // const response = await registerApi.register(registerForm)

    // 模拟 API 调用
    await new Promise((resolve) => setTimeout(resolve, 2000));

    ElMessage.success("注册成功！请登录您的账号");
    router.push("/auth/login");
  } catch (error: any) {
    console.error("注册失败:", error);
    ElMessage.error("注册失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// 跳转到登录页面
const goToLogin = () => {
  router.push("/auth/login");
};
</script>

<style scoped>
.register-container {
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

.register-wrapper {
  width: 100vw;
  height: 100vh;
  background: white;
  display: flex;
  overflow: hidden;
}

/* 左侧图片区域 */
.register-left {
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

/* 右侧注册表单 */
.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fafbfc;
  overflow-y: auto;
}

.register-box {
  width: 100%;
  max-width: 400px;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-header h2 {
  color: #1a202c;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  color: #718096;
  margin: 0;
  font-size: 16px;
}

.register-form {
  margin-bottom: 24px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.form-input {
  border-radius: 8px;
}

.register-options {
  margin-bottom: 24px;
}

.register-options :deep(.el-form-item__content) {
  line-height: 1.5;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.register-footer {
  text-align: center;
  margin-top: 24px;
}

.register-footer p {
  color: #a0aec0;
  font-size: 14px;
  margin: 8px 0;
}

.register-footer p:first-child {
  color: #4a5568;
  font-size: 16px;
}

/* 动画 */
@keyframes float {
  0%,
  100% {
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
  .register-wrapper {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .register-left {
    flex: none;
    height: 250px;
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
  .register-container {
    padding: 20px;
  }

  .register-wrapper {
    width: 100%;
    border-radius: 12px;
  }

  .register-left {
    height: 200px;
    padding: 30px;
  }

  .register-right {
    padding: 30px 20px;
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
  line-height: 1.5;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

:deep(.el-link) {
  font-size: 14px;
}
</style>
