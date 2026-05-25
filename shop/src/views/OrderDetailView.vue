<template>
  <div class="page" v-loading="loading">
    <template v-if="order">
      <div class="status-bar"><span>📦 订单 {{order.orderNo}}</span><el-tag :type="statusTag(order.status)">{{statusLabel(order.status)}}</el-tag></div>
      <el-card style="margin-bottom:12px"><h4>收货信息</h4><p>{{order.receiver}} {{order.phone}}</p><p>{{order.address}}</p></el-card>
      <el-card style="margin-bottom:12px"><h4>商品清单</h4>
        <div v-for="i in order.items" :key="i.id" class="item">
          <img v-if="i.imageUrl" :src="i.imageUrl" style="width:50px;height:50px;border-radius:8px;object-fit:cover;margin-right:10px"/>
          <span style="flex:1">{{i.snackName}} ×{{i.quantity}}{{i.unit}} ¥{{i.subtotal}}</span>
          <el-button v-if="order.status==='COMPLETED'" size="small" type="warning" @click="openReview(i)">💬 评价</el-button>
        </div>
        <div class="total">共{{order.totalQuantity}}件 合计 <span class="price">¥{{order.totalAmount}}</span></div>
      </el-card>
      <div class="actions">
        <el-button v-if="order.status==='PENDING_SHIP'" type="danger" @click="cancel">取消订单</el-button>
        <el-button v-if="order.status==='SHIPPED'" type="primary" @click="confirm">确认收货</el-button>
      </div>
    </template>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewVisible" title="💬 发表评价" width="420px">
      <p style="margin-bottom:8px;color:#666">商品：{{reviewItem?.snackName}}</p>
      <el-input v-model="reviewText" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="说说你的感受吧~"/>
      <template #footer><el-button @click="reviewVisible=false">取消</el-button><el-button type="primary" :loading="reviewing" @click="submitReview">发表</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderDetail, cancelOrder, confirmReceive } from '@/api/orders'
import request from '@/utils/request'
const route=useRoute();const order=ref<any>(null);const loading=ref(false)
const reviewVisible=ref(false);const reviewText=ref('');const reviewing=ref(false);const reviewItem=ref<any>(null)

onMounted(async()=>{loading.value=true;try{const r=await getOrderDetail(Number(route.params.id));order.value=r.data}finally{loading.value=false}})

async function cancel(){try{await ElMessageBox.confirm('确定取消订单吗？','取消确认',{type:'warning'});await cancelOrder(order.value.id);ElMessage.success('已取消');const r=await getOrderDetail(order.value.id);order.value=r.data}catch{}}
async function confirm(){try{await ElMessageBox.confirm('确认已收到商品？','确认收货',{type:'success'});await confirmReceive(order.value.id);ElMessage.success('已确认收货');const r=await getOrderDetail(order.value.id);order.value=r.data}catch{}}

function openReview(item:any){reviewItem.value=item;reviewText.value='';reviewVisible.value=true}
async function submitReview(){if(!reviewText.value){ElMessage.warning('请输入评价内容');return};reviewing.value=true
  try{await request.post('/shop/products/'+reviewItem.value.snackId+'/reviews',{orderId:order.value.id,content:reviewText.value});ElMessage.success('评价成功');reviewVisible.value=false;const r=await getOrderDetail(order.value.id);order.value=r.data}finally{reviewing.value=false}}

function statusLabel(s:string):string{return {PENDING_SHIP:'待发货',SHIPPED:'已发货',COMPLETED:'已完成',CANCELLED:'已取消'}[s]||s}
function statusTag(s:string):any{return {PENDING_SHIP:'warning',SHIPPED:'',COMPLETED:'success',CANCELLED:'info'}[s]||''}
</script>

<style scoped>
.page{padding:0}.status-bar{display:flex;justify-content:space-between;align-items:center;padding:16px;background:#fff;border-radius:12px;margin-bottom:16px;font-size:16px;font-weight:bold}
.item{display:flex;align-items:center;padding:8px 0;border-bottom:1px solid #f5f5f5}.total{text-align:right;margin-top:8px;font-size:14px}.price{font-size:16px;color:#E8659A;font-weight:bold}.actions{text-align:right;margin-top:16px}
</style>
