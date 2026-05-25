<template>
  <div class="bear-assistant">
    <transition name="bubble">
      <div v-if="showBubble" class="bear-bubble">
        <div class="bubble-icon">🐻</div>
        <div class="bubble-text">{{ currentTip }}</div>
        <div class="bubble-actions">
          <el-button v-if="tipData?.link" size="small" type="primary" @click="goTo(tipData.link)">去看看</el-button>
          <el-button size="small" @click="showBubble=false">知道了</el-button>
        </div>
      </div>
    </transition>
    <div class="bear-avatar" @click="toggleBubble" title="小熊店长">
      <span>🐻</span>
      <span v-if="hasAlert" class="bear-dot"></span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const notifStore = useNotificationStore()
const showBubble = ref(false)
const tipIndex = ref(0)

const tips = [
  { text:'发现零食即将过期，快去看看吧！', link:'/snacks' },
  { text:'库存偏低了，要不要采购一些？', link:'/shopping-list' },
  { text:'来看看最近的统计报告吧~', link:'/statistics' },
  { text:'好久没盘点库存了呢！', link:'/stock-records' },
  { text:'有新的系统消息哦~', link:'/notifications' },
]

const hasAlert = computed(() => notifStore.unread.count > 0)
const tipData = computed(() => tips[tipIndex.value % tips.length])
const currentTip = computed(() => tipData.value.text)

function toggleBubble() {
  showBubble.value = !showBubble.value
  if (showBubble.value) tipIndex.value = Math.floor(Math.random() * tips.length)
}

function goTo(path: string) {
  showBubble.value = false
  router.push(path)
}

onMounted(() => {
  // 首次加载 3 秒后自动弹一次
  setTimeout(() => {
    if (hasAlert.value) { showBubble.value = true; tipIndex.value = 0 }
  }, 3000)
})
</script>

<style scoped>
.bear-assistant { position:fixed;bottom:24px;right:24px;z-index:9999;display:flex;flex-direction:column;align-items:flex-end;gap:12px; }

.bear-avatar { width:56px;height:56px;border-radius:50%;background:var(--gradient-primary);display:flex;align-items:center;justify-content:center;font-size:28px;cursor:pointer;box-shadow:var(--shadow-hover);animation:float 3s ease-in-out infinite;position:relative;transition:transform 0.2s; }
.bear-avatar:hover { transform:scale(1.1); }
.bear-avatar:active { transform:scale(0.95); }

.bear-dot { position:absolute;top:2px;right:2px;width:12px;height:12px;background:var(--danger);border-radius:50%;border:2px solid #fff;animation:pulse 2s infinite; }

.bear-bubble { background:#fff;border-radius:var(--radius-lg);padding:16px 18px;box-shadow:var(--shadow-modal);max-width:280px;position:relative; }
.bear-bubble::after { content:'';position:absolute;bottom:-10px;right:20px;width:20px;height:20px;background:#fff;transform:rotate(45deg);border-radius:2px; }
.bubble-icon { font-size:20px;margin-bottom:6px; }
.bubble-text { font-size:14px;line-height:1.7;color:var(--text-primary);margin-bottom:10px; }
.bubble-actions { display:flex;justify-content:flex-end;gap:8px; }

.bubble-enter-active,.bubble-leave-active { transition:all 0.3s ease; }
.bubble-enter-from,.bubble-leave-to { opacity:0;transform:translateY(12px) scale(0.9); }
</style>
