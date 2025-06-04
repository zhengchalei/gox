import { createRouter, createWebHistory } from "vue-router";
import { routes } from "./routes.ts";

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫 - 检查token
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  if (to.meta.requiresAuth && !token) {
    // 需要认证但没有token，跳转到登录页
    next("/auth/login");
  } else if ((to.path === "/auth/login" || to.path === "/login") && token) {
    // 已登录状态访问登录页，跳转到首页
    next("/");
  } else {
    next();
  }
});

export default router;
