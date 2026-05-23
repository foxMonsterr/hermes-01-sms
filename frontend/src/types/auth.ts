export interface LoginDTO { username: string; password: string }
export interface RegisterDTO { username: string; password: string; nickname?: string }
export interface LoginVO { id: number; token: string; username: string; nickname: string; avatarUrl: string }
export interface UserInfo { id: number; username: string; nickname: string; avatarUrl: string }
