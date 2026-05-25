<template>
  <div class="page anim-slide-up">
    <div class="toolbar">
      <el-button type="primary" @click="openDialog(null)">✨ 新增分类</el-button>
    </div>

    <div v-if="list.length===0" class="empty-hint">
      <span class="empty-icon">🐱</span><p>还没有分类哦~</p>
    </div>

    <div v-else class="cat-grid">
      <div v-for="c in list" :key="c.id" class="cat-card">
        <div class="cat-main" @click="goToSnacks(c)">
          <div class="cat-icon">{{ c.icon || '📦' }}</div>
          <div class="cat-name">{{ c.name }}</div>
        </div>
        <div class="cat-actions">
          <el-button size="small" @click.stop="openDialog(c)">✏️</el-button>
          <el-button size="small" type="danger" @click.stop="handleDelete(c.id)">🗑️</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId?'✏️ 编辑分类':'✨ 新增分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="🏷️ 名称" prop="name"><el-input v-model="form.name" maxlength="50"/></el-form-item>
        <el-form-item label="🎨 图标"><el-input v-model="form.icon" maxlength="10" placeholder="如 🍿"/></el-form-item>
        <el-form-item label="🔢 排序"><el-input-number v-model="form.sortOrder" :min="0"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">✨ 保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useRouter } from 'vue-router'
import { getCategories, createCategory, updateCategory, deleteCategory } from '@/api/category'
import type { CategoryVO } from '@/types/category'

const router = useRouter()
const list=ref<CategoryVO[]>([]);const dialogVisible=ref(false);const editingId=ref<number|null>(null);const submitting=ref(false);const formRef=ref<FormInstance>()
const form=reactive({name:'',icon:'',sortOrder:0})
const rules:FormRules={name:[{required:true,message:'请输入分类名称',trigger:'blur'}]}

onMounted(()=>fetchData())
async function fetchData(){try{const res=await getCategories();list.value=res.data}catch{}}

function openDialog(row:CategoryVO|null){if(row){editingId.value=row.id;form.name=row.name;form.icon=row.icon||'';form.sortOrder=row.sortOrder||0}else{editingId.value=null;form.name='';form.icon='';form.sortOrder=0};dialogVisible.value=true}

async function handleSubmit(){const ok=await formRef.value?.validate().catch(()=>false);if(!ok)return;submitting.value=true;try{const d={name:form.name,icon:form.icon||undefined,sortOrder:form.sortOrder};if(editingId.value){await updateCategory(editingId.value,d)}else{await createCategory(d)};dialogVisible.value=false;fetchData()}finally{submitting.value=false}}

function goToSnacks(cat: CategoryVO) { router.push({ path: '/snacks', query: { categoryId: cat.id } }) }

async function handleDelete(id:number){try{await ElMessageBox.confirm('🐻 确定删除该分类吗？分类下的零食不会被删除','删除确认',{type:'warning'});await deleteCategory(id);ElMessage.success('删除成功');fetchData()}catch{}}
</script>

<style scoped>
.page { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px; }
.toolbar { margin-bottom:20px; }
.cat-grid { display:grid;grid-template-columns:repeat(auto-fill,minmax(150px,1fr));gap:16px; }
.cat-card { background:var(--bg-page);border-radius:var(--radius-lg);overflow:hidden;transition:all 0.3s ease;border:2px solid transparent;display:flex;flex-direction:column; }
.cat-card:hover { transform:translateY(-4px);box-shadow:var(--shadow-hover);border-color:var(--primary-light); }
.cat-main { cursor:pointer;padding:20px 16px 8px;text-align:center;transition:transform 0.2s;flex:1; }
.cat-main:hover { transform:scale(1.05); }
.cat-main:hover .cat-icon { animation:wiggle 0.5s ease; }
.cat-icon { font-size:40px;margin-bottom:10px; }
.cat-name { font-size:15px;font-weight:600;color:var(--text-primary);margin-bottom:4px; }
.cat-actions { display:flex;justify-content:center;gap:6px;padding:0 12px 14px; }
.empty-hint { text-align:center;padding:60px 20px;color:var(--text-secondary); }
.empty-icon { font-size:64px;display:block;margin-bottom:12px; }
</style>
