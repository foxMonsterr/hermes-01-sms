<template>
  <div class="snack-page anim-slide-up">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="🔍 搜索零食..." clearable style="width:220px" @clear="handleSearch" @keyup.enter="handleSearch" />
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width:160px" @change="handleSearch">
        <el-option v-for="c in categoryList" :key="c.id" :label="(c.icon||'')+' '+c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" @click="openDialog(null)">✨ 添加零食</el-button>
      <el-upload :show-file-list="false" :before-upload="beforeImport" :http-request="handleImport" accept=".xlsx">
        <el-button>📥 导入</el-button>
      </el-upload>
      <el-button type="danger" :disabled="selectedIds.length===0" @click="handleBatchDelete">🗑️ 批量删除 ({{selectedIds.length}})</el-button>
      <div class="view-toggle">
        <el-button :type="viewMode==='card'?'primary':'default'" size="small" circle @click="viewMode='card'">⊞</el-button>
        <el-button :type="viewMode==='table'?'primary':'default'" size="small" circle @click="viewMode='table'">☰</el-button>
      </div>
    </div>

    <!-- 卡片模式 -->
    <div v-if="viewMode==='card'" v-loading="loading" class="card-grid">
      <div v-if="snacks.length===0" class="empty-hint">
        <span class="empty-icon">🐱</span>
        <p>还没有零食哦~ 让小熊帮你添加第一个吧！</p>
        <el-button type="primary" @click="openDialog(null)">✨ 添加零食</el-button>
      </div>
      <div v-for="row in snacks" :key="row.id" class="snack-card" @click="openDialog(row)">
        <div class="card-img">
          <img v-if="row.imageUrl" :src="row.imageUrl" />
          <span v-else class="card-emoji">{{ categoryEmoji(row.categoryId) }}</span>
        </div>
        <div class="card-body">
          <div class="card-name">{{ row.name }}</div>
          <div class="card-cat">{{ row.categoryName }}</div>
          <div class="card-stats">
            <span>📦 {{ row.quantity }}{{ row.unit }}</span>
            <span v-if="row.price!=null">💰 ¥{{ row.price }}</span>
          </div>
          <span v-if="row.isOnShelf!==1" class="shelf-tag">已下架</span>
          <span class="expire-tag" :class="expireClass(row)">
            {{ expireLabel(row) }}
          </span>
        </div>
      </div>
    </div>

    <!-- 表格模式 -->
    <div v-else>
      <el-table :data="snacks" v-loading="loading" stripe empty-text="暂无零食" @selection-change="(rows:SnackVO[])=>selectedIds=rows.map(r=>r.id)">
        <el-table-column type="selection" width="45" />
        <el-table-column label="图片" width="70">
          <template #default="{row}"><img v-if="row.imageUrl" :src="row.imageUrl" style="width:40px;height:40px;border-radius:8px;object-fit:cover" /><span v-else style="font-size:28px">{{ categoryEmoji(row.categoryId) }}</span></template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="categoryName" label="分类" width="90" />
        <el-table-column label="数量" width="150">
          <template #default="{row}">
            <el-button size="small" circle @click.stop="handleQuantity(row,-1)">-</el-button>
            <span style="margin:0 6px">{{ row.quantity }} {{ row.unit }}</span>
            <el-button size="small" circle @click.stop="handleQuantity(row,1)">+</el-button>
            <el-button size="small" style="margin-left:4px" @click.stop="openAdjust(row)">调整</el-button>
          </template>
        </el-table-column>
        <el-table-column label="过期" width="110">
          <template #default="{row}"><span class="expire-tag" :class="expireClass(row)">{{ expireLabel(row) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click.stop="openDialog(row)">✏️</el-button>
            <el-button size="small" type="warning" @click.stop="openDispose(row)">🗑️处理</el-button>
            <el-button size="small" type="info" @click.stop="openCheck(row)">📋盘点</el-button>
            <el-button size="small" type="danger" @click.stop="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchSnacks" />
    </div>

    <!-- 弹窗: 添加/编辑 -->
    <el-dialog v-model="dialogVisible" :title="isEdit?'✏️ 编辑零食':'✨ 添加零食'" width="680px">
      <el-row :gutter="24">
        <el-col :span="10">
          <div class="upload-area" @click="triggerUpload">
            <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
            <div class="upload-icon">📷</div>
            <div class="upload-text">点击上传零食图片</div>
            <div class="upload-hint">PNG / JPG / WEBP ≤ 2MB</div>
          </div>
        </el-col>
        <el-col :span="14">
          <div class="preview-card">
            <div class="preview-img">
              <img v-if="form.imageUrl" :src="form.imageUrl" />
              <span v-else style="font-size:64px">🍿</span>
            </div>
            <div class="preview-name">{{ form.name || '零食名称' }}</div>
            <div class="preview-info">
              <div class="preview-info-item"><div class="label">库存</div><div class="val">{{ form.quantity }}{{ form.unit }}</div></div>
              <div class="preview-info-item"><div class="label">单价</div><div class="val">{{ form.price?'¥'+form.price:'未设' }}</div></div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" style="margin-top:16px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="🏷️ 名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="🗂️ 分类" prop="categoryId"><el-select v-model="form.categoryId" style="width:100%"><el-option v-for="c in categoryList" :key="c.id" :label="(c.icon||'')+' '+c.name" :value="c.id" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="📦 数量" prop="quantity"><el-input-number v-model="form.quantity" :min="1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="📏 单位"><el-input v-model="form.unit" maxlength="10" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="💰 单价"><el-input v-model="form.price" placeholder="选填" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="📅 采购日期"><el-date-picker v-model="form.purchaseDate" type="date" placeholder="选填" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="🎂 过期日期"><el-date-picker v-model="form.expiryDate" type="date" placeholder="选填" style="width:100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="🏪 上架"><el-switch v-model="form.isOnShelf" active-text="已上架" inactive-text="已下架" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="📋 描述"><el-input v-model="form.description" maxlength="500" placeholder="商品描述" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="📝 备注"><el-input v-model="form.notes" type="textarea" maxlength="200" :rows="2" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">✨ 保存</el-button></template>
    </el-dialog>

    <!-- 调整库存弹窗 -->
    <el-dialog v-model="adjustVisible" title="调整库存" width="360px">
      <el-form label-width="80px"><el-form-item label="零食">{{ adjustSnack?.name }}</el-form-item><el-form-item label="数量"><el-input-number v-model="adjustQty" :min="0" /></el-form-item><el-form-item label="备注"><el-input v-model="adjustRemark" /></el-form-item></el-form>
      <template #footer><el-button @click="adjustVisible=false">取消</el-button><el-button type="primary" :loading="adjustLoading" @click="handleAdjust">确认</el-button></template>
    </el-dialog>

    <!-- 处理弹窗 -->
    <el-dialog v-model="disposeVisible" title="🗑️ 处理零食" width="380px">
      <el-form label-width="80px"><el-form-item label="零食">{{disposeSnack?.name}}</el-form-item><el-form-item label="当前库存">{{disposeSnack?.quantity}}{{disposeSnack?.unit}}</el-form-item><el-form-item label="处理数量"><el-input-number v-model="disposeQty" :min="1" :max="disposeSnack?.quantity||0"/></el-form-item><el-form-item label="原因"><el-select v-model="disposeReason" style="width:100%"><el-option label="已过期" value="EXPIRED"/><el-option label="已损坏" value="DAMAGED"/><el-option label="手动处理" value="MANUAL"/></el-select></el-form-item><el-form-item label="备注"><el-input v-model="disposeRemark"/></el-form-item></el-form>
      <template #footer><el-button @click="disposeVisible=false">取消</el-button><el-button type="primary" :loading="disposeLoading" @click="handleDispose">确认处理</el-button></template>
    </el-dialog>

    <!-- 盘点弹窗 -->
    <el-dialog v-model="checkVisible" title="📋 库存盘点" width="380px">
      <el-form label-width="80px"><el-form-item label="零食">{{checkSnack?.name}}</el-form-item><el-form-item label="系统库存">{{checkSnack?.quantity}}{{checkSnack?.unit}}</el-form-item><el-form-item label="实际库存"><el-input-number v-model="checkActualQty" :min="0"/></el-form-item><el-form-item label="备注"><el-input v-model="checkRemark"/></el-form-item></el-form>
      <template #footer><el-button @click="checkVisible=false">取消</el-button><el-button type="primary" :loading="checkLoading" @click="handleCheck">确认盘点</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getCategories } from '@/api/category'
