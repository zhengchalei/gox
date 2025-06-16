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
  deviceApi,
  type DeviceCreateDTO,
  type DeviceListDTO,
  type DeviceSpecification,
  type DeviceUpdateDTO,
} from "../../api/iot/device";
import { ElMessage, ElMessageBox } from "element-plus";
import { formatDateTime } from "../../utils/dateUtil";

const crud = ref(null);
// 响应式数据
const tableData = ref<DeviceListDTO[]>([]);
const searchSpecification = ref<DeviceSpecification>({});
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
  title: "设备管理",
  column: [
    {
      label: "设备名称",
      prop: "name",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入设备名称",
          trigger: "blur",
        },
      ],
    },
    {
      label: "设备编号",
      prop: "code",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入设备编号",
          trigger: "blur",
        },
      ],
    },
    {
      label: "设备状态",
      prop: "online",
      search: true,
      type: "radio",
      dicData: [
        { label: "在线", value: true },
        { label: "离线", value: false },
      ],
      rules: [
        {
          required: true,
          message: "请选择设备状态",
          trigger: "blur",
        },
      ],
    },
    {
      label: "设备描述",
      prop: "description",
      search: true,
      type: "textarea",
      span: 24,
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
  form: DeviceSpecification,
  done: (form: DeviceSpecification) => void
) => {
  const { data } = await deviceApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await deviceApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: DeviceCreateDTO,
  done: (form: DeviceCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await deviceApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: DeviceUpdateDTO,
  _index: number,
  done: (row: DeviceUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await deviceApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: DeviceListDTO,
  _index: number,
  done: (row: DeviceListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该设备, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await deviceApi.deleteById(row.id);
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
