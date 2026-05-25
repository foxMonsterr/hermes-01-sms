<template>
  <div class="not-found anim-scale-in">
    <div class="nf-icon">🐱</div>
    <h1>页面走丢了喵...</h1>
    <p>你要找的零食可能被小熊吃掉啦</p>
    <el-button type="primary" size="large" @click="$router.push('/dashboard')">🏠 回到首页</el-button>
  </div>
</template>

<style scoped>
.not-found { text-align:center;padding:80px 20px; }
.nf-icon { font-size:96px;margin-bottom:20px;animation:float 3s ease-in-out infinite; }
h1 { font-size:24px;color:var(--text-primary);margin-bottom:8px; }
p { font-size:15px;color:var(--text-secondary);margin-bottom:28px; }
</style>
