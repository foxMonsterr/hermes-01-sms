<template>
  <div class="dashboard anim-slide-up">
    <!-- 欢迎语 -->
    <div class="welcome">
      <span class="welcome-bear">🐻</span>
      <span class="welcome-text">{{ welcomeMsg }}</span>
      <span class="welcome-date">{{ today }}</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="4" v-for="c in statCards" :key="c.label">
        <div class="stat-card" :style="{'--bar-color': c.color}" @click="c.link&&router.push(c.link)">
          <div class="stat-icon">{{ c.icon }}</div>
          <div class="stat-value" :style="{color: c.color}">{{ c.value }}</div>
          <div class="stat-label">{{ c.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="14">
        <div class="chart-card" v-loading="trendLoading">
          <div class="chart-header">
            <span>📈 近7天出入库趋势</span>
            <el-radio-group v-model="trendPeriod" size="small" @change="loadTrend">
              <el-radio-button value="7d">7天</el-radio-button>
              <el-radio-button value="30d">30天</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="trendRef" style="height:300px" />
        </div>
      </el-col>
      <el-col :span="10">
        <div class="chart-card" v-loading="pieLoading">
          <div class="chart-header">🥧 分类占比</div>
          <div ref="pieRef" style="height:300px" />
        </div>
      </el-col>
    </el-row>

    <!-- 最近动态 + 小贴士 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="14">
        <div class="chart-card" v-loading="recentsLoading">
          <div class="chart-header">⚡ 最近动态</div>
          <div v-if="recents.length===0" class="empty-hint">🐱 还没有操作记录喵~</div>
          <div v-else class="activity-list">
            <div v-for="r in recents" :key="r.id" class="activity-item" :class="r.changeType==='OUT'?'out':r.changeType==='IN'?'in':''">
              <span class="activity-icon">{{ r.changeType==='IN'?'➕':r.changeType==='OUT'?'➖':'📋' }}</span>
              <span class="activity-text">{{ r.snackName }} {{ r.changeQty>0?'+'+r.changeQty:r.changeQty }}</span>
              <span class="activity-time">{{ r.createTime }}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="tip-card">
          <div class="tip-icon">💡</div>
          <div class="tip-text">{{ dailyTip }}</div>
          <div class="tip-author">— 🐻 小熊店长</div>
        </div>
      </el-col>
    </el-row>

    <!-- 兔子播报 -->
    <div class="bunny-bar" v-if="bunnyMsg">
      <span class="bunny-icon">🐰</span>
      <span>{{ bunnyMsg }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getStatisticsOverview, getCategoryDistribution, getStockTrend } from '@/api/statistics'
import { getStockRecordStats } from '@/api/stockRecord'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const notifStore = useNotificationStore()
const trendRef = ref(); const pieRef = ref()
const trendLoading=ref(false); const pieLoading=ref(false); const recentsLoading=ref(false)
const recents=ref<any[]>([]); const trendPeriod=ref('7d')

// 欢迎语
const today = computed(() => new Date().toLocaleDateString('zh-CN',{year:'numeric',month:'long',day:'numeric',weekday:'long'}))
const welcomeMsg=computed(()=>{const h=new Date().getHours();const g=h<12?['早上好呀！☀️ 新的一天开始啦~','元气满满的早晨~']:h<18?['下午好呀！🌤️ 午后的阳光真好~','继续加油哦！']:['晚上好呀！🌙 辛苦了一天~'];return g[Math.floor(Math.random()*g.length)]})

// 统计卡片
const statCards=ref([{icon:'📦',label:'零食种类',value:0,color:'#FF7EB3',link:'/snacks'},{icon:'📊',label:'库存总量',value:0,color:'#6BCB77',link:'/snacks'},{icon:'💰',label:'库存价值',value:'¥0',color:'#E6A23C',link:'/statistics'},{icon:'⚠️',label:'低库存',value:0,color:'#FF6B6B',link:'/snacks'},{icon:'🎂',label:'即将过期',value:0,color:'#9B59B6',link:'/snacks'},{icon:'📥',label:'今日入库',value:0,color:'#4D96FF',link:'/stock-records'}])

// 每日小贴士
const tips=[ '巧克力放在阴凉处会更美味哦~','薯片开封后记得夹紧袋口，保持酥脆！','饼干受潮了？放一片面包进去就能恢复酥脆~','坚果类零食最好放在密封罐里保存','碳酸饮料冷藏后口感更佳，但不要冷冻哦','肉干类零食开封后要尽快食用','果冻放在冰箱冷藏，口感会更加Q弹','零食分类存放，找起来更方便呢~','晚上吃零食要适量哦，小熊关心你的健康','早上来包坚果，一天都元气满满！','糖果类零食要放在干燥处，避免粘连','定期检查过期日期，食品安全最重要','不同口味的零食搭配着吃，更有乐趣','分享零食给朋友，快乐会加倍哦','膨化食品开封后最好3天内吃完','冷冻水果干是夏天的绝佳零食','小熊的私藏：巧克力配咖啡，绝配！','用透明盒子分类存放零食，一目了然','每周盘点一次库存，心里有数不慌张','采购前列好清单，省钱又高效！' ]
const dailyTip=ref(tips[Math.floor(Math.random()*tips.length)])

// 兔子播报
const bunnyMsg=ref('')

onMounted(async()=>{await loadOverview();await loadTrend();await loadPie();await loadRecents();notifStore.generate().catch(()=>{});notifStore.fetchUnread().catch(()=>{})})

async function loadOverview(){try{const res=await getStatisticsOverview();const d=res.data;statCards.value[0].value=d.totalSnackCount;statCards.value[1].value=d.totalQuantity;statCards.value[2].value=d.totalValue!=null?'¥'+d.totalValue:'¥0';statCards.value[3].value=d.lowStockCount;statCards.value[4].value=d.soonExpiredCount;statCards.value[5].value=d.todayInQty||0;bunnyMsg.value=d.lowStockCount>3?`有 ${d.lowStockCount} 种零食库存偏低，可以去采购清单看看~`:d.totalSnackCount>20?'零食种类丰富，管理得真棒！✨':'零食种类不多，可以再多添一些哦~'}catch{}}

async function loadTrend(){trendLoading.value=true;try{const res=await getStockTrend(trendPeriod.value);await nextTick();if(!trendRef.value)return;const c=echarts.init(trendRef.value);const pink='#FF7EB3',purple='#9B59B6';c.setOption({tooltip:{trigger:'axis',backgroundColor:'rgba(255,255,255,0.92)',borderColor:'#FFB6D3',textStyle:{color:'#3D2C3A'}},legend:{data:['入库','出库'],textStyle:{color:'#9B8EA0'}},grid:{top:20,right:20,bottom:20,left:40},xAxis:{type:'category',data:res.data.dailyData.map((d:any)=>d.date),axisLine:{lineStyle:{color:'#E8D5F5'}},axisLabel:{color:'#9B8EA0'}},yAxis:{type:'value',axisLine:{show:false},axisLabel:{color:'#9B8EA0'},splitLine:{lineStyle:{color:'#F5F0F8'}}},series:[{name:'入库',type:'bar',data:res.data.dailyData.map((d:any)=>d.inQty),itemStyle:{borderRadius:[6,6,0,0],color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:pink},{offset:1,color:'#FFB6D3'}])},barWidth:20},{name:'出库',type:'bar',data:res.data.dailyData.map((d:any)=>d.outQty),itemStyle:{borderRadius:[6,6,0,0],color:new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:'#C39BD3'},{offset:1,color:'#E8D5F5'}])},barWidth:20}]})}finally{trendLoading.value=false}}

