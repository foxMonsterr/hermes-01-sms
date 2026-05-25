<template>
  <div class="page anim-slide-up">
    <h2 style="margin-bottom:16px">📦 店铺订单管理</h2>
    <div style="display:flex;gap:12px;margin-bottom:16px;flex-wrap:wrap">
      <el-input v-model="keyword" placeholder="搜索订单号" clearable style="width:220px" @keyup.enter="fetch" />
      <el-select v-model="statusFilter" placeholder="全部状态" clearable style="width:140px" @change="fetch">
        <el-option label="待发货" value="PENDING_SHIP" /><el-option label="已发货" value="SHIPPED" /><el-option label="已完成" value="COMPLETED" /><el-option label="已取消" value="CANCELLED" />
      </el-select>
    </div>
    <el-table :data="orders" stripe v-loading="loading" empty-text="暂无订单">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="receiver" label="收货人" width="100" />
      <el-table-column prop="totalAmount" label="金额" width="100"><template #default="{row}">¥{{row.totalAmount}}</template></el-table-column>
      <el-table-column prop="totalQuantity" label="件数" width="70" />
      <el-table-column prop="status" label="状态" width="90"><template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{statusLabel(row.status)}}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="160" />
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button v-if="row.status==='PENDING_SHIP'" size="small" type="primary" @click="handleAction(row.id,'ship')">发货</el-button>
          <el-button v-if="row.status==='SHIPPED'" size="small" type="success" @click="handleAction(row.id,'complete')">完成</el-button>
          <el-button v-if="row.status==='PENDING_SHIP'" size="small" type="danger" @click="handleAction(row.id,'cancel')">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetch" /></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { getToken } from '@/utils/token'

const orders=ref<any[]>([]);const total=ref(0);const loading=ref(false);const page=ref(1);const size=ref(10)
const keyword=ref('');const statusFilter=ref('')

const http = axios.create({ baseURL: '/api' })
http.interceptors.request.use(c=>{const t=getToken();if(t)c.headers.Authorization=`Bearer ${t}`;return c})
http.interceptors.response.use(r=>r.data, e=>{ElMessage.error(e.response?.data?.msg||'请求失败');return Promise.reject(e)})

onMounted(()=>fetch())
async function fetch(){loading.value=true;try{const p:any={page:page.value,size:size.value};if(statusFilter.value)p.status=statusFilter.value;if(keyword.value)p.keyword=keyword.value;const r=await http.get('/admin/shop-orders',{params:p});orders.value=(r.data as any).records;total.value=(r.data as any).total}finally{loading.value=false}}

async function handleAction(id:number,action:string){
  const labels:any={ship:'确认发货？',complete:'确认完成？',cancel:'确认取消？此操作会退回库存'}
  try{await ElMessageBox.confirm(labels[action],'操作确认',{type:'warning'});await http.patch(`/admin/shop-orders/${id}/${action}`);ElMessage.success('操作成功');fetch()}catch{}
}

function statusLabel(s:string):string{return {PENDING_SHIP:'待发货',SHIPPED:'已发货',COMPLETED:'已完成',CANCELLED:'已取消'}[s]||s}
function statusTag(s:string):any{return {PENDING_SHIP:'warning',SHIPPED:'',COMPLETED:'success',CANCELLED:'info'}[s]||''}
</script>

<style scoped>
.page { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px; }
.pagination { margin-top:16px;display:flex;justify-content:flex-end; }
</style>
