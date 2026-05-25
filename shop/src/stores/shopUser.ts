import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken } from '@/utils/token'
import { login as loginApi, getProfile } from '@/api/auth'

export const useShopUserStore = defineStore('shopUser',()=>{
  const id=ref(0);const username=ref('');const nickname=ref('')
  const isLoggedIn=computed(()=>!!getToken())
  async function login(u:string,p:string){const r=await loginApi(u,p);setToken(r.data.token);id.value=r.data.id;username.value=r.data.username;nickname.value=r.data.nickname}
  function logout(){removeToken();id.value=0;username.value='';nickname.value=''}
  async function fetchProfile(){const r=await getProfile();id.value=r.data.id;username.value=r.data.username;nickname.value=r.data.nickname}
  return {id,username,nickname,isLoggedIn,login,logout,fetchProfile}
})
