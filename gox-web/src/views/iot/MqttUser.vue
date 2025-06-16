<template>
  <el-card>
    <avue-crud
      ref="crud"
      :data="tableData"
      :option="tableOption"
      :page="page"
      :search="searchSpecification"
      @row-save="handleSave"
      @row-update="handleUpdate"
      @row-del="handleDelete"
      @refresh-change="handleRefresh"
      @search-change="handleSearch"
      @search-reset="handleRefresh"
    ></avue-crud>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import {
  mqttUserApi,
  type MqttUserCreateDTO,
  type MqttUserListDTO,
  type MqttUserSpecification,
  type MqttUserUpdateDTO,
} from "../../api/iot/mqttUser";
import { ElMessage, ElMessageBox } from "element-plus";
import { formatDateTime } from "../../utils/dateUtil";

const crud = ref(null);
// 响应式数据
const tableData = ref<MqttUserListDTO[]>([]);
const searchSpecification = ref<MqttUserSpecification>({});
const page = ref({
  total: 0,
  currentPage: 1,
  pageSize: 20,
});

const tableOption = ref({
  emptyBtn: true,
  viewBtn: true,
  border: true,
  index: true,
  tree: true,
  dialogDrag: true,
  headerAlign: "center",
  align: "center",
  labelWidth: 100,
  addBtnText: "新增",
  title: "MQTT用户管理",
  column: [
    {
      label: "用户名",
      prop: "username",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入用户名",
          trigger: "blur",
        },
      ],
    },
    {
      label: "密码",
      prop: "password",
      type: "password",
      rules: [
        {
          required: true,
          message: "请输入密码",
          trigger: "blur",
        },
      ],
    },
    {
      label: "超级用户",
      prop: "isSuperuser",
      type: "switch",
      dicData: [
        {
          label: "是",
          value: true,
        },
        {
          label: "否",
          value: false,
        },
      ],
      value: false,
      search: true,
    },
    {
      label: "创建时间",
      prop: "createdTime",
      type: "datetime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      addDisplay: false,
      editDisplay: false,
    },
    {
      label: "更新时间",
      prop: "updatedTime",
      type: "datetime",
      format: "YYYY-MM-DD HH:mm:ss",
      valueFormat: "YYYY-MM-DD HH:mm:ss",
      addDisplay: false,
      editDisplay: false,
    },
  ],
});

const handleSearch = async (
  form: MqttUserSpecification,
  done: (form: MqttUserSpecification) => void
) => {
  const { data } = await mqttUserApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await mqttUserApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: MqttUserCreateDTO,
  done: (form: MqttUserCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await mqttUserApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: MqttUserUpdateDTO,
  _index: number,
  done: (row: MqttUserUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await mqttUserApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: MqttUserListDTO,
  _index: number,
  done: (row: MqttUserListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该MQTT用户, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await mqttUserApi.deleteById(row.id);
      success ? ElMessage.success("删除成功") : ElMessage.error("删除失败");
      done(row);
    })
    .catch(() => {});
};

// 生命周期
onMounted(async () => {
  await handleRefresh();
});
</script>

<style scoped></style>
