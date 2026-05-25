import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/BasicLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        meta: { requiresAuth: true },
        component: () => import('@/views/DashboardView.vue')
      },
      {
        path: 'categories',
        name: 'Categories',
        meta: { requiresAuth: true },
        component: () => import('@/views/CategoryListView.vue')
      },
      {
        path: 'snacks',
        name: 'Snacks',
        meta: { requiresAuth: true },
        component: () => import('@/views/SnackListView.vue')
      },
      {
        path: 'stock-records',
        name: 'StockRecords',
        meta: { requiresAuth: true },
        component: () => import('@/views/StockRecordView.vue')
      },
      {
        path: 'shopping-list',
        name: 'ShoppingList',
        meta: { requiresAuth: true },
        component: () => import('@/views/ShoppingListView.vue')
      },
      {
        path: 'statistics',
        name: 'Statistics',
        meta: { requiresAuth: true },
        component: () => import('@/views/StatisticsView.vue')
      },
      {
        path: 'notifications',
        name: 'Notifications',
        meta: { requiresAuth: true },
        component: () => import('@/views/NotificationView.vue')
      },
      {
        path: 'suppliers',
        name: 'Suppliers',
        meta: { requiresAuth: true },
        component: () => import('@/views/SupplierListView.vue')
      },
      {
        path: 'logs',
        name: 'OperationLog',
        meta: { requiresAuth: true },
        component: () => import('@/views/OperationLogView.vue')
      },
      {
        path: 'shop-orders',
        name: 'ShopOrders',
        meta: { requiresAuth: true },
        component: () => import('@/views/ShopOrderListView.vue')
      },
      {
        path: 'settings',
        name: 'Settings',
        meta: { requiresAuth: true },
        component: () => import('@/views/SettingsView.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
