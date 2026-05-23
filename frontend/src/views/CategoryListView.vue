<template>
  <div class="category-page">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openDialog(null)">新增分类</el-button>
    </div>

    <el-table :data="categories" v-loading="loading" stripe empty-text="暂无分类">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="icon" label="图标" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="snackCount" label="零食数量" width="100" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" maxlength="50" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" maxlength="50" placeholder="例如: 🍪" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'
import type { CategoryVO, CategoryFormDTO } from '@/types/category'

const categories = ref<CategoryVO[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const defaultForm: CategoryFormDTO = { name: '', icon: '', sortOrder: 0 }
const form = reactive<CategoryFormDTO>({ ...defaultForm })

const rules: FormRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

onMounted(() => fetchCategories())

async function fetchCategories() {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data
  } finally {
    loading.value = false
  }
}

function openDialog(row: CategoryVO | null) {
  if (row) {
    isEdit.value = true
    editingId.value = row.id
    form.name = row.name
    form.icon = row.icon
    form.sortOrder = row.sortOrder
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, defaultForm)
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateCategory(editingId.value!, { ...form })
      ElMessage.success('更新成功')
    } else {
      await createCategory({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchCategories()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row: CategoryVO) {
  try {
    await ElMessageBox.confirm(`确定删除分类「${row.name}」吗？`, '删除确认', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    await fetchCategories()
  } catch {
    // 取消或失败
  }
}
</script>

<style scoped>
.category-page {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
