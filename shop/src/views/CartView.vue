<template>
  <div class="page"><h2><span class="cart-icon">🛒</span> 购物车 <span v-if="items.length" class="cart-count">{{items.length}} 件</span></h2>
    <div v-if="items.length===0" class="empty">购物车是空的，去逛逛吧~</div>
    <div v-else>
      <el-table :data="items" stripe><el-table-column label="商品" min-width="200"><template #default="{row}"><div style="display:flex;gap:12px;align-items:center"><img v-if="row.imageUrl" :src="row.imageUrl" style="width:60px;height:60px;border-radius:8px;object-fit:cover"/><div><div>{{row.snackName}}</div><div style="font-size:12px;color:#999" v-if="!row.onShelf">⚠️ 已下架，不可结算</div></div></div></template></el-table-column>
      <el-table-column prop="price" label="单价" width="100"><template #default="{row}">¥{{row.price}}</template></el-table-column>
      <el-table-column label="数量" width="140"><template #default="{row}"><el-input-number v-model="row.quantity" :min="1" :max="row.stock" size="small" @change="updateQty(row)"/></template></el-table-column>
      <el-table-column prop="subtotal" label="小计" width="100"><template #default="{row}">¥{{row.subtotal}}</template></el-table-column>
      <el-table-column label="操作" width="80"><template #default="{row}"><el-button size="small" type="danger" @click="remove(row.id)">删除</el-button></template></el-table-column></el-table>
      <div class="footer"><span class="total">合计：¥{{total}}</span><el-button type="primary" size="large" :disabled="!canCheckout" @click="$router.push('/checkout')">去结算</el-button></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCart, updateCartQty, removeFromCart } from '@/api/cart'
const items=ref<any[]>([]);const total=computed(()=>items.value.reduce((s,i)=>s+(i.subtotal||0),0).toFixed(2))
const canCheckout=computed(()=>items.value.some(i=>i.onShelf))
onMounted(async()=>{const r=await getCart();items.value=r.data})
async function updateQty(row:any){await updateCartQty(row.id,row.quantity);const r=await getCart();items.value=r.data}
async function remove(id:number){await removeFromCart(id);const r=await getCart();items.value=r.data;ElMessage.success('已删除')}
</script>

<style scoped>
.page{padding:0}.empty{text-align:center;padding:60px;color:#999}
.footer{display:flex;justify-content:flex-end;align-items:center;gap:16px;margin-top:16px;padding:16px;background:#fff;border-radius:12px}
.total{font-size:18px;font-weight:bold;color:#E8659A}
</style>