async function loadPie(){pieLoading.value=true;try{const res=await getCategoryDistribution();await nextTick();if(!pieRef.value)return;const c=echarts.init(pieRef.value);const colors=['#FF7EB3','#FFD93D','#6BCB77','#4D96FF','#C39BD3','#FF9E9E','#88E088','#FFB6D3'];c.setOption({tooltip:{trigger:'item',backgroundColor:'rgba(255,255,255,0.92)',borderColor:'#FFB6D3',textStyle:{color:'#3D2C3A'}},series:[{type:'pie',radius:['55%','78%'],center:['50%','50%'],itemStyle:{borderRadius:6,borderColor:'#fff',borderWidth:3},label:{color:'#9B8EA0'},data:res.data.map((d:any,i:number)=>({name:d.categoryName,value:d.snackCount,itemStyle:{color:colors[i%colors.length]}}))}]})}finally{pieLoading.value=false}}

async function loadRecents(){recentsLoading.value=true;try{const res=await getStockRecordStats();recents.value=(res.data.recentRecords||[]).slice(0,6)}finally{recentsLoading.value=false}}
</script>

<style scoped>
.dashboard { max-width: 1400px; }

/* 欢迎语 */
.welcome { display:flex;align-items:center;gap:12px;margin-bottom:20px;padding:16px 20px;background:var(--bg-card);border-radius:var(--radius-lg);box-shadow:var(--shadow-card); }
.welcome-bear { font-size:32px;animation:float 3s ease-in-out infinite; }
.welcome-text { font-size:17px;font-weight:600;color:var(--text-primary);flex:1; }
.welcome-date { font-size:13px;color:var(--text-secondary); }

