import request from '@/utils/request'
import type { LoginDTO, RegisterDTO, LoginVO, UserInfo } from '@/types/auth'

export function login(data: LoginDTO) { return request.post<LoginVO>('/auth/login', data) }
export function register(data: RegisterDTO) { return request.post('/auth/register', data) }
export function getProfile() { return request.get<UserInfo>('/auth/profile') }
export function updatePassword(data: { oldPassword: string; newPassword: string; confirmPassword: string }) { return request.put('/auth/password', data) }
export function updateProfile(data: { nickname: string }) { return request.put<UserInfo>('/auth/profile', data) }
export function uploadAvatar(formData: FormData) { return request.upload<UserInfo>('/auth/avatar', formData) }
