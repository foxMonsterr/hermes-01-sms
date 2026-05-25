const KEY = 'snack_shop_token'
export function getToken(){return localStorage.getItem(KEY)}
export function setToken(t:string){localStorage.setItem(KEY,t)}
export function removeToken(){localStorage.removeItem(KEY)}
