<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="cards">
      <el-col :span="4" v-for="c in statCards" :key="c.label">
        <el-card shadow="hover"><div class="card-label">{{ c.label }}</div><div class="card-value" :style="{color:c.color}">{{ c.value }}</div></el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 + 分类饼图 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="14">
        <el-card><template #header>近7天出入库趋势</template><div ref="trendRef" style="height:300px" v-loading="trendLoading" /></el-card>
      </el-col>
      <el-col :span="10">
        <el-card><template #header>分类占比</template><div ref="pieRef" style="height:300px" v-loading="pieLoading" /></el-card>
      </el-col>
    </el-row>

    <!-- 最近操作 -->
    <el-card style="margin-top:16px" v-loading="recentsLoading">
      <template #header>最近操作</template>
      <el-timeline v-if="recents.length > 0">
        <el-timeline-item v-for="r in recents" :key="r.id" :timestamp="r.createTime" placement="top">
          {{ r.snackName }} {{ r.changeQty > 0 ? '+' + r.changeQty : r.changeQty }} (剩余{{ r.afterQty }})
        </el-timeline-item>
      </el-timeline>
      <span v-else style="color:#909399">暂无操作记录</span>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsOverview, getCategoryDistribution, getStockTrend } from '@/api/statistics'
import { getStockRecordStats } from '@/api/stockRecord'
import { getSystemConfig } from '@/api/systemConfig'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const userStore = useUserStore()
const notifStore = useNotificationStore()
const trendRef = ref()
const pieRef = ref()
const trendLoading = ref(false)
const pieLoading = ref(false)
const recentsLoading = ref(false)
const recents = ref<any[]>([])

const statCards = ref([
  { label: '零食种类', value: 0, color: '#409EFF' },
  { label: '库存总量', value: 0, color: '#67C23A' },
  { label: '库存价值', value: '¥0', color: '#E6A23C' },
  { label: '即将过期', value: 0, color: '#F56C6C' },
  { label: '低库存', value: 0, color: '#909399' },
  { label: '今日入库', value: 0, color: '#409EFF' },
])

onMounted(async () => {
  await loadOverview()
  await loadTrend()
  await loadPie()
  await loadRecents()
  notifStore.generate().catch(() => {})
  notifStore.fetchUnread().catch(() => {})
})

async function loadOverview() {
  try {
    const res = await getStatisticsOverview()
    const d = res.data
    statCards.value[0].value = d.totalSnackCount
    statCards.value[1].value = d.totalQuantity
    statCards.value[2].value = d.totalValue != null ? '¥' + d.totalValue : '¥0'
    statCards.value[3].value = d.soonExpiredCount
    statCards.value[4].value = d.lowStockCount
    statCards.value[5].value = d.todayInQty || 0
  } catch { /* ignore */ }
}

async function loadTrend() {
  trendLoading.value = true
  try {
    const res = await getStockTrend('7d')
    await nextTick()
    if (!trendRef.value) return
    const chart = echarts.init(trendRef.value)
    const dates = res.data.dailyData.map((d: any) => d.date)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['入库', '出库'] },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value' },
      series: [
        { name: '入库', type: 'line', data: res.data.dailyData.map((d: any) => d.inQty), smooth: true, color: '#67C23A' },
        { name: '出库', type: 'line', data: res.data.dailyData.map((d: any) => d.outQty), smooth: true, color: '#F56C6C' }
      ]
    })
  } finally { trendLoading.value = false }
}

async function loadPie() {
  pieLoading.value = true
  try {
    const res = await getCategoryDistribution()
    await nextTick()
    if (!pieRef.value) return
    const chart = echarts.init(pieRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: res.data.map((d: any) => ({ name: d.categoryName, value: d.snackCount }))
      }]
    })
  } finally { pieLoading.value = false }
}

async function loadRecents() {
  recentsLoading.value = true
  try {
    const res = await getStockRecordStats()
    recents.value = res.data.recentRecords || []
  } finally { recentsLoading.value = false }
}
</script>

<style scoped>
.dashboard { max-width: 1400px; }
.cards .el-card { text-align: center; }
.card-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.card-value { font-size: 28px; font-weight: bold; }
</style>