import { getSnacks, createSnack, updateSnack, updateQuantity, adjustQuantity, deleteSnack, batchDeleteSnacks, uploadSnackImage, importSnacks } from '@/api/snack'
import { disposeSnack } from '@/api/disposal'
import { checkSnack as checkSnackApi } from '@/api/inventoryCheck'
import type { CategoryVO } from '@/types/category'
import type { SnackVO, SnackCreateDTO, SnackQuery } from '@/types/snack'

const snacks = ref<SnackVO[]>([]); const total = ref(0); const loading = ref(false)
const selectedIds = ref<number[]>([]); const categoryList = ref<CategoryVO[]>([])
const query = reactive<SnackQuery>({ page: 1, size: 10 })
const route = useRoute()
const viewMode = ref<'card'|'table'>('card')

const dialogVisible = ref(false); const isEdit = ref(false); const editingId = ref<number|null>(null)
const submitting = ref(false); const formRef = ref<FormInstance>()
const fileInput = ref<HTMLInputElement>()

const adjustVisible = ref(false); const adjustLoading = ref(false); const adjustSnack = ref<SnackVO|null>(null)
const adjustQty = ref(0); const adjustRemark = ref('')

const defaultForm: any = { name:'',categoryId:0,quantity:1,unit:'包',price:'',imageUrl:'',isOnShelf:true,description:'',purchaseDate:undefined,expiryDate:undefined,notes:'' }
const form = reactive<any>({ ...defaultForm })
const rules: FormRules = { name:[{required:true,message:'请输入零食名称',trigger:'blur'}], categoryId:[{required:true,message:'请选择分类',trigger:'change'}], quantity:[{required:true,message:'请输入数量',trigger:'blur'}] }

