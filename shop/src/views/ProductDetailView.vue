<template>
  <div class="detail" v-loading="loading">
    <div v-if="!product" class="empty">商品不存在或已下架</div>
    <template v-else>
      <el-row :gutter="32">
        <el-col :span="10"><div class="detail-img"><img v-if="product.imageUrl" :src="product.imageUrl"/><span v-else style="font-size:80px">🍿</span></div></el-col>
        <el-col :span="14">
          <h1>{{product.name}}</h1><p class="cat">{{product.categoryName}}</p>
          <p class="price">¥{{product.price}} <span class="unit">/ {{product.unit}}</span></p>
          <p class="desc" v-if="product.description">{{product.description}}</p>
          <p class="stock">库存：{{product.quantity}} {{product.unit}} <span v-if="!product.inStock" class="sold-out">已售罄</span></p>
          <div style="margin-top:20px;display:flex;gap:12px">
            <el-input-number v-model="qty" :min="1" :max="product.quantity||1" :disabled="!product.inStock"/>
            <el-button type="primary" size="large" :disabled="!product.inStock" @click="addCart">🛒 加入购物车</el-button>
            <el-button type="danger" size="large" :disabled="!product.inStock" @click="buyNow">⚡ 立即购买</el-button>
          </div>
        </el-col>
      </el-row>
      <!-- 评价区 -->
      <div class="review-section" style="margin-top:32px">
        <h3>💬 商品评价 ({{reviews.length}})</h3>
        <div v-if="reviews.length===0" style="color:#999;padding:20px 0">暂无评价，购买后可在订单详情中评价~</div>
        <div v-for="r in reviews" :key="r.id" class="review-item">
          <div class="review-header"><span class="review-user">{{r.username}}</span><span class="review-time">{{r.createTime}}</span></div>
          <div class="review-content">{{r.content}}</div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '@/api/products'
import { addToCart } from '@/api/cart'
import request from '@/utils/request'
const route=useRoute();const router=useRouter()
const product=ref<any>(null);const loading=ref(false);const qty=ref(1)
const reviews=ref<any[]>([])
onMounted(async()=>{loading.value=true;try{const r=await getProductDetail(Number(route.params.id));product.value=r.data;loadReviews()}finally{loading.value=false}})
async function addCart(){try{await addToCart(product.value.id,qty.value);ElMessage.success('已加入购物车')}catch(e:any){if(e.response?.status===401)router.push('/login')}}
async function buyNow(){try{await addToCart(product.value.id,qty.value);router.push({path:'/checkout',query:{direct:'1',snackName:product.value.name,qty:qty.value}})}catch(e:any){if(e.response?.status===401)router.push('/login')}}
async function loadReviews(){try{const r=await request.get('/shop/products/'+product.value.id+'/reviews');reviews.value=(r.data as any)||[]}catch{}}
</script>

<style scoped>
.detail{background:#fff;border-radius:16px;padding:32px}
.detail-img{width:100%;height:300px;display:flex;align-items:center;justify-content:center;background:#FAFAFA;border-radius:12px;overflow:hidden}
.detail-img img{width:100%;height:100%;object-fit:cover}
h1{font-size:24px;margin-bottom:8px}.cat{color:#999;margin-bottom:12px}.price{font-size:28px;color:#E8659A;font-weight:bold}.unit{font-size:14px;color:#999;font-weight:normal}.desc{color:#666;margin-top:12px;line-height:1.6}.stock{color:#666;margin-top:8px}.sold-out{color:#ccc}.empty{text-align:center;padding:60px;color:#999}
.review-section{margin-top:32px;border-top:1px solid #f0f0f0;padding-top:24px}
.review-item{padding:12px 0;border-bottom:1px solid #f5f5f5}
.review-header{display:flex;justify-content:space-between;margin-bottom:4px}
.review-user{font-weight:600;color:#333}.review-time{font-size:12px;color:#999}
.review-content{font-size:14px;color:#555;line-height:1.6}
</style>
