<template>
  <div class="page">
    <div class="toolbar">
      <el-select v-model="filterType" placeholder="类型筛选" clearable style="width:160px" @change="fetchData">
        <el-option label="即将过期" value="EXPIRY_SOON" /><el-option label="已过期" value="EXPIRED" />
        <el-option label="低库存" value="LOW_STOCK" /><el-option label="已缺货" value="STOCK_OUT" />
      </el-select>
      <el-button @click="handleGenerate">刷新提醒</el-button>
      <el-button type="primary" @click="handleReadAll">全部已读</el-button>
    </div>

    <el-table :data="notifs" stripe v-loading="loading" empty-text="暂无提醒" @row-click="handleRead">
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }"><el-tag :type="typeTag(row.type)" size="small">{{ typeLabel(row.type) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="120" />
      <el-table-column prop="content" label="内容" min-width="200" />
      <el-table-column prop="snackName" label="零食" width="120" />
      <el-table-column prop="createTime" label="时间" width="160" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }"><el-button size="small" type="danger" @click.stop="handleDelete(row.id)">删除</el-button></template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10,20,50]"
        :total="total" layout="total,sizes,prev,pager,next" @change="fetchData" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, generateNotifications, markRead, markAllRead, deleteNotification } from '@/api/notification'
import { useNotificationStore } from '@/stores/notification'
import type { Notification } from '@/types/notification'

const notifStore = useNotificationStore()
const notifs = ref<Notification[]>([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const filterType = ref('')

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getNotifications(page.value, size.value, filterType.value || undefined)
    notifs.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleGenerate() {
  await generateNotifications()
  await fetchData()
  await notifStore.fetchUnread()
  ElMessage.success('提醒已刷新')
}

async function handleRead(row: Notification) {
  if (row.isRead) return
  await markRead(row.id)
  row.isRead = 1
  notifStore.fetchUnread()
}

async function handleReadAll() {
  await markAllRead()
  await fetchData()
  notifStore.fetchUnread()
  ElMessage.success('已全部标记已读')
}

async function handleDelete(id: number) {
  await deleteNotification(id)
  await fetchData()
  notifStore.fetchUnread()
}

function typeLabel(t: string) {
  return { EXPIRY_SOON: '即将过期', EXPIRED: '已过期', LOW_STOCK: '低库存', STOCK_OUT: '已缺货' }[t] || t
}
function typeTag(t: string): any {
  return { EXPIRY_SOON: 'warning', EXPIRED: 'danger', LOW_STOCK: 'info', STOCK_OUT: 'danger' }[t] || 'info'
}
</script>

<style scoped>
.page { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
