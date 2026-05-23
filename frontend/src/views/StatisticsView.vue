<template>
  <div class="stats-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="库存统计" name="overview">
        <el-row :gutter="16" class="cards">
          <el-col :span="4" v-for="c in cards" :key="c.label"><el-card shadow="hover"><div class="card-label">{{c.label}}</div><div class="card-value" :style="{color:c.color}">{{c.value}}</div></el-card></el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="14"><el-card><template #header>出入库趋势<el-radio-group v-model="period" size="small" style="margin-left:16px" @change="loadTrend"><el-radio-button value="7d">7天</el-radio-button><el-radio-button value="30d">30天</el-radio-button><el-radio-button value="90d">90天</el-radio-button></el-radio-group></template><div ref="trendRef" style="height:320px" v-loading="trendLoading" /></el-card></el-col>
          <el-col :span="10"><el-card v-loading="valueLoading"><template #header>库存价值 (¥{{totalValue}})</template><el-table :data="catValues" stripe size="small"><el-table-column prop="categoryName" label="分类"/><el-table-column prop="totalValue" label="价值" width="100"><template #default="{row}">¥{{row.totalValue}}</template></el-table-column><el-table-column prop="percentage" label="占比" width="80"><template #default="{row}">{{row.percentage}}%</template></el-table-column></el-table><div style="margin-top:8px;font-size:13px;color:#909399">最贵 Top 5:</div><div v-for="p in priciest" :key="p.snackName" style="font-size:13px">¥{{p.totalPrice}} — {{p.snackName}}</div></el-card></el-col>
        </el-row>
        <el-card style="margin-top:16px" v-loading="distLoading"><template #header>分类分布</template><el-table :data="distribution" stripe empty-text="暂无数据"><el-table-column prop="categoryName" label="分类"/><el-table-column prop="snackCount" label="零食种类" width="120"/><el-table-column prop="totalQuantity" label="总数量" width="120"/></el-table></el-card>
      </el-tab-pane>

      <el-tab-pane label="消耗分析" name="consumption">
        <div style="margin-bottom:12px"><el-radio-group v-model="consPeriod" size="small" @change="loadConsumption"><el-radio-button value="7d">7天</el-radio-button><el-radio-button value="30d">30天</el-radio-button><el-radio-button value="90d">90天</el-radio-button></el-radio-group></div>
        <el-row :gutter="16"><el-col :span="6" v-for="c in consCards" :key="c.label"><el-card shadow="hover"><div class="card-label">{{c.label}}</div><div class="card-value" :style="{color:c.color}">{{c.value}}</div></el-card></el-col></el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12"><el-card><template #header>消耗最多零食 Top 5</template><el-table :data="topSnacks" stripe size="small" empty-text="暂无"><el-table-column prop="snackName" label="零食"/><el-table-column prop="quantity" label="数量" width="80"/></el-table></el-card></el-col>
          <el-col :span="12"><el-card><template #header>消耗最多分类 Top 5</template><el-table :data="topCats" stripe size="small" empty-text="暂无"><el-table-column prop="categoryName" label="分类"/><el-table-column prop="quantity" label="数量" width="80"/></el-table></el-card></el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="损耗统计" name="disposal">
        <div style="margin-bottom:12px"><el-date-picker v-model="dispDateRange" type="daterange" range-separator="至" value-format="YYYY-MM-DD" @change="loadDisposal" /></div>
        <el-row :gutter="16"><el-col :span="6" v-for="c in dispCards" :key="c.label"><el-card shadow="hover"><div class="card-label">{{c.label}}</div><div class="card-value" :style="{color:c.color}">{{c.value}}</div></el-card></el-col></el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getStatisticsOverview, getCategoryDistribution, getStockTrend, getValueStats } from '@/api/statistics'
import { getDisposalStats } from '@/api/disposal'
import type { DisposalStats } from '@/types/disposal'
import request from '@/utils/request'
import type { Result } from '@/types/common'

