import type {RouteRecordRaw} from "vue-router";

export const userLayoutRoutes = [
    {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: {
            requiresAuth: true,
            title: '仪表盘',
            icon: 'Grid'
        }
    },
    // 系统管理路由
    {
        path: 'system',
        name: 'System',
        meta: {
            requiresAuth: true,
            title: '系统管理',
            icon: 'Setting'
        },
        children: [
            {
                path: 'user',
                name: 'SystemUser',
                component: () => import('../views/system/User.vue'),
                meta: {
                    requiresAuth: true,
                    title: '用户管理',
                    icon: 'User'
                }
            },
            {
                path: 'role',
                name: 'SystemRole',
                component: () => import('../views/system/Role.vue'),
                meta: {
                    requiresAuth: true,
                    title: '角色管理',
                    icon: 'UserFilled'
                }
            },
            {
                path: 'permission',
                name: 'SystemPermission',
                component: () => import('../views/system/Permission.vue'),
                meta: {
                    requiresAuth: true,
                    title: '权限管理',
                    icon: 'Lock'
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
            title: '文件管理',
            icon: 'FolderOpened'
        },
        children: [
            {
                path: '',
                name: 'FileManagement',
                component: () => import('../views/file/List.vue'),
                meta: {
                    requiresAuth: true,
                    title: '文件管理',
                    icon: 'FolderOpened'
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
    },
    {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: {
            requiresAuth: true,
            title: '个人设置',
            icon: 'User'
        }
    },
];

export const routes: RouteRecordRaw[] = [
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
        name: 'UserLayout',
        component: () => import('../layout/UserLayout.vue'),
        meta: {
            requiresAuth: true
        },
        children: userLayoutRoutes
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
];
