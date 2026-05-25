import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/token'

const routes = [
  { path:'/', component:()=>import('@/layouts/ShopLayout.vue'), redirect:'/home', children:[
    { path:'home', name:'Home', component:()=>import('@/views/HomeView.vue') },
    { path:'products', name:'Products', component:()=>import('@/views/ProductListView.vue') },
    { path:'products/:id', name:'ProductDetail', component:()=>import('@/views/ProductDetailView.vue') },
    { path:'cart', name:'Cart', component:()=>import('@/views/CartView.vue'), meta:{auth:true} },
    { path:'checkout', name:'Checkout', component:()=>import('@/views/CheckoutView.vue'), meta:{auth:true} },
    { path:'orders', name:'Orders', component:()=>import('@/views/OrderListView.vue'), meta:{auth:true} },
    { path:'orders/:id', name:'OrderDetail', component:()=>import('@/views/OrderDetailView.vue'), meta:{auth:true} },
    { path:'login', name:'Login', component:()=>import('@/views/LoginView.vue') },
    { path:'register', name:'Register', component:()=>import('@/views/RegisterView.vue') },
    { path:'profile', name:'Profile', component:()=>import('@/views/ProfileView.vue'), meta:{auth:true} },
  ]},
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to,from,next)=>{
  if (to.meta.auth && !getToken()) next('/login')
  else next()
})

export default router
