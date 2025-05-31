import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/auth/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: {
      requiresAuth: false
    }
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: {
      requiresAuth: false
    }
  },
  {
    path: '/login',
    redirect: '/auth/login'
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../layout/UserLayout.vue'),
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: {
          requiresAuth: true,
          title: '仪表盘'
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: {
          requiresAuth: true,
          title: '个人设置'
        }
      },
      // 系统管理路由
      {
        path: 'system',
        name: 'System',
        meta: {
          requiresAuth: true,
          title: '系统管理'
        },
        children: [
          {
            path: 'user',
            name: 'SystemUser',
            component: () => import('../views/system/User.vue'),
            meta: {
              requiresAuth: true,
              title: '用户管理'
            }
          },
          {
            path: 'role',
            name: 'SystemRole',
            component: () => import('../views/system/Role.vue'),
            meta: {
              requiresAuth: true,
              title: '角色管理'
            }
          },
          {
            path: 'permission',
            name: 'SystemPermission',
            component: () => import('../views/system/Permission.vue'),
            meta: {
              requiresAuth: true,
              title: '权限管理'
            }
          },
        ]
      },
      // 文件管理路由
      {
        path: 'file',
        name: 'File',
        meta: {
          requiresAuth: true,
          title: '文件管理'
        },
        children: [
          {
            path: '',
            name: 'FileManagement',
            component: () => import('../views/file/List.vue'),
            meta: {
              requiresAuth: true,
              title: '文件管理'
            }
          },
          {
            path: 'upload',
            redirect: '/file'
          },
          {
            path: 'list',
            redirect: '/file'
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查token
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要认证但没有token，跳转到登录页
    next('/auth/login')
  } else if ((to.path === '/auth/login' || to.path === '/login') && token) {
    // 已登录状态访问登录页，跳转到首页
    next('/')
  } else {
    next()
  }
})

export default router 