<template>
  <div class="page anim-slide-up">
    <div class="toolbar">
      <el-radio-group v-model="activeTab" @change="fetchData">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="PENDING">待采购</el-radio-button>
        <el-radio-button value="BOUGHT">已采购</el-radio-button>
        <el-radio-button value="CANCELLED">已取消</el-radio-button>
      </el-radio-group>
      <el-input v-model="keyword" placeholder="🔍 搜索" clearable style="width:200px" @keyup.enter="fetchData" />
      <el-button type="primary" @click="openCreate">✨ 新增</el-button>
      <el-button @click="handleFromLowStock">🔄 从低库存生成</el-button>
    </div>

    <el-table :data="items" stripe v-loading="loading" empty-text="暂无采购清单">
      <el-table-column prop="snackName" label="名称" min-width="130" />
      <el-table-column prop="categoryName" label="分类" width="90" />
      <el-table-column prop="plannedQty" label="计划数量" width="90" />
      <el-table-column prop="price" label="预估单价" width="90"><template #default="{row}">{{row.price?'¥'+row.price:''}}</template></el-table-column>
      <el-table-column prop="supplierName" label="供应商" width="100" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{statusLabel(row.status)}}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{row}">
          <template v-if="row.status==='PENDING'">
            <el-button size="small" @click="openEdit(row)">✏️</el-button>
            <el-button size="small" type="success" @click="openStockIn(row)">📥 入库</el-button>
            <el-button size="small" type="warning" @click="handleCancel(row.id)">取消</el-button>
          </template>
          <el-button v-if="row.status!=='PENDING'" size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchData"/></div>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="editingId?'✏️ 编辑':'✨ 新增采购项'" width="420px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="🏷️ 名称" prop="snackName"><el-input v-model="form.snackName" maxlength="100"/></el-form-item>
        <el-form-item label="📦 数量" prop="plannedQty"><el-input-number v-model="form.plannedQty" :min="1"/></el-form-item>
        <el-form-item label="💰 单价"><el-input v-model="form.price" placeholder="选填"/></el-form-item>
        <el-form-item label="🏭 供应商"><el-select v-model="form.supplierId" placeholder="选择供应商" clearable filterable style="width:100%"><el-option v-for="s in supplierOptions" :key="s.id" :label="s.name" :value="s.id"/></el-select></el-form-item>
        <el-form-item label="📝 备注"><el-input v-model="form.remark" maxlength="200"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">✨ 确定</el-button></template>
    </el-dialog>

    <!-- 入库 -->
    <el-dialog v-model="stockInVisible" title="📥 采购入库" width="380px">
      <el-form label-width="80px"><el-form-item label="零食"><span>{{stockInItem?.snackName}}</span></el-form-item><el-form-item label="实际数量"><el-input-number v-model="stockInForm.actualQty" :min="1"/></el-form-item><el-form-item label="实际单价"><el-input v-model="stockInForm.price" placeholder="选填"/></el-form-item><el-form-item label="备注"><el-input v-model="stockInForm.remark"/></el-form-item></el-form>
      <template #footer><el-button @click="stockInVisible=false">取消</el-button><el-button type="primary" :loading="stockInLoading" @click="handleStockIn">确认入库</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getShoppingList, createShoppingItem, updateShoppingItem, updateShoppingItemStatus, deleteShoppingItem, fromLowStock, stockIn } from '@/api/shoppingList'
import { getSupplierOptions } from '@/api/supplier'
import type { ShoppingListItem } from '@/types/shoppingList'

const items=ref<ShoppingListItem[]>([]);const total=ref(0);const loading=ref(false);const page=ref(1);const size=ref(10);const activeTab=ref('');const keyword=ref('')
const supplierOptions=ref<{id:number;name:string}[]>([])
const dialogVisible=ref(false);const editingId=ref<number|null>(null);const submitting=ref(false);const formRef=ref<FormInstance>()
const form=reactive({snackName:'',plannedQty:1,price:'',remark:'',supplierId:undefined as number|undefined})
const formRules:FormRules={snackName:[{required:true,message:'请输入名称',trigger:'blur'}]}
const stockInVisible=ref(false);const stockInLoading=ref(false);const stockInItem=ref<ShoppingListItem|null>(null)
const stockInForm=reactive({actualQty:1,price:'',remark:''})

onMounted(()=>{fetchData();getSupplierOptions().then(r=>supplierOptions.value=r.data).catch(()=>{})})
async function fetchData(){loading.value=true;try{const res=await getShoppingList(page.value,size.value,activeTab.value||undefined,keyword.value||undefined);items.value=res.data.records;total.value=res.data.total}finally{loading.value=false}}

function openCreate(){editingId.value=null;Object.assign(form,{snackName:'',plannedQty:1,price:'',remark:'',supplierId:undefined});dialogVisible.value=true}
function openEdit(row:ShoppingListItem){editingId.value=row.id;Object.assign(form,{snackName:row.snackName,plannedQty:row.plannedQty,price:row.price||'',remark:row.remark,supplierId:row.supplierId});dialogVisible.value=true}

async function handleSubmit(){const ok=await formRef.value?.validate().catch(()=>false);if(!ok)return;submitting.value=true;try{const data:any={snackName:form.snackName,plannedQty:form.plannedQty,price:form.price?Number(form.price):undefined,remark:form.remark,supplierId:form.supplierId};if(editingId.value){await updateShoppingItem(editingId.value,data)}else{await createShoppingItem(data)};dialogVisible.value=false;fetchData()}finally{submitting.value=false}}

async function handleCancel(id:number){await updateShoppingItemStatus(id,'CANCELLED');fetchData()}
async function handleDelete(id:number){await deleteShoppingItem(id);fetchData()}
async function handleFromLowStock(){const res=await fromLowStock();ElMessage.success('已生成 '+res.data.createdCount+' 条，跳过 '+res.data.skippedCount+' 条');fetchData()}
function openStockIn(row:ShoppingListItem){stockInItem.value=row;stockInForm.actualQty=row.plannedQty;stockInForm.price='';stockInForm.remark='';stockInVisible.value=true}
async function handleStockIn(){stockInLoading.value=true;try{const res=await stockIn(stockInItem.value!.id,{actualQty:stockInForm.actualQty,price:stockInForm.price?Number(stockInForm.price):undefined,remark:stockInForm.remark});ElMessage.success('入库成功，当前库存: '+res.data.quantity);stockInVisible.value=false;fetchData()}finally{stockInLoading.value=false}}

function statusLabel(s:string){return {PENDING:'待采购',BOUGHT:'已采购',CANCELLED:'已取消'}[s]||s}
function statusTag(s:string):any{return {PENDING:'warning',BOUGHT:'success',CANCELLED:'info'}[s]||'info'}
</script>

<style scoped>
.page { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px; }
.toolbar { display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap; }
.pagination { margin-top:16px;display:flex;justify-content:flex-end; }
</style>
