<template>
  <div class="snack-page">
    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="搜索零食名称" clearable style="width:200px"
        @clear="handleSearch" @keyup.enter="handleSearch" />
      <el-select v-model="query.categoryId" placeholder="分类筛选" clearable style="width:160px" @change="handleSearch">
        <el-option v-for="c in categoryList" :key="c.id" :label="c.icon + ' ' + c.name" :value="c.id" />
      </el-select>
      <el-select v-model="query.expiryStatus" placeholder="过期状态" clearable style="width:140px" @change="handleSearch">
        <el-option label="已过期" value="expired" /><el-option label="即将过期" value="soon" /><el-option label="新鲜" value="fresh" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button type="success" @click="openDialog(null)">新增零食</el-button>
      <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除 ({{ selectedIds.length }})</el-button>
    </div>

    <el-table :data="snacks" v-loading="loading" stripe empty-text="暂无零食"
      @selection-change="(rows: SnackVO[]) => selectedIds = rows.map(r => r.id)">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="name" label="名称" min-width="130" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="quantity" label="数量" width="150">
        <template #default="{ row }">
          <el-button size="small" circle @click="handleQuantity(row, -1)">-</el-button>
          <span style="margin:0 6px">{{ row.quantity }} {{ row.unit }}</span>
          <el-button size="small" circle @click="handleQuantity(row, 1)">+</el-button>
          <el-button size="small" style="margin-left:4px" @click="openAdjust(row)">调整</el-button>
        </template>
      </el-table-column>
      <el-table-column label="单价/总价" width="120">
        <template #default="{ row }">
          <span v-if="row.price != null">¥{{ row.price }} / ¥{{ row.totalPrice }}</span>
          <span v-else style="color:#909399">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="过期日期" width="130">
        <template #default="{ row }">
          <el-tag v-if="row.expiryStatus === 'expired'" type="danger">{{ row.expiryDate || '无' }}</el-tag>
          <el-tag v-else-if="row.expiryStatus === 'soon'" type="warning">{{ row.expiryDate }}{{ row.daysUntilExpiry != null ? ` (${row.daysUntilExpiry}天)` : '' }}</el-tag>
          <el-tag v-else-if="row.expiryStatus === 'fresh'" type="success">{{ row.expiryDate || '无' }}</el-tag>
          <span v-else style="color:#909399">{{ row.expiryDate || '未设置' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="notes" label="备注" min-width="100" show-overflow-tooltip />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openDispose(row)">处理</el-button>
          <el-button size="small" type="info" @click="openCheck(row)">盘点</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :page-sizes="[10,20,50]"
        :total="total" layout="total,sizes,prev,pager,next" @current-change="fetchSnacks" @size-change="fetchSnacks" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑零食' : '新增零食'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" maxlength="100" /></el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categoryList" :key="c.id" :label="c.icon + ' ' + c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="0" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload :show-file-list="false" :before-upload="beforeImageUpload" :http-request="uploadImage" accept="image/*">
            <el-button size="small">上传图片</el-button>
          </el-upload>
          <img v-if="form.imageUrl" :src="form.imageUrl" style="max-width:100px;max-height:80px;margin-top:4px;border-radius:4px" />
        </el-form-item>
        <el-form-item label="单价"><el-input v-model="form.price" placeholder="选填" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" maxlength="20" placeholder="包" /></el-form-item>
        <el-form-item label="采购日期"><el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" /></el-form-item>
        <el-form-item label="过期日期"><el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.notes" type="textarea" maxlength="500" show-word-limit :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 盘点调整弹窗 -->
    <el-dialog v-model="adjustVisible" title="盘点调整库存" width="360px">
      <el-form label-width="80px">
        <el-form-item label="零食">{{ adjustSnack?.name }}</el-form-item>
        <el-form-item label="当前库存">{{ adjustSnack?.quantity }} {{ adjustSnack?.unit }}</el-form-item>
        <el-form-item label="调整为"><el-input-number v-model="adjustQty" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="adjustRemark" placeholder="盘点调整" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" :loading="adjustLoading" @click="handleAdjust">确认调整</el-button>
      </template>
    </el-dialog>

    <!-- 过期处理弹窗 -->
    <el-dialog v-model="disposeVisible" title="处理零食" width="380px">
      <el-form label-width="80px">
        <el-form-item label="零食">{{ disposeSnack?.name }}</el-form-item>
        <el-form-item label="当前库存">{{ disposeSnack?.quantity }} {{ disposeSnack?.unit }}</el-form-item>
        <el-form-item label="处理数量"><el-input-number v-model="disposeQty" :min="1" :max="disposeSnack?.quantity||0" /></el-form-item>
        <el-form-item label="原因">
          <el-select v-model="disposeReason" style="width:100%">
            <el-option label="已过期" value="EXPIRED" /><el-option label="已损坏" value="DAMAGED" /><el-option label="手动处理" value="MANUAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="disposeRemark" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="disposeVisible=false">取消</el-button><el-button type="primary" :loading="disposeLoading" @click="handleDispose">确认处理</el-button></template>
    </el-dialog>

    <!-- 库存盘点弹窗 -->
    <el-dialog v-model="checkVisible" title="库存盘点" width="380px">
      <el-form label-width="80px">
        <el-form-item label="零食">{{ checkSnack?.name }}</el-form-item>
        <el-form-item label="系统库存">{{ checkSnack?.quantity }} {{ checkSnack?.unit }}</el-form-item>
        <el-form-item label="实际库存"><el-input-number v-model="checkActualQty" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="checkRemark" placeholder="盘点备注" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="checkVisible=false">取消</el-button><el-button type="primary" :loading="checkLoading" @click="handleCheck">确认盘点</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getCategories } from '@/api/category'
import { getSnacks, createSnack, updateSnack, updateQuantity, adjustQuantity, deleteSnack, batchDeleteSnacks, uploadSnackImage, importSnacks } from '@/api/snack'
import { disposeSnack } from '@/api/disposal'
import { checkSnack as checkSnackApi } from '@/api/inventoryCheck'
import { ElLoading } from 'element-plus'
import type { CategoryVO } from '@/types/category'
import type { SnackVO, SnackCreateDTO, SnackQuery } from '@/types/snack'

const snacks = ref<SnackVO[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])
const categoryList = ref<CategoryVO[]>([])
const query = reactive<SnackQuery>({ page: 1, size: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const adjustVisible = ref(false)
const adjustLoading = ref(false)
const adjustSnack = ref<SnackVO | null>(null)
const adjustQty = ref(0)
const adjustRemark = ref('')

const defaultForm: any = { name: '', categoryId: 0, quantity: 1, unit: '包', price: '', imageUrl: '', purchaseDate: undefined, expiryDate: undefined, notes: '' }
const form = reactive<any>({ ...defaultForm })

const rules: FormRules = {
  name: [{ required: true, message: '请输入零食名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

onMounted(() => { loadCategories(); fetchSnacks() })

async function loadCategories() { try { const res = await getCategories(); categoryList.value = res.data } catch { /* ignore */ } }

async function fetchSnacks() {
  loading.value = true
  try { const res = await getSnacks({ ...query }); snacks.value = res.data.records; total.value = res.data.total } finally { loading.value = false }
}

function handleSearch() { query.page = 1; fetchSnacks() }

function openDialog(row: SnackVO | null) {
  if (row) {
    isEdit.value = true; editingId.value = row.id
    form.name = row.name; form.categoryId = row.categoryId; form.unit = row.unit
    form.price = row.price != null ? String(row.price) : ''
    form.imageUrl = row.imageUrl || ''; form.purchaseDate = row.purchaseDate || undefined; form.expiryDate = row.expiryDate || undefined; form.notes = row.notes
  } else {
    isEdit.value = false; editingId.value = null
    Object.assign(form, { ...defaultForm, categoryId: categoryList.value[0]?.id || 0 })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data: any = { name: form.name, categoryId: form.categoryId, unit: form.unit, imageUrl: form.imageUrl, purchaseDate: form.purchaseDate, expiryDate: form.expiryDate, notes: form.notes }
    if (form.price) data.price = Number(form.price)
    if (!isEdit.value) data.quantity = form.quantity
    if (isEdit.value) { await updateSnack(editingId.value!, data); ElMessage.success('更新成功') }
    else { await createSnack(data); ElMessage.success('创建成功') }
    dialogVisible.value = false
    await fetchSnacks()
  } finally { submitting.value = false }
}

async function handleQuantity(row: SnackVO, delta: number) {
  try { await updateQuantity(row.id, { delta }); await fetchSnacks() } catch { /* ignore */ }
}

function openAdjust(row: SnackVO) { adjustSnack.value = row; adjustQty.value = row.quantity; adjustRemark.value = ''; adjustVisible.value = true }

async function handleAdjust() {
  adjustLoading.value = true
  try { await adjustQuantity(adjustSnack.value!.id, { quantity: adjustQty.value, remark: adjustRemark.value || undefined }); ElMessage.success('库存调整成功'); adjustVisible.value = false; await fetchSnacks() }
  finally { adjustLoading.value = false }
}

const disposeVisible = ref(false); const disposeLoading = ref(false)
const disposeSnack = ref<SnackVO|null>(null); const disposeQty = ref(1); const disposeReason = ref('EXPIRED'); const disposeRemark = ref('')
function openDispose(row: SnackVO) { disposeSnack.value = row; disposeQty.value = 1; disposeReason.value = 'EXPIRED'; disposeRemark.value = ''; disposeVisible.value = true }
async function handleDispose() { disposeLoading.value = true; try { await disposeSnack(disposeSnack.value!.id, { quantity: disposeQty.value, reason: disposeReason.value as any, remark: disposeRemark.value || undefined }); ElMessage.success('处理成功'); disposeVisible.value = false; await fetchSnacks() } finally { disposeLoading.value = false } }

const checkVisible = ref(false); const checkLoading = ref(false)
const checkSnack = ref<SnackVO|null>(null); const checkActualQty = ref(0); const checkRemark = ref('')
function openCheck(row: SnackVO) { checkSnack.value = row; checkActualQty.value = row.quantity; checkRemark.value = ''; checkVisible.value = true }
async function handleCheck() { checkLoading.value = true; try { const res = await checkSnackApi(checkSnack.value!.id, { actualQty: checkActualQty.value, remark: checkRemark.value || undefined }); const d = res.data; ElMessage.success('盘点完成,'+(d.difference===0?'库存一致':d.difference>0?'盘盈 '+d.difference:'盘亏 '+Math.abs(d.difference))); checkVisible.value = false; await fetchSnacks() } finally { checkLoading.value = false } }

async function beforeImageUpload(file: File) { const ok = ['image/jpeg','image/png','image/webp'].includes(file.type); if(!ok)ElMessage.error('仅支持 jpg/png/webp'); else if(file.size>2*1024*1024)ElMessage.error('图片不能超过2MB'); return ok&&file.size<=2*1024*1024 }
async function uploadImage({ file }: any) { const fd = new FormData(); fd.append('file', file); const res = await uploadSnackImage(fd); form.imageUrl = res.data.url; ElMessage.success('上传成功') }

async function beforeImport(file: File) { return file.name.endsWith('.xlsx') }
async function handleImport({ file }: any) { const fd = new FormData(); fd.append('file', file); try { const res = await importSnacks(fd); const d = res.data; ElMessage.success('导入完成:成功'+d.successCount+'条,失败'+d.failCount+'条'); if(d.errors.length)ElMessage.warning(d.errors.slice(0,3).map((e:any)=>'第'+e.row+'行: '+e.reason).join('; ')); await fetchSnacks() } catch { ElMessage.error('导入失败') } }

async function handleDelete(id: number) {
  try { await ElMessageBox.confirm('确定删除该零食吗？', '删除确认', { type: 'warning' }); await deleteSnack(id); ElMessage.success('删除成功'); await fetchSnacks() } catch { /* 取消 */ }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 个零食吗？`, '批量删除确认', { type: 'warning' })
    await batchDeleteSnacks(selectedIds.value); ElMessage.success('删除成功'); selectedIds.value = []; await fetchSnacks()
  } catch { /* 取消 */ }
}
</script>

<style scoped>
.snack-page { background: #fff; border-radius: 8px; padding: 20px; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