onMounted(()=>{const catId=route.query.categoryId;if(catId){query.categoryId=Number(catId)};loadCategories();fetchSnacks()})
async function loadCategories(){try{const res=await getCategories();categoryList.value=res.data}catch{}}

async function fetchSnacks(){loading.value=true;try{const res=await getSnacks({...query});snacks.value=res.data.records;total.value=res.data.total}finally{loading.value=false}}
function handleSearch(){query.page=1;fetchSnacks()}

function categoryEmoji(catId:number){const c=categoryList.value.find(x=>x.id===catId);return c?.icon||'📦'}

function expireClass(row:SnackVO):string{
  if(!row.expiryDate)return'normal'
  const d=(new Date(row.expiryDate).getTime()-Date.now())/(86400000)
  if(d<0)return'expired';if(d<3)return'critical';if(d<7)return'danger';if(d<30)return'warning';return'normal'
}
function expireLabel(row:SnackVO):string{
  if(!row.expiryDate)return'无期限'
  const d=Math.ceil((new Date(row.expiryDate).getTime()-Date.now())/(86400000))
  if(d<0)return'已过期';if(d===0)return'今天过期';if(d===1)return'明天过期';if(d<3)return d+'天后';if(d<7)return d+'天后';if(d<30)return d+'天后';return d+'天后'
}

function openDialog(row:SnackVO|null){
  if(row){isEdit.value=true;editingId.value=row.id;form.name=row.name;form.categoryId=row.categoryId;form.unit=row.unit;form.price=row.price!=null?String(row.price):'';form.imageUrl=row.imageUrl||'';form.isOnShelf=row.isOnShelf===1||row.isOnShelf==null||row.isOnShelf===true;form.description=row.description||'';form.purchaseDate=row.purchaseDate||undefined;form.expiryDate=row.expiryDate||undefined;form.notes=row.notes}
  else{isEdit.value=false;editingId.value=null;Object.assign(form,{...defaultForm,categoryId:categoryList.value[0]?.id||0})}
  dialogVisible.value=true
}