/* 统计卡片 */
.stat-row { margin-bottom: 0; }
.stat-card { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px 16px;text-align:center;box-shadow:var(--shadow-card);position:relative;overflow:hidden;cursor:pointer;transition:all 0.3s ease;border-top:4px solid var(--bar-color,#FF7EB3); }
.stat-card:hover { transform:translateY(-6px);box-shadow:var(--shadow-hover); }
.stat-icon { font-size:28px;margin-bottom:8px; }
.stat-value { font-size:24px;font-weight:bold;margin-bottom:4px; }
.stat-label { font-size:13px;color:var(--text-secondary); }

/* 图表卡片 */
.chart-card { background:var(--bg-card);border-radius:var(--radius-lg);padding:20px;box-shadow:var(--shadow-card);margin-bottom:0; }
.chart-header { display:flex;align-items:center;justify-content:space-between;margin-bottom:12px;font-size:15px;font-weight:600;color:var(--text-primary); }

/* 动态 */
.activity-list { max-height:340px;overflow-y:auto; }
.activity-item { display:flex;align-items:center;gap:10px;padding:10px 0;border-bottom:1px dashed var(--border-light);font-size:14px; }
.activity-item:last-child { border-bottom:none; }
.activity-icon { width:28px;height:28px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:14px;flex-shrink:0; }
.activity-item.in .activity-icon { background:rgba(107,203,119,0.15); }
.activity-item.out .activity-icon { background:rgba(255,107,107,0.12); }
.activity-text { flex:1;color:var(--text-primary); }
.activity-time { font-size:12px;color:var(--text-secondary);white-space:nowrap; }

/* 小贴士 */
.tip-card { background:var(--gradient-primary);color:#fff;border-radius:var(--radius-lg);padding:24px 20px 20px;position:relative;overflow:hidden;height:100%;display:flex;flex-direction:column;justify-content:center; }
.tip-card::before { content:'💡';position:absolute;top:-30px;right:20px;font-size:120px;opacity:0.12; }
.tip-icon { font-size:32px;margin-bottom:12px;position:relative; }
.tip-text { font-size:16px;line-height:1.8;position:relative; }
.tip-author { margin-top:12px;font-size:13px;opacity:0.85;text-align:right;position:relative; }

/* 兔子播报 */
.bunny-bar { margin-top:16px;padding:14px 20px;background:var(--bg-card);border-radius:var(--radius-lg);box-shadow:var(--shadow-card);display:flex;align-items:center;gap:10px;font-size:14px;color:var(--text-secondary); }
.bunny-icon { font-size:24px;animation:float 3s ease-in-out infinite; }

/* 空状态 */
.empty-hint { text-align:center;padding:40px 0;color:var(--text-secondary);font-size:15px; }
</style>
