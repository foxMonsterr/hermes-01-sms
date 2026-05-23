<template>
  <div class="page">
    <div class="toolbar">
      <el-select v-model="filterAction" placeholder="操作类型" clearable style="width:160px" @change="fetchData">
        <el-option v-for="a in ACTION_OPTIONS" :key="a.value" :label="a.label" :value="a.value" />
      </el-select>
      <el-select v-model="filterTarget" placeholder="对象类型" clearable style="width:140px" @change="fetchData">
        <el-option v-for="t in TARGET_TYPE_OPTIONS" :key="t.value" :label="t.label" :value="t.value" />
      </el-select>
      <el-date-picker v-model="dateRange" type="daterange" range-separator="至" value-format="YYYY-MM-DD" style="width:260px" @change="fetchData" />
    </div>
    <el-table :data="list" stripe v-loading="loading" empty-text="暂无日志">
      <el-table-column prop="createTime" label="时间" width="160" />
      <el-table-column prop="username" label="用户" width="100" />
      <el-table-column prop="action" label="操作" width="120"><template #default="{row}">{{ actionLabel(row.action) }}</template></el-table-column>
      <el-table-column prop="targetType" label="对象" width="100" />
      <el-table-column prop="detail" label="详情" min-width="160" show-overflow-tooltip />
      <el-table-column prop="result" label="结果" width="80"><template #default="{row}"><el-tag :type="row.result==='SUCCESS'?'success':'danger'" size="small">{{row.result}}</el-tag></template></el-table-column>
    </el-table>
    <div class="pagination"><el-pagination v-model:current-page="page" v-model:page-size="size" :page-sizes="[10,20,50]" :total="total" layout="total,sizes,prev,pager,next" @change="fetchData" /></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getLogs } from '@/api/operationLog'
import { ACTION_OPTIONS, TARGET_TYPE_OPTIONS } from '@/types/operationLog'
import type { OperationLog } from '@/types/operationLog'

const list=ref<OperationLog[]>([]);const total=ref(0);const loading=ref(false)
const page=ref(1);const size=ref(10)
const filterAction=ref('');const filterTarget=ref('');const dateRange=ref<[string,string]|null>(null)

onMounted(()=>fetchData())
async function fetchData(){loading.value=true
  try{const p:any={page:page.value,size:size.value};if(filterAction.value)p.action=filterAction.value;if(filterTarget.value)p.targetType=filterTarget.value
    if(dateRange.value){p.startDate=dateRange.value[0];p.endDate=dateRange.value[1]}
    const res=await getLogs(p);list.value=res.data.records;total.value=res.data.total}finally{loading.value=false}}

function actionLabel(a:string){const item=ACTION_OPTIONS.find(x=>x.value===a);return item?item.label:a}
</script>

<style scoped>.page{background:#fff;border-radius:8px;padding:20px}.toolbar{display:flex;gap:12px;margin-bottom:16px}.pagination{margin-top:16px;display:flex;justify-content:flex-end}</style>