function triggerUpload(){fileInput.value?.click()}
async function onFileChange(e:Event){const f=(e.target as HTMLInputElement).files?.[0];if(!f)return;const ok=['image/jpeg','image/png','image/webp'].includes(f.type);if(!ok){ElMessage.error('仅支持 jpg/png/webp');return};if(f.size>2*1024*1024){ElMessage.error('图片不能超过2MB');return};const fd=new FormData();fd.append('file',f);try{const res=await uploadSnackImage(fd);if(res.data?.url){form.imageUrl=res.data.url;ElMessage.success('✨ 上传成功')}else{ElMessage.error('上传失败，请重试')}}catch{ElMessage.error('上传失败，网络错误')}}

async function handleSubmit(){const valid=await formRef.value?.validate().catch(()=>false);if(!valid)return;submitting.value=true;try{const data:any={name:form.name,categoryId:form.categoryId,unit:form.unit,imageUrl:form.imageUrl,isOnShelf:form.isOnShelf?1:0,description:form.description,purchaseDate:form.purchaseDate,expiryDate:form.expiryDate,notes:form.notes};if(form.price)data.price=Number(form.price);if(!isEdit.value)data.quantity=form.quantity;if(isEdit.value){await updateSnack(editingId.value!,data);ElMessage.success('✨ 更新成功')}else{await createSnack(data);ElMessage.success('🎉 添加成功！')};dialogVisible.value=false;await fetchSnacks()}finally{submitting.value=false}}

async function handleQuantity(row:SnackVO,delta:number){try{await updateQuantity(row.id,{delta});await fetchSnacks()}catch{}}
function openAdjust(row:SnackVO){adjustSnack.value=row;adjustQty.value=row.quantity;adjustRemark.value='';adjustVisible.value=true}
async function handleAdjust(){adjustLoading.value=true;try{await adjustQuantity(adjustSnack.value!.id,{quantity:adjustQty.value,remark:adjustRemark.value||undefined});ElMessage.success('库存调整成功');adjustVisible.value=false;await fetchSnacks()}finally{adjustLoading.value=false}}

const disposeVisible=ref(false);const disposeLoading=ref(false);const disposeSnack=ref<SnackVO|null>(null);const disposeQty=ref(1);const disposeReason=ref('EXPIRED');const disposeRemark=ref('')
function openDispose(row:SnackVO){disposeSnack.value=row;disposeQty.value=1;disposeReason.value='EXPIRED';disposeRemark.value='';disposeVisible.value=true}
async function handleDispose(){disposeLoading.value=true;try{await disposeSnack(disposeSnack.value!.id,{quantity:disposeQty.value,reason:disposeReason.value as any,remark:disposeRemark.value||undefined});ElMessage.success('处理成功');disposeVisible.value=false;await fetchSnacks()}finally{disposeLoading.value=false}}

const checkVisible=ref(false);const checkLoading=ref(false);const checkSnack=ref<SnackVO|null>(null);const checkActualQty=ref(0);const checkRemark=ref('')
function openCheck(row:SnackVO){checkSnack.value=row;checkActualQty.value=row.quantity;checkRemark.value='';checkVisible.value=true}
async function handleCheck(){checkLoading.value=true;try{const res=await checkSnackApi(checkSnack.value!.id,{actualQty:checkActualQty.value,remark:checkRemark.value||undefined});const d=res.data;ElMessage.success('盘点完成,'+(d.difference===0?'库存一致':d.difference>0?'盘盈 '+d.difference:'盘亏 '+Math.abs(d.difference)));checkVisible.value=false;await fetchSnacks()}finally{checkLoading.value=false}}

async function beforeImport(file:File){return file.name.endsWith('.xlsx')}
async function handleImport({file}:any){const fd=new FormData();fd.append('file',file);try{const res=await importSnacks(fd);const d=res.data;ElMessage.success('导入完成:成功'+d.successCount+'条,失败'+d.failCount+'条');if(d.errors.length)ElMessage.warning(d.errors.slice(0,3).map((e:any)=>'第'+e.row+'行: '+e.reason).join('; '));await fetchSnacks()}catch{ElMessage.error('导入失败')}}

async function handleDelete(id:number){try{await ElMessageBox.confirm('🐻 确定删除该零食吗？删除后就找不回来啦', '删除确认',{type:'warning'});await deleteSnack(id);ElMessage.success('删除成功');await fetchSnacks()}catch{}}
async function handleBatchDelete(){try{await ElMessageBox.confirm('🐻 确定删除选中的 '+selectedIds.value.length+' 个零食吗？','批量删除确认',{type:'warning'});await batchDeleteSnacks(selectedIds.value);ElMessage.success('删除成功');selectedIds.value=[];await fetchSnacks()}catch{}}
</script>

