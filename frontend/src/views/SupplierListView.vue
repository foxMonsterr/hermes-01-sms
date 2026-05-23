<template>
  <div class="page">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索供应商" clearable style="width:200px" @keyup.enter="fetchData" />
      <el-button type="primary" @click="openDialog(null)">新增供应商</el-button>
    </div>
    <el-table :data="list" stripe v-loading="loading" empty-text="暂无供应商">
      <el-table-column prop="name" label="名称" min-width="120" />
      <el-table-column prop="contact" label="联系人" width="100" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="address" label="地址" min-width="160" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" @click="openStats(row)">统计</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchData" /></div>

    <!-- dialog -->
    <el-dialog v-model="dialogVisible" :title="editingId?'编辑供应商':'新增供应商'" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contact" maxlength="50" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" maxlength="30" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" maxlength="200" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.notes" type="textarea" maxlength="500" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button></template>
    </el-dialog>

    <!-- stats -->
    <el-dialog v-model="statsVisible" title="采购统计" width="400px">
      <el-form inline><el-form-item label="日期"><el-date-picker v-model="statsDate" type="daterange" range-separator="至" value-format="YYYY-MM-DD" @change="loadStats" /></el-form-item></el-form>
      <div v-loading="statsLoading">
        <p>采购次数: {{ stats.purchaseCount }}</p><p>采购总量: {{ stats.totalQuantity }}</p><p>采购金额: ¥{{ stats.estimatedAmount || 0 }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getSuppliers, createSupplier, updateSupplier, deleteSupplier, getSupplierStats } from '@/api/supplier'
import type { Supplier, SupplierStats } from '@/types/supplier'

const list=ref<Supplier[]>([]);const total=ref(0);const loading=ref(false)
const page=ref(1);const size=ref(10);const keyword=ref('')
const dialogVisible=ref(false);const editingId=ref<number|null>(null);const submitting=ref(false)
const formRef=ref<FormInstance>();const form=reactive({name:'',contact:'',phone:'',address:'',notes:''})
const rules:FormRules={name:[{required:true,message:'请输入名称',trigger:'blur'}]}

const statsVisible=ref(false);const statsLoading=ref(false)
const stats=ref<SupplierStats>({supplierId:0,supplierName:'',purchaseCount:0,totalQuantity:0,estimatedAmount:null})
const statsDate=ref<[string,string]|null>(null)
let statsSupplierId=0

onMounted(()=>fetchData())
async function fetchData(){loading.value=true;try{const res=await getSuppliers({page:page.value,size:size.value,keyword:keyword.value||undefined});list.value=res.data.records;total.value=res.data.total}finally{loading.value=false}}

function openDialog(row:Supplier|null){if(row){editingId.value=row.id;form.name=row.name;form.contact=row.contact;form.phone=row.phone;form.address=row.address;form.notes=row.notes}else{editingId.value=null;Object.assign(form,{name:'',contact:'',phone:'',address:'',notes:''})};dialogVisible.value=true}

async function handleSubmit(){const ok=await formRef.value?.validate().catch(()=>false);if(!ok)return;submitting.value=true
  try{const d={...form};if(editingId.value)await updateSupplier(editingId.value,d);else await createSupplier(d);dialogVisible.value=false;fetchData()}finally{submitting.value=false}}

async function handleDelete(id:number){try{await ElMessageBox.confirm('确定删除该供应商吗？','删除确认',{type:'warning'});await deleteSupplier(id);ElMessage.success('删除成功');fetchData()}catch{}}

function openStats(row:Supplier){statsSupplierId=row.id;stats.value={supplierId:row.id,supplierName:row.name,purchaseCount:0,totalQuantity:0,estimatedAmount:null};statsDate.value=null;statsVisible.value=true;loadStats()}
async function loadStats(){statsLoading.value=true;try{const p:any={};if(statsDate.value){p.startDate=statsDate.value[0];p.endDate=statsDate.value[1]};const res=await getSupplierStats(statsSupplierId,p);stats.value=res.data}finally{statsLoading.value=false}}
</script>

<style scoped>.page{background:#fff;border-radius:8px;padding:20px}.toolbar{display:flex;gap:12px;margin-bottom:16px}.pagination{margin-top:16px;display:flex;justify-content:flex-end}</style>
