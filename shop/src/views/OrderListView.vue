<template>
  <div class="page"><h2>📋 我的订单</h2>
    <div style="margin-bottom:16px"><el-radio-group v-model="statusFilter" @change="fetch"><el-radio-button value="">全部</el-radio-button><el-radio-button value="PENDING_SHIP">待发货</el-radio-button><el-radio-button value="SHIPPED">已发货</el-radio-button><el-radio-button value="COMPLETED">已完成</el-radio-button><el-radio-button value="CANCELLED">已取消</el-radio-button></el-radio-group></div>
    <div v-if="orders.length===0" class="empty">暂无订单</div>
    <div v-else v-for="o in orders" :key="o.id" class="order-card" @click="$router.push('/orders/'+o.id)">
      <div class="order-header"><span>{{o.orderNo}}</span><el-tag :type="statusTag(o.status)" size="small">{{statusLabel(o.status)}}</el-tag></div>
      <div class="order-items"><div v-for="i in o.items" :key="i.id" class="item">{{i.snackName}} ×{{i.quantity}}  ¥{{i.subtotal}}</div></div>
      <div class="order-footer">共{{o.totalQuantity}}件 合计 <span class="price">¥{{o.totalAmount}}</span></div>
    </div>
    <div class="pager"><el-pagination v-model:current-page="page" :page-size="10" :total="total" layout="prev,pager,next" @change="fetch"/></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onMounted } from 'vue'
import { getOrders } from '@/api/orders'
const orders=ref<any[]>([]);const total=ref(0);const page=ref(1);const statusFilter=ref('')
onMounted(()=>fetch())
async function fetch(){const r=await getOrders({page:page.value,size:10,status:statusFilter.value||undefined});orders.value=r.data.records;total.value=r.data.total}
function statusLabel(s:string):string{return {PENDING_SHIP:'待发货',SHIPPED:'已发货',COMPLETED:'已完成',CANCELLED:'已取消'}[s]||s}
function statusTag(s:string):any{return {PENDING_SHIP:'warning',SHIPPED:'',COMPLETED:'success',CANCELLED:'info'}[s]||''}
</script>

<style scoped>
.page{padding:0}.empty{text-align:center;padding:60px;color:#999}
.order-card{background:#fff;border-radius:12px;padding:16px;margin-bottom:12px;cursor:pointer;transition:box-shadow 0.2s;box-shadow:0 2px 8px rgba(0,0,0,0.04)}
.order-card:hover{box-shadow:0 4px 16px rgba(0,0,0,0.08)}
.order-header{display:flex;justify-content:space-between;margin-bottom:8px;font-size:13px;color:#999}
.order-items{padding:8px 0;border-top:1px solid #f5f5f5;border-bottom:1px solid #f5f5f5}
.item{font-size:14px;padding:2px 0}
.order-footer{text-align:right;margin-top:8px;font-size:14px}.price{font-size:16px;color:#E8659A;font-weight:bold}
.pager{margin-top:20px;display:flex;justify-content:center}
</style>
