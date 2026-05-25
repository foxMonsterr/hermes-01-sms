<template>
  <div class="page"><h2>📦 确认订单</h2>
    <el-card style="margin-bottom:16px"><h3>收货信息</h3>
      <el-form label-width="80px"><el-form-item label="收货人"><el-input v-model="form.receiver"/></el-form-item><el-form-item label="手机号"><el-input v-model="form.phone"/></el-form-item><el-form-item label="地址"><el-input v-model="form.address"/></el-form-item><el-form-item label="备注"><el-input v-model="form.remark"/></el-form-item></el-form>
    </el-card>
    <el-card><h3>商品清单</h3>
      <el-table :data="items" stripe><el-table-column prop="snackName" label="商品"/><el-table-column prop="price" label="单价" width="80"/><el-table-column prop="quantity" label="数量" width="60"/><el-table-column prop="subtotal" label="小计" width="80"/></el-table>
      <div class="footer"><span class="total">合计：¥{{total}}</span><el-button type="primary" size="large" :loading="submitting" @click="submit">提交订单</el-button></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCart } from '@/api/cart'
import { createOrder } from '@/api/orders'
const router=useRouter();const route=useRoute();const items=ref<any[]>([]);const submitting=ref(false)
const form=reactive({receiver:'',phone:'',address:'',remark:''})
const total=computed(()=>items.value.reduce((s,i)=>s+(i.subtotal||0),0).toFixed(2))
onMounted(async()=>{
  if(route.query.direct==='1'){
    // 直接购买模式：只买当前商品
    const r=await getCart();const all=r.data;
    const name=String(route.query.snackName||'');
    const target=all.find((i:any)=>i.snackName===name);
    if(target){items.value=[target]}else{ElMessage.error('商品已失效');router.push('/cart');return}
  }else{
    const r=await getCart();if(r.data.length===0){ElMessage.warning('购物车为空');router.push('/cart');return};items.value=r.data.filter((i:any)=>i.onShelf)
  }
})
async function submit(){if(!form.receiver||!form.phone||!form.address){ElMessage.warning('请填写收货信息');return};submitting.value=true;try{const r=await createOrder({cartItemIds:items.value.map(i=>i.id),...form});ElMessage.success('下单成功！');router.push('/orders/'+r.data.id)}finally{submitting.value=false}}
</script>

<style scoped>
.page{padding:0}.footer{display:flex;justify-content:flex-end;align-items:center;gap:16px;margin-top:16px}.total{font-size:18px;font-weight:bold;color:#E8659A}
</style>