const activeTab = ref('overview')
const cards = ref([{label:'零食种类',value:0,color:'#409EFF'},{label:'库存总量',value:0,color:'#67C23A'},{label:'库存价值',value:'¥0',color:'#E6A23C'},{label:'即将过期',value:0,color:'#F56C6C'},{label:'低库存',value:0,color:'#909399'},{label:'今日入库',value:0,color:'#409EFF'}])
const period = ref('7d');const trendRef = ref();const trendLoading=ref(false);const valueLoading=ref(false);const distLoading=ref(false)
const totalValue=ref('0');const catValues=ref<any[]>([]);const priciest=ref<any[]>([]);const distribution=ref<any[]>([])

onMounted(async()=>{await loadOverview();await loadTrend();await loadValue();await loadDist()})
async function loadOverview(){try{const res=await getStatisticsOverview();const d=res.data;cards.value[0].value=d.totalSnackCount;cards.value[1].value=d.totalQuantity;cards.value[2].value=d.totalValue!=null?'¥'+d.totalValue:'¥0';cards.value[3].value=d.soonExpiredCount;cards.value[4].value=d.lowStockCount;cards.value[5].value=d.todayInQty||0}catch{}}
async function loadTrend(){trendLoading.value=true;try{const res=await getStockTrend(period.value);await nextTick();if(!trendRef.value)return;const c=echarts.init(trendRef.value);c.setOption({tooltip:{trigger:'axis'},legend:{data:['入库','出库']},xAxis:{type:'category',data:res.data.dailyData.map((d:any)=>d.date)},yAxis:{type:'value'},series:[{name:'入库',type:'bar',data:res.data.dailyData.map((d:any)=>d.inQty),color:'#67C23A'},{name:'出库',type:'bar',data:res.data.dailyData.map((d:any)=>d.outQty),color:'#F56C6C'}]})}finally{trendLoading.value=false}}
async function loadValue(){valueLoading.value=true;try{const res=await getValueStats();totalValue.value=res.data.totalValue!=null?String(res.data.totalValue):'0';catValues.value=res.data.categoryValues||[];priciest.value=res.data.priciestSnacks||[]}finally{valueLoading.value=false}}
async function loadDist(){distLoading.value=true;try{const res=await getCategoryDistribution();distribution.value=res.data}finally{distLoading.value=false}}

// consumption
const consPeriod=ref('30d');const consCards=ref([{label:'总消耗量',value:0,color:'#409EFF'},{label:'日均消耗',value:0,color:'#67C23A'},{label:'消耗金额',value:'¥0',color:'#E6A23C'}])
const topSnacks=ref<any[]>([]);const topCats=ref<any[]>([])
async function loadConsumption(){try{const res:Result<any>=await request.get('/statistics/consumption-analysis',{params:{period:consPeriod.value}});const d=res.data;consCards.value[0].value=d.totalConsumedQty;consCards.value[1].value=d.averageDailyConsumed;consCards.value[2].value=d.estimatedConsumedValue!=null?'¥'+d.estimatedConsumedValue:'¥0';topSnacks.value=d.topSnacks||[];topCats.value=d.topCategories||[]}catch{}}

// disposal
const dispDateRange=ref<[string,string]|null>(null)
const dispCards=ref([{label:'总丢弃量',value:0,color:'#F56C6C'},{label:'过期丢弃',value:0,color:'#E6A23C'},{label:'损坏丢弃',value:0,color:'#909399'},{label:'损耗金额',value:'¥0',color:'#F56C6C'}])
async function loadDisposal(){try{const p:any={};if(dispDateRange.value){p.startDate=dispDateRange.value[0];p.endDate=dispDateRange.value[1]};const res=await getDisposalStats(p);const d=res.data;dispCards.value[0].value=d.totalDisposedQty;dispCards.value[1].value=d.expiredDisposedQty;dispCards.value[2].value=d.damagedDisposedQty;dispCards.value[3].value=d.estimatedLoss!=null?'¥'+d.estimatedLoss:'¥0'}catch{}}
</script>

<style scoped>.stats-page h2{margin-bottom:16px}.cards{margin-bottom:16px}.cards .el-card{text-align:center}.card-label{font-size:13px;color:#909399;margin-bottom:8px}.card-value{font-size:26px;font-weight:bold}</style>
