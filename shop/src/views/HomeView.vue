<template>
  <div class="home">
    <div class="hero"><h1>🛒 零食小卖铺</h1><p>好好吃零食，好好享受生活 ~</p></div>
    <h3 style="margin:24px 0 12px">📂 分类浏览</h3>
    <div class="cat-row" v-loading="catLoading">
      <div v-for="c in cats" :key="c.id" class="cat-chip" @click="$router.push({path:'/products',query:{categoryId:c.id}})">{{c.icon}} {{c.name}}</div>
    </div>
    <h3 style="margin:24px 0 12px">🔥 热销零食</h3>
    <div class="product-grid" v-loading="loading">
      <div v-if="products.length===0" class="empty">暂无上架零食</div>
      <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/products/'+p.id)">
        <div class="card-img"><img v-if="p.imageUrl" :src="p.imageUrl"/><span v-else class="emoji">🍿</span></div>
        <div class="card-info"><div class="name">{{p.name}}</div><div class="price">¥{{p.price}}</div></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProducts, getCategories } from '@/api/products'
const products=ref<any[]>([]);const cats=ref<any[]>([]);const loading=ref(false);const catLoading=ref(false)
onMounted(async()=>{loading.value=true;try{const r=await getProducts({page:1,size:8,sort:'default'});products.value=r.data.records}finally{loading.value=false};catLoading.value=true;try{const r=await getCategories();cats.value=r.data}finally{catLoading.value=false}})
</script>

<style scoped>
.hero{text-align:center;padding:40px 20px;background:linear-gradient(135deg,#FFE0EC,#FFF5F5);border-radius:16px;margin-bottom:8px}
.hero h1{font-size:28px;color:#E8659A;margin-bottom:8px}.hero p{color:#999}
.cat-row{display:flex;flex-wrap:wrap;gap:10px}
.cat-chip{padding:8px 16px;background:#fff;border-radius:20px;cursor:pointer;border:1px solid #f0f0f0;transition:all 0.2s;font-size:14px}
.cat-chip:hover{border-color:#E8659A;color:#E8659A}
.product-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px}
.product-card{background:#fff;border-radius:12px;overflow:hidden;cursor:pointer;transition:transform 0.2s;box-shadow:0 2px 8px rgba(0,0,0,0.06)}
.product-card:hover{transform:translateY(-4px)}
.card-img{height:140px;display:flex;align-items:center;justify-content:center;background:#FAFAFA}
.card-img img{width:100%;height:100%;object-fit:cover}.emoji{font-size:48px}
.card-info{padding:12px}.name{font-size:14px;font-weight:500}.price{font-size:16px;color:#E8659A;font-weight:bold;margin-top:4px}
.empty{grid-column:1/-1;text-align:center;padding:40px;color:#999}
</style>
