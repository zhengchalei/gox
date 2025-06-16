<template>
  <avue-tree
    ref="tree"
    :option="option"
    :data="data"
    v-model="form"
    @check-change="checkChange"
  >
  </avue-tree>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits, watch } from "vue";
import { ElMessage } from "element-plus";
import {
  permissionApi,
  type PermissionListDTO,
} from "../../../api/system/permission";

interface Props {
  roleId?: number;
  selectedPermissions?: number[];
}

const props = defineProps<Props>();
const emit = defineEmits(["update:selectedPermissions", "check-change"]);

const tree = ref(null);
const form = ref({});
const data = ref<PermissionListDTO[]>([]);
const option = ref({
  defaultExpandAll: true,
  multiple: true,
  props: {
    label: "name",
    value: "id",
    children: "children",
  },
  formOption: {
    labelWidth: 100,
    column: [
      { label: "权限名称", prop: "name" },
      { label: "权限编码", prop: "code" },
    ],
  },
});

// 获取权限树数据
const fetchPermissionTree = async () => {
  try {
    const { data: permissionData, success } = await permissionApi.findTree();
    if (success) {
      data.value = permissionData.content;
    } else {
      ElMessage.error("获取权限树失败");
    }
  } catch (error) {
    console.error("获取权限树出错:", error);
    ElMessage.error("获取权限树出错");
  }
};

// 监听选中权限变化
const checkChange = (data, checked, indeterminate) => {
  const checkedKeys = tree.value?.getCheckedKeys() || [];
  emit("update:selectedPermissions", checkedKeys);
  emit("check-change", data, checked, indeterminate, checkedKeys);
};

// 设置选中的权限
const setCheckedPermissions = (permissionIds: number[]) => {
  if (tree.value && permissionIds && permissionIds.length > 0) {
    // 清空之前的选择
    const allKeys = getAllKeys(data.value);
    tree.value.setCheckedKeys([], false);

    // 设置新的选择
    tree.value.setCheckedKeys(permissionIds);
  }
};
// 获取所有权限ID
const getAllKeys = (permissions: PermissionListDTO[]) => {
  const keys: number[] = [];
  const traverse = (items: any[]) => {
    if (!items || !items.length) return;
    items.forEach((item) => {
      keys.push(item.id);
      if (item.children && item.children.length) {
        traverse(item.children);
      }
    });
  };
  traverse(permissions);
  return keys;
};

// 监听selectedPermissions属性变化
watch(
  () => props.selectedPermissions,
  (newVal) => {
    if (newVal && newVal.length > 0) {
      setCheckedPermissions(newVal);
    }
  }
);

// 暴露方法给父组件
defineExpose({
  setCheckedPermissions,
  getCheckedKeys: () => tree.value?.getCheckedKeys() || [],
});

onMounted(async () => {
  await fetchPermissionTree();
  if (props.selectedPermissions && props.selectedPermissions.length > 0) {
    setCheckedPermissions(props.selectedPermissions);
  }
});
</script>
