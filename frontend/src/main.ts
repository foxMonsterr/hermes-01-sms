import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { setupGuards } from './router/guards'
import './styles/theme.css'
import './styles/anime.css'
import './styles/theme-mint.css'
import './styles/theme-blue.css'
import './styles/theme-orange.css'

// 初始化主题
const savedTheme = localStorage.getItem('theme')
if (savedTheme && savedTheme !== 'pink') {
  document.documentElement.setAttribute('data-theme', savedTheme)
}

const app = createApp(App)

// Element Plus 中文
app.use(ElementPlus, { locale: zhCn })

// Element Plus 图标全局注册
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Pinia
app.use(createPinia())

// Router
app.use(router)
setupGuards(router)

app.mount('#app')
