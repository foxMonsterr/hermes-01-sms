<template>
  <div class="page anim-slide-up">
    <div class="toolbar">
      <el-select v-model="query.changeType" placeholder="变动类型" clearable style="width:140px" @change="fetchRecords">
        <el-option label="入库" value="IN" /><el-option label="出库" value="OUT" /><el-option label="初始化" value="INIT" /><el-option label="调整" value="ADJUST" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" value-format="YYYY-MM-DD" style="width:260px" @change="fetchRecords" />
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="📊 库存流水" name="records">
        <el-table :data="records" stripe v-loading="loading" empty-text="暂无流水">
          <el-table-column prop="snackName" label="零食" min-width="120" />
          <el-table-column prop="changeType" label="类型" width="90">
            <template #default="{row}"><el-tag :type="typeTag(row.changeType)" size="small">{{typeLabel(row.changeType)}}</el-tag></template>
          </el-table-column>
          <el-table-column prop="changeQty" label="变动量" width="90"><template #default="{row}">{{row.changeQty>0?'+'+row.changeQty:row.changeQty}}</template></el-table-column>
          <el-table-column prop="beforeQty" label="变动前" width="80" /><el-table-column prop="afterQty" label="变动后" width="80" />
          <el-table-column prop="remark" label="备注" min-width="140" /><el-table-column prop="createTime" label="时间" width="160" />
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchRecords" /></div>
      </el-tab-pane>

      <el-tab-pane label="📋 盘点记录" name="checks">
        <div style="margin-bottom:12px"><el-date-picker v-model="checkDateRange" type="daterange" range-separator="至" value-format="YYYY-MM-DD" style="width:260px" @change="fetchChecks" /></div>
        <div class="stats-bar" v-if="checkStats"><span>总盘点: {{checkStats.totalCheckCount}}</span><span>有差异: {{checkStats.differenceCount}}</span><span>盘盈: +{{checkStats.totalPositiveDiff}}</span><span>盘亏: -{{checkStats.totalNegativeDiff}}</span></div>
        <el-table :data="checks" stripe v-loading="checkLoading" empty-text="暂无盘点记录">
          <el-table-column prop="snackName" label="零食" min-width="120" /><el-table-column prop="systemQty" label="系统库存" width="90" /><el-table-column prop="actualQty" label="实际库存" width="90" />
          <el-table-column prop="difference" label="差异" width="80"><template #default="{row}"><span :style="{color:row.difference>0?'#67C23A':row.difference<0?'#F56C6C':'#909399'}">{{row.difference>0?'+'+row.difference:row.difference}}</span></template></el-table-column>
          <el-table-column prop="remark" label="备注" min-width="120" /><el-table-column prop="checkDate" label="盘点日期" width="110" /><el-table-column prop="createTime" label="时间" width="160" />
        </el-table>
        <div class="pagination"><el-pagination v-model:current-page="checkPage" v-model:page-size="checkSize" :page-sizes="[10,20,50]" :total="checkTotal" layout="total,sizes,prev,pager,next" @change="fetchChecks" /></div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getStockRecords } from '@/api/stockRecord'
import { getInventoryChecks, getInventoryCheckStats } from '@/api/inventoryCheck'
import type { StockRecord } from '@/types/stockRecord'
import type { InventoryCheckRecord, InventoryCheckStats } from '@/types/inventoryCheck'

const activeTab=ref('records');const records=ref<StockRecord[]>([]);const total=ref(0);const loading=ref(false)
const dateRange=ref<[string,string]|null>(null)
const query=reactive({page:1,size:10,changeType:''as string,startDate:'',endDate:''})

onMounted(()=>{fetchRecords();fetchChecks()})

async function fetchRecords(){loading.value=true;try{query.startDate=dateRange.value?.[0]||'';query.endDate=dateRange.value?.[1]||'';const res=await getStockRecords(query);records.value=res.data.records;total.value=res.data.total}finally{loading.value=false}}
function typeLabel(t:string){return {INIT:'初始化',IN:'入库',OUT:'出库',ADJUST:'调整'}[t]||t}
function typeTag(t:string):any{return {IN:'success',OUT:'danger',INIT:'info',ADJUST:'warning'}[t]||'info'}

const checks=ref<InventoryCheckRecord[]>([]);const checkTotal=ref(0);const checkLoading=ref(false);const checkPage=ref(1);const checkSize=ref(10)
const checkDateRange=ref<[string,string]|null>(null);const checkStats=ref<InventoryCheckStats|null>(null)

async function fetchChecks(){checkLoading.value=true;try{const p:any={page:checkPage.value,size:checkSize.value};if(checkDateRange.value){p.startDate=checkDateRange.value[0];p.endDate=checkDateRange.value[1]};const [r,s]=await Promise.all([getInventoryChecks(p.page,p.size,p.startDate,p.endDate),getInventoryCheckStats(p)]);checks.value=r.data.records;checkTotal.value=r.data.total;checkStats.value=s.data}finally{checkLoading.value=false}}
</script>

<style scoped>
.page { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px; }
.toolbar { display:flex;gap:12px;margin-bottom:16px; }
.pagination { margin-top:16px;display:flex;justify-content:flex-end; }
.stats-bar { display:flex;gap:20px;padding:10px 14px;background:var(--bg-page);border-radius:var(--radius-sm);margin-bottom:12px;font-size:13px;color:var(--text-secondary); }
</style>
