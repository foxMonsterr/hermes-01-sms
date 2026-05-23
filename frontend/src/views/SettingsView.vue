<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="个人资料" name="profile">
        <el-card>
          <el-form label-width="80px">
            <el-form-item label="用户名"><el-input :model-value="userStore.username" disabled /></el-form-item>
            <el-form-item label="昵称"><el-input v-model="profileForm.nickname" maxlength="50" /></el-form-item>
            <el-form-item label="头像">
              <el-upload :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="handleAvatarUpload" accept="image/*">
                <el-avatar :size="64" :src="userStore.avatarUrl"><el-icon :size="32"><UserFilled /></el-icon></el-avatar>
                <el-button size="small" style="margin-left:12px">上传头像</el-button>
              </el-upload>
              <div style="font-size:12px;color:#909399;margin-top:4px">限 jpg/png/webp，≤1MB</div>
            </el-form-item>
            <el-form-item><el-button type="primary" :loading="profileLoading" @click="handleProfile">保存</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="修改密码" name="password">
        <el-card>
          <el-form ref="pwdRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
            <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
            <el-form-item><el-button type="primary" :loading="pwdLoading" @click="handlePassword">修改密码</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="系统偏好" name="preferences">
        <el-card>
          <el-form :model="configForm" label-width="140px">
            <el-form-item label="低库存阈值"><el-input-number v-model="configForm.lowStockThreshold" :min="0" :max="99" /></el-form-item>
            <el-form-item label="过期提醒天数"><el-input-number v-model="configForm.expiryAlertDays" :min="1" :max="365" /></el-form-item>
            <el-form-item label="默认分页大小"><el-select v-model="configForm.defaultPageSize"><el-option v-for="v in [5,10,20,50]" :key="v" :value="v" :label="String(v)" /></el-select></el-form-item>
            <el-form-item label="自动生成提醒"><el-switch v-model="configForm.autoGenerateNotification" /></el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="configLoading" @click="handleConfig">保存配置</el-button>
              <el-button @click="handleResetConfig">恢复默认</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="数据维护" name="maintenance">
        <el-card>
          <p style="color:#909399;margin-bottom:12px">下载数据库备份文件（仅本地开发/演示环境）</p>
          <el-button type="primary" @click="handleBackup">下载数据库备份</el-button>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updatePassword, updateProfile, uploadAvatar } from '@/api/auth'
import { getSystemConfig, updateSystemConfig, resetSystemConfig } from '@/api/systemConfig'
import { backupDatabase } from '@/api/system'
import type { SystemConfig } from '@/types/systemConfig'

const userStore = useUserStore()
const activeTab = ref('profile')

// profile
const profileLoading = ref(false)
const profileForm = reactive({ nickname: userStore.nickname })
async function beforeAvatarUpload(file: File) {
  const ok = ['image/jpeg','image/png','image/webp'].includes(file.type)
  if (!ok) { ElMessage.error('仅支持 jpg/png/webp'); return false }
  if (file.size > 1024*1024) { ElMessage.error('头像不能超过 1MB'); return false }
  return true
}
async function handleAvatarUpload({ file }: any) {
  const fd = new FormData(); fd.append('file', file)
  const res = await uploadAvatar(fd)
  userStore.updateAvatar(res.data.avatarUrl)
  ElMessage.success('头像上传成功')
}
async function handleProfile() { profileLoading.value = true
  try { await updateProfile({ nickname: profileForm.nickname }); userStore.nickname = profileForm.nickname; ElMessage.success('修改成功') } finally { profileLoading.value = false } }

// password
const pwdRef = ref<FormInstance>(); const pwdLoading = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '密码最少6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: (_r: any, v: string, cb: any) => v === pwdForm.newPassword ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }]
}
async function handlePassword() { const ok = await pwdRef.value?.validate().catch(() => false); if (!ok) return; pwdLoading.value = true
  try { await updatePassword({ ...pwdForm }); ElMessage.success('密码修改成功'); pwdForm.oldPassword = ''; pwdForm.newPassword = ''; pwdForm.confirmPassword = '' } finally { pwdLoading.value = false } }

// config
const configLoading = ref(false)
const configForm = reactive<SystemConfig>({ lowStockThreshold: 2, expiryAlertDays: 30, defaultPageSize: 10, autoGenerateNotification: true })
onMounted(async () => { try { const res = await getSystemConfig(); Object.assign(configForm, res.data) } catch {} })
async function handleConfig() { configLoading.value = true; try { await updateSystemConfig({ ...configForm }); ElMessage.success('配置已保存') } finally { configLoading.value = false } }
async function handleResetConfig() { configLoading.value = true; try { const res = await resetSystemConfig(); Object.assign(configForm, res.data); ElMessage.success('已恢复默认') } finally { configLoading.value = false } }

// backup
async function handleBackup() { try { await backupDatabase(); ElMessage.success('备份下载中') } catch { ElMessage.error('备份失败') } }
</script>

<style scoped>
.settings-page { max-width: 640px; }
.el-card { margin-bottom: 0; }
</style>
