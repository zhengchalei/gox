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
      @row-click="handleRowClick"
    ></avue-crud>
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import {
  ${entityName?uncap_first}Api,
  type ${entityName}CreateDTO,
  type ${entityName}ListDTO,
  type ${entityName}Specification,
  type ${entityName}UpdateDTO,
} from "../../api/${moduleName}/${entityName?uncap_first}";
import { ElMessage, ElMessageBox } from "element-plus";

const crud = ref(null);
// 响应式数据
const tableData = ref<${entityName}ListDTO[]>([]);
const searchSpecification = ref<${entityName}Specification>({});
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
  title: "${entityComment}管理",
  column: [
    <#list fields as field>
    {
      label: "${field.comment}",
      prop: "${field.name}",
      search: true,
      <#if field.type == "Boolean">
      type: "radio",
      dicData: [
        { label: "是", value: true },
        { label: "否", value: false },
      ],
      </#if>
      <#if field.name == "description" || field.name == "remark" || field.name?contains("desc")>
      type: "textarea",
      </#if>
      rules: [
        <#if !field.nullable>
        {
          required: true,
          message: "请输入${field.comment}",
          trigger: "blur",
        },
        </#if>
      ],
    },
    </#list>
  ],
});

const handleSearch = async (
  form: ${entityName}Specification,
  done: (form: ${entityName}Specification) => void
) => {
  const { data } = await ${entityName?uncap_first}Api.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await ${entityName?uncap_first}Api.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: ${entityName}CreateDTO,
  done: (form: ${entityName}CreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await ${entityName?uncap_first}Api.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};

const handleUpdate = async (
  row: ${entityName}UpdateDTO,
  _index: number,
  done: (row: ${entityName}UpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await ${entityName?uncap_first}Api.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: ${entityName}ListDTO,
  _index: number,
  done: (row: ${entityName}ListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该${entityComment}, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await ${entityName?uncap_first}Api.deleteById(row.id);
      success ? ElMessage.success("删除成功") : ElMessage.error("删除失败");
      done(row);
    })
    .catch(() => {});
};

const handleRowClick = (row: ${entityName}ListDTO) => {
  // 可以在这里处理行点击事件
};

// 生命周期
onMounted(async () => {
  await handleRefresh();
});
</script>

<style scoped></style>