<style scoped>
.snack-page { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px; }
.search-bar { display:flex;gap:10px;margin-bottom:20px;flex-wrap:wrap;align-items:center; }
.view-toggle { margin-left:auto;display:flex;gap:4px; }

/* 卡片网格 */
.card-grid { display:grid;grid-template-columns:repeat(auto-fill,minmax(220px,1fr));gap:16px; }
.snack-card { background:var(--bg-card);border-radius:var(--radius-lg);overflow:hidden;box-shadow:var(--shadow-card);cursor:pointer;transition:all 0.3s ease;border:2px solid transparent;position:relative; }
.snack-card::before { content:'';position:absolute;top:0;left:0;right:0;height:5px;background:var(--gradient-primary); }
.snack-card:hover { transform:translateY(-6px);box-shadow:var(--shadow-hover);border-color:var(--primary-light); }
.card-img { height:120px;display:flex;align-items:center;justify-content:center;background:linear-gradient(135deg,rgba(255,245,247,0.8),rgba(255,255,255,0.8));margin-top:5px; }
.card-img img { width:100%;height:100%;object-fit:cover; }
.card-emoji { font-size:56px; }
.card-body { padding:14px 16px 16px; }
.card-name { font-size:15px;font-weight:600;color:var(--text-primary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis; }
.card-cat { font-size:12px;color:var(--text-secondary);margin:4px 0 8px; }
.card-stats { display:flex;justify-content:space-between;font-size:13px;color:var(--text-secondary);margin-bottom:10px; }

/* 过期标签 */
.expire-tag { display:inline-block;padding:3px 10px;border-radius:var(--radius-sm);font-size:12px;font-weight:600; }
.expire-tag.normal { background:rgba(107,203,119,0.15);color:var(--expire-normal); }
.expire-tag.warning { background:rgba(255,217,61,0.18);color:#C8960D; }
.expire-tag.danger { background:rgba(255,165,0,0.15);color:var(--expire-danger); }
.expire-tag.critical { background:rgba(255,107,107,0.18);color:var(--expire-critical);animation:pulse 2s ease-in-out infinite; }
.expire-tag.expired { background:rgba(155,89,182,0.15);color:var(--expire-expired);text-decoration:line-through; }
.shelf-tag { display:inline-block;padding:2px 8px;border-radius:4px;font-size:11px;background:rgba(144,147,153,0.15);color:#909399;margin-right:4px }

/* 弹窗左右分栏 */
.upload-area { border:2px dashed var(--primary);border-radius:var(--radius-md);padding:36px 16px;text-align:center;cursor:pointer;transition:all 0.3s ease;background:rgba(255,245,247,0.3); }
.upload-area:hover { border-color:var(--primary-dark);background:rgba(255,245,247,0.5); }
.upload-icon { font-size:40px;color:var(--primary);margin-bottom:8px; }
.upload-text { font-size:13px;color:var(--text-secondary); }
.upload-hint { font-size:11px;color:var(--text-light);margin-top:4px; }

.preview-card { background:var(--glass-bg);border:2px solid var(--glass-border);border-radius:var(--radius-lg);padding:20px;text-align:center; }
.preview-img { width:100px;height:100px;margin:0 auto 12px;border-radius:var(--radius-md);background:var(--bg-page);display:flex;align-items:center;justify-content:center;overflow:hidden; }
.preview-img img { width:100%;height:100%;object-fit:cover; }
.preview-name { font-size:16px;font-weight:600;color:var(--text-primary); }
.preview-info { display:flex;justify-content:space-around;margin-top:12px;padding-top:12px;border-top:1px solid var(--border-light); }
.preview-info-item .label { font-size:11px;color:var(--text-secondary); } .preview-info-item .val { font-size:15px;font-weight:600;color:var(--text-primary); }

.empty-hint { grid-column:1/-1;text-align:center;padding:60px 20px;color:var(--text-secondary); }
.empty-icon { font-size:64px;display:block;margin-bottom:12px; }

.pagination { margin-top:20px;display:flex;justify-content:flex-end; }
</style>
