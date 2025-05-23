# GOX 管理系统

一个基于 Vue 3 + TypeScript + Element Plus 的现代化后台管理系统。

## 🎯 最新更新

### 登录功能修复
- ✅ 根据 OpenAPI 规范重构了登录接口
- ✅ 完善了 API 类型定义，支持标准化响应格式
- ✅ 优化了错误处理和用户反馈机制
- ✅ 实现了自动获取用户信息功能

### 后台布局优化
- ✅ 参考 Ant Design Pro 设计，重新设计了整体布局
- ✅ 优化了侧边栏菜单样式和交互效果
- ✅ 增加了搜索功能和更好的用户体验
- ✅ 改进了响应式设计，支持移动端访问
- ✅ 增加了页面过渡动画效果

## 功能特性

- 🚀 **现代化技术栈**: Vue 3 + TypeScript + Vite + Element Plus
- 🔐 **完整的认证系统**: 支持用户名密码登录，token 自动管理
- 🛡️ **路由守卫**: 自动检查token并拦截未授权访问
- 📱 **响应式布局**: 参考 Ant Design Pro 的后台布局设计
- 🎨 **优雅UI**: 基于 Element Plus 的现代化界面
- 🔧 **完整的API系统**: 集成 Axios 请求拦截器和错误处理
- 🔍 **API测试工具**: 内置API连接测试功能
- 🎯 **模块化设计**: 清晰的项目结构和组件化开发

## 项目结构

```
src/
├── api/           # API接口定义
│   ├── index.ts   # 主要API配置和登录接口
│   └── test.ts    # API测试工具
├── components/    # 公共组件
├── layout/        # 布局组件
│   └── index.vue  # 主布局（侧边栏+顶栏+内容区）
├── router/        # 路由配置
├── types/         # TypeScript类型定义
│   └── api.ts     # API相关类型
├── views/         # 页面组件
│   ├── Login.vue      # 登录页面
│   ├── Dashboard.vue  # 仪表盘
│   ├── Profile.vue    # 个人设置
│   ├── system/        # 系统管理页面
│   │   ├── User.vue
│   │   ├── Role.vue
│   │   ├── Permission.vue
│   │   └── RoutePermission.vue
│   └── file/          # 文件管理页面
│       ├── Upload.vue
│       └── List.vue
├── assets/        # 静态资源
├── App.vue        # 根组件
└── main.ts        # 入口文件
```

## API 接口规范

项目已根据 OpenAPI 规范重构了接口类型定义：

### 标准响应格式
```typescript
interface ApiResponse<T = any> {
  success: boolean
  message: string
  data: T
  code: number
  timestamp: number
}
```

### 主要接口
- `POST /api/auth/login` - 用户登录
- `POST /api/logout` - 退出登录
- `GET /api/user/info` - 获取用户信息

## 快速开始

### 环境要求

- Node.js >= 16
- Bun (推荐) 或 npm/yarn
- 后端服务运行在 `http://localhost:8080`

### 安装依赖

```bash
bun install
```

### 启动开发服务器

```bash
bun run dev
```

### 构建生产版本

```bash
bun run build
```

## 功能演示

### 登录界面
- 现代化的登录表单设计
- 表单验证和错误提示
- 自动保存登录状态

### 管理后台
- 侧边栏导航，支持折叠
- 顶部搜索功能
- 面包屑导航
- 用户信息展示
- 响应式设计

### 仪表盘
- 数据统计卡片
- API 测试工具
- 快速操作按钮
- 实时测试结果展示

## 开发指南

### API 测试
项目内置了 API 测试工具，可在仪表盘页面测试：
- 后端连接状态
- 登录接口功能
- 用户信息获取

### 添加新页面
1. 在 `src/views/` 目录下创建新的 Vue 组件
2. 在 `src/router/index.ts` 中添加路由配置
3. 如需要，在侧边栏菜单中添加导航

### 样式定制
- 项目使用 Element Plus 组件库
- 支持深度选择器自定义样式
- 遵循响应式设计原则

## 技术栈详情

- **前端框架**: Vue 3 (Composition API)
- **类型系统**: TypeScript
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **路由管理**: Vue Router 4
- **HTTP 客户端**: Axios
- **包管理器**: Bun

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 部署说明

项目构建后会生成静态文件，可部署到任何静态文件服务器：

```bash
# 构建
bun run build

# 预览构建结果
bun run preview
```

## 更新日志

### v1.1.0 (最新)
- 🔧 重构登录接口，支持 OpenAPI 规范
- 🎨 优化后台布局，参考 Ant Design Pro 设计
- ✨ 增加 API 测试工具
- 🐛 修复类型定义问题
- 💄 改进响应式设计

### v1.0.0
- 🎉 初始版本发布
- ✨ 基础认证系统
- 🎨 基础后台布局
- 📱 响应式设计

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进项目。

## 许可证

MIT License
