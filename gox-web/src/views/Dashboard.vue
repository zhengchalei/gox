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
                <el-icon size="24">
                  <User />
                </el-icon>
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
                <el-icon size="24">
                  <Document />
                </el-icon>
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
                <el-icon size="24">
                  <UserFilled />
                </el-icon>
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
                <el-icon size="24">
                  <Lock />
                </el-icon>
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

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="24">
        <!-- 用户增长趋势图 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" header="用户增长趋势">
            <div ref="userTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 文件类型分布图 -->
        <el-col :xs="24" :lg="12">
          <el-card class="chart-card" header="文件类型分布">
            <div ref="fileTypeChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 20px">
        <!-- 系统访问量统计 -->
        <el-col :xs="24" :lg="16">
          <el-card class="chart-card" header="系统访问量统计">
            <div ref="accessChart" class="chart-container"></div>
          </el-card>
        </el-col>

        <!-- 角色权限分布 -->
        <el-col :xs="24" :lg="8">
          <el-card class="chart-card" header="角色权限分布">
            <div ref="roleChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick } from "vue";
import { Document, Lock, User, UserFilled } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import type { UserDetailDTO } from "../api/system/user";

// 图表引用
const userTrendChart = ref<HTMLDivElement>();
const fileTypeChart = ref<HTMLDivElement>();
const accessChart = ref<HTMLDivElement>();
const roleChart = ref<HTMLDivElement>();

// 响应式数据
const userInfo = ref<UserDetailDTO>({
  id: 0,
  username: "用户",
  nickname: "",
  avatar: "",
  email: "",
  phone: "",
  enabled: true,
  createdTime: "",
  updatedTime: "",
  roleIds: [],
  roles: [],
});

const loadUserInfo = () => {
  const storedUserInfo = localStorage.getItem("userInfo");
  if (storedUserInfo) {
    try {
      userInfo.value = JSON.parse(storedUserInfo);
    } catch (error) {
      console.error("解析用户信息失败:", error);
    }
  }
};

// 初始化用户增长趋势图
const initUserTrendChart = () => {
  if (!userTrendChart.value) return;

  const chart = echarts.init(userTrendChart.value);
  const option = {
    tooltip: {
      trigger: "axis",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: ["1月", "2月", "3月", "4月", "5月", "6月", "7月"],
    },
    yAxis: {
      type: "value",
    },
    series: [
      {
        name: "新增用户",
        type: "line",
        stack: "Total",
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: {
          color: "#667eea",
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(102, 126, 234, 0.3)" },
            { offset: 1, color: "rgba(102, 126, 234, 0.1)" },
          ]),
        },
      },
      {
        name: "活跃用户",
        type: "line",
        stack: "Total",
        smooth: true,
        data: [220, 182, 191, 234, 290, 330, 310],
        itemStyle: {
          color: "#4facfe",
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(79, 172, 254, 0.3)" },
            { offset: 1, color: "rgba(79, 172, 254, 0.1)" },
          ]),
        },
      },
    ],
  };
  chart.setOption(option);
};

// 初始化文件类型分布图
const initFileTypeChart = () => {
  if (!fileTypeChart.value) return;

  const chart = echarts.init(fileTypeChart.value);
  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{a} <br/>{b}: {c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "文件类型",
        type: "pie",
        radius: "50%",
        data: [
          { value: 335, name: "PDF" },
          { value: 310, name: "图片" },
          { value: 234, name: "Word" },
          { value: 135, name: "Excel" },
          { value: 148, name: "其他" },
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
  chart.setOption(option);
};

// 初始化系统访问量统计图
const initAccessChart = () => {
  if (!accessChart.value) return;

  const chart = echarts.init(accessChart.value);
  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "shadow",
      },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: [
      {
        type: "category",
        data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
        axisTick: {
          alignWithLabel: true,
        },
      },
    ],
    yAxis: [
      {
        type: "value",
      },
    ],
    series: [
      {
        name: "访问量",
        type: "bar",
        barWidth: "60%",
        data: [320, 332, 301, 334, 390, 330, 320],
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "#f093fb" },
            { offset: 1, color: "#f5576c" },
          ]),
        },
      },
    ],
  };
  chart.setOption(option);
};

// 初始化角色权限分布图
const initRoleChart = () => {
  if (!roleChart.value) return;

  const chart = echarts.init(roleChart.value);
  const option = {
    tooltip: {
      trigger: "item",
    },
    series: [
      {
        name: "角色分布",
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: "center",
        },
        emphasis: {
          label: {
            show: true,
            fontSize: "20",
            fontWeight: "bold",
          },
        },
        labelLine: {
          show: false,
        },
        data: [
          { value: 40, name: "管理员" },
          { value: 25, name: "编辑者" },
          { value: 20, name: "查看者" },
          { value: 15, name: "游客" },
        ],
      },
    ],
  };
  chart.setOption(option);
};

// 生命周期
onMounted(async () => {
  loadUserInfo();

  await nextTick();

  // 初始化所有图表
  initUserTrendChart();
  initFileTypeChart();
  initAccessChart();
  initRoleChart();
});
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

.charts-section {
  margin-bottom: 20px;
}

.chart-card {
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-container {
  height: 300px;
  width: 100%;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  font-weight: 600;
  color: #333;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .chart-container {
    height: 250px;
  }
}
</style>
