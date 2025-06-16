<template>
  <el-dialog
    title="权限分配"
    v-model="dialogVisible"
    width="50%"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @closed="handleDialogClosed"
  >
    <el-form :model="formData" label-width="100px">
      <el-form-item label="角色名称">
        <el-input v-model="formData.name" disabled></el-input>
      </el-form-item>
      <el-form-item label="权限分配">
        <permission-tree
          ref="permissionTreeRef"
          :role-id="formData.id"
          :selected-permissions="formData.permissionIds"
          @check-change="handleCheckChange"
        ></permission-tree>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { defineEmits, defineProps, ref, watch } from 'vue';
import { roleApi, type RolePermissionUpdateDTO } from '../../../api/system/role';
import PermissionTree from './PermissionTree.vue';

interface Props {
  visible: boolean;
  roleId?: number;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:visible', 'success']);

const dialogVisible = ref(false);
const permissionTreeRef = ref(null);
const formData = ref<{
  id?: number;
  name?: string;
  permissionIds: number[];
}>({ permissionIds: [] });

// 监听visible属性变化
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal;
    if (newVal && props.roleId) {
      fetchRoleDetail(props.roleId);
    }
  }
);

// 监听dialogVisible变化，同步更新父组件的visible属性
watch(
  () => dialogVisible.value,
  (newVal) => {
    emit('update:visible', newVal);
  }
);

// 获取角色详情
const fetchRoleDetail = async (roleId: number) => {
  try {
    const { data: roleDetail, success } = await roleApi.findById(roleId);
    if (success && roleDetail) {
      formData.value = {
        id: roleDetail.id,
        name: roleDetail.name,
        permissionIds: roleDetail.permissionIds || []
      };
    } else {
      ElMessage.error('获取角色详情失败');
    }
  } catch (error) {
    console.error('获取角色详情出错:', error);
    ElMessage.error('获取角色详情出错');
  }
};

// 处理权限选择变化
const handleCheckChange = (data, checked, indeterminate, checkedKeys) => {
  formData.value.permissionIds = checkedKeys;
};

// 提交权限分配
const handleSubmit = async () => {
  if (!formData.value.id) {
    ElMessage.warning('请先选择角色');
    return;
  }

  try {
    const permissionIds = permissionTreeRef.value?.getCheckedKeys() || [];
    const data: RolePermissionUpdateDTO = {
      id: formData.value.id,
      permissionIds: permissionIds
    };

    const { success } = await roleApi.assignRolePermission(data);
    if (success) {
      ElMessage.success('权限分配成功');
      dialogVisible.value = false;
      emit('success');
    } else {
      ElMessage.error('权限分配失败');
    }
  } catch (error) {
    console.error('分配权限出错:', error);
    ElMessage.error('分配权限出错');
  }
};

// 对话框关闭时重置表单
const handleDialogClosed = () => {
  formData.value = { permissionIds: [] };
};
</script>