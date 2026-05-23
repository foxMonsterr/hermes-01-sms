import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/token'
import { login as loginApi, getProfile } from '@/api/auth'
import type { LoginDTO } from '@/types/auth'

export const useUserStore = defineStore('user', () => {
  const id = ref<number>(0)
  const username = ref('')
  const nickname = ref('')
  const avatarUrl = ref('')
  const isLoggedIn = computed(() => !!getToken())

  async function login(dto: LoginDTO) {
    const res = await loginApi(dto)
    setToken(res.data.token)
    id.value = res.data.id
    username.value = res.data.username
    nickname.value = res.data.nickname
    avatarUrl.value = res.data.avatarUrl || ''
  }

  function logout() {
    removeToken()
    id.value = 0; username.value = ''; nickname.value = ''; avatarUrl.value = ''
  }

  async function fetchProfile() {
    const res = await getProfile()
    id.value = res.data.id
    username.value = res.data.username
    nickname.value = res.data.nickname
    avatarUrl.value = res.data.avatarUrl || ''
  }

  function updateAvatar(url: string) { avatarUrl.value = url }

  return { id, username, nickname, avatarUrl, isLoggedIn, login, logout, fetchProfile, updateAvatar }
})
