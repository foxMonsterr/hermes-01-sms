<template>
  <div class="page">
    <div class="filters">
      <el-input v-model="keyword" placeholder="搜索零食..." clearable style="width:240px" @keyup.enter="fetch"/>
      <el-select v-model="categoryId" placeholder="全部分类" clearable style="width:160px" @change="fetch"><el-option v-for="c in cats" :key="c.id" :label="c.icon+' '+c.name" :value="c.id"/></el-select>
      <el-select v-model="sort" style="width:140px" @change="fetch"><el-option label="默认排序" value="default"/><el-option label="价格升序" value="price_asc"/><el-option label="价格降序" value="price_desc"/></el-select>
    </div>
    <div class="product-grid" v-loading="loading">
      <div v-if="products.length===0" class="empty">暂无上架零食</div>
      <div v-for="p in products" :key="p.id" class="product-card" @click="$router.push('/products/'+p.id)">
        <div class="card-img"><img v-if="p.imageUrl" :src="p.imageUrl"/><span v-else class="emoji">🍿</span></div>
        <div class="card-info"><div class="name">{{p.name}}</div><div class="cat">{{p.categoryName}}</div><div class="bottom"><span class="price">¥{{p.price}}</span><span v-if="!p.inStock" class="sold-out">已售罄</span></div></div>
      </div>
    </div>
    <div class="pager"><el-pagination v-model:current-page="page" :page-size="12" :total="total" layout="prev,pager,next" @change="fetch"/></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProducts, getCategories } from '@/api/products'
const route=useRoute();const products=ref<any[]>([]);const cats=ref<any[]>([]);const loading=ref(false);const total=ref(0);const page=ref(1)
const keyword=ref('');const categoryId=ref<number|null>(null);const sort=ref('default')
onMounted(()=>{if(route.query.keyword)keyword.value=String(route.query.keyword);if(route.query.categoryId)categoryId.value=Number(route.query.categoryId);fetch();getCategories().then(r=>cats.value=r.data)})
async function fetch(){loading.value=true;try{const r=await getProducts({page:page.value,size:12,keyword:keyword.value||undefined,categoryId:categoryId.value||undefined,sort:sort.value});products.value=r.data.records;total.value=r.data.total}finally{loading.value=false}}
</script>

<style scoped>
.page{padding:0}.filters{display:flex;gap:12px;margin-bottom:16px}
.product-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:16px}
.product-card{background:#fff;border-radius:12px;overflow:hidden;cursor:pointer;transition:transform 0.2s;box-shadow:0 2px 8px rgba(0,0,0,0.06)}
.product-card:hover{transform:translateY(-4px)}
.card-img{height:140px;display:flex;align-items:center;justify-content:center;background:#FAFAFA}.card-img img{width:100%;height:100%;object-fit:cover}.emoji{font-size:48px}
.card-info{padding:12px}.name{font-size:14px;font-weight:500}.cat{font-size:12px;color:#999;margin:4px 0}.bottom{display:flex;justify-content:space-between;align-items:center}.price{font-size:16px;color:#E8659A;font-weight:bold}.sold-out{font-size:12px;color:#ccc}
.pager{margin-top:20px;display:flex;justify-content:center}.empty{grid-column:1/-1;text-align:center;padding:40px;color:#999}
</style>
