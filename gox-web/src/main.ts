import { createApp } from "vue";
import "./style.css";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import App from "./App.vue";
import router from "./router";
import Avue from '@smallwei/avue';
import '@smallwei/avue/lib/index.css';

const app = createApp(App);

// 注册Element Plus
app.use(ElementPlus);

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册路由
app.use(router);
app.use(Avue);

app.mount("#app");
