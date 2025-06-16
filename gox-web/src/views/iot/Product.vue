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
  productApi,
  type ProductCreateDTO,
  type ProductListDTO,
  type ProductSpecification,
  type ProductUpdateDTO,
} from "../../api/iot/product";
import { ElMessage, ElMessageBox } from "element-plus";

const crud = ref(null);
// 响应式数据
const tableData = ref<ProductListDTO[]>([]);
const searchSpecification = ref<ProductSpecification>({});
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
  column: [
    {
      label: "产品名称",
      prop: "name",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入姓名",
          trigger: "blur",
        },
      ],
    },
    {
      label: "产品编号",
      prop: "code",
      search: true,
      rules: [
        {
          required: true,
          message: "请输入姓名",
          trigger: "blur",
        },
      ],
    },
    {
      label: "产品状态",
      prop: "status",
      search: true,
      type: "radio",
      dicData: [
        { label: "启用", value: true },
        { label: "停用", value: false },
      ],
      rules: [
        {
          required: true,
          message: "请输入姓名",
          trigger: "blur",
        },
      ],
    },
    {
      label: "产品描述",
      prop: "description",
      search: true,
      type: "textarea",
      span: 24,
    },
  ],
});

const handleSearch = async (
  form: ProductSpecification,
  done: (form: ProductSpecification) => void
) => {
  const { data } = await productApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    form
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
  done(form);
};

const handleRefresh = async () => {
  const { data } = await productApi.findPage(
    page.value.currentPage,
    page.value.pageSize,
    searchSpecification.value
  );
  tableData.value = data.content;
  page.value.total = data.page.totalElements;
};

const handleSave = async (
  form: ProductCreateDTO,
  done: (form: ProductCreateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await productApi.create(form);
  success ? ElMessage.success("创建成功") : ElMessage.error("创建失败");
  done(form);
};
const handleUpdate = async (
  row: ProductUpdateDTO,
  _index: number,
  done: (row: ProductUpdateDTO) => void,
  loading: () => void
) => {
  loading();
  const { success } = await productApi.update(row);
  success ? ElMessage.success("更新成功") : ElMessage.error("更新失败");
  done(row);
};

const handleDelete = async (
  row: ProductListDTO,
  _index: number,
  done: (row: ProductListDTO) => void
) => {
  ElMessageBox.confirm("此操作将永久删除该产品, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      const { success } = await productApi.deleteById(row.id);
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
