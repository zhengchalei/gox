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
  mqttAclApi,
  type MqttAclCreateDTO,
  type MqttAclListDTO,
  type MqttAclSpecification,
  type MqttAclUpdateDTO,
} from "../../api/iot/mqttAcl";
import { ElMessage, ElMessageBox } from "element-plus";
import { formatDateTime } from "../../utils/dateUtil";

const crud = ref(null);
// 响应式数据
const tableData = ref<MqttAclListDTO[]>([]);
const searchSpecification = ref<MqttAclSpecification>({});
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
  title: "MQTT访问控制管理",
  column: [
    {
      label: "IP地址",
      prop: "ipaddress",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入IP地址",
          trigger: "blur",
        },
      ],
    },
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
      label: "客户端ID",
      prop: "clientId",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入客户端ID",
          trigger: "blur",
        },
      ],
    },
    {
      label: "操作",
      prop: "action",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入操作",
          trigger: "blur",
        },
      ],
    },
    {
      label: "权限",
      prop: "permission",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入权限",
          trigger: "blur",
        },
      ],
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
  form: MqttAclSpecification,
  done: (form: MqttAclSpecification) => void
) => {
  const { data } = await mqttAclApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await mqttAclApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: MqttAclCreateDTO,
  done: (form: MqttAclCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await mqttAclApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: MqttAclUpdateDTO,
  _index: number,
  done: (row: MqttAclUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await mqttAclApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: MqttAclListDTO,
  _index: number,
  done: (row: MqttAclListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该MQTT访问控制, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await mqttAclApi.deleteById(row.id);
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
