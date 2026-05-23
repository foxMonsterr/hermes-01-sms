# 前后端接口契约 (v3.0)

> 本文档定义前后端接口的统一约定。所有 API 开发和调用必须遵守。  
> **v3.0 变更**: 增加用户隔离说明、路径含义澄清、补充 Snack/Statistics 接口。

---

## 1. Base URL 与路径说明

**本文档接口路径以「前端 request 调用路径」为准，不包含 Vite 代理前缀 `/api`。**

| 视角 | 路径示例 |
|------|---------|
| 前端调用 | `request.post('/auth/login')` |
| 后端 Controller | `@PostMapping("/api/auth/login")` |
| 实际 HTTP | `http://localhost:5173/api/auth/login` → 代理 → `http://localhost:8080/api/auth/login` |

**规则**:
- Vite 代理: `/api` → `http://localhost:8080`
- 前端 API 文件不写 `/api` 前缀（baseURL 已配置）
- 后端 Controller `@RequestMapping` 必须写 `/api/...`

---

## 2. 统一响应格式

### Result<T>

```typescript
interface Result<T = unknown> {
  code: number    // 业务状态码
  msg: string     // 提示信息（不是 message）
  data: T         // 响应数据，错误时为 null
}
```

**成功**:
```json
{ "code": 200, "msg": "操作成功", "data": {} }
```

**失败**:
```json
{ "code": 400, "msg": "错误原因", "data": null }
```

---

## 3. 分页结构

```typescript
interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
}
```

分页接口返回 `Result<PageData<T>>`。

---

## 4. 认证

- Header: `Authorization: Bearer <token>`
- 前端不传 userId
- 后端从 JWT 解析 currentUserId
- 访问其他用户数据 → 404

**401 响应**:
```json
{ "code": 401, "msg": "未登录或 token 已过期", "data": null }
```

---

## 5. 错误码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 / 业务校验失败 |
| 401 | 未登录或 token 过期 |
| 404 | 资源不存在或不属于当前用户 |
| 409 | 资源冲突（如分类名重复） |
| 500 | 服务器错误 |

---

## 6. 用户隔离说明

- 所有需要认证的接口都会根据 JWT 中的 userId 自动限定数据范围
- **前端不需要传 userId**
- **后端不信任前端传入的 userId**
- DTO 中不包含 userId 字段
- userId 只从 token 解析得到

---

## 7. 接口清单

### 7.1 认证 `POST /auth/*` (无需认证)

| 前端调用 | 后端路径 | 说明 |
|---------|---------|------|
| `POST /auth/register` | `POST /api/auth/register` | 注册，自动创建 8 个默认分类 |
| `POST /auth/login` | `POST /api/auth/login` | 登录，失败统一 "用户名或密码错误" |
| `GET /auth/profile` | `GET /api/auth/profile` | 获取当前用户（需认证） |

### 7.2 分类 `GET/POST/PUT/DELETE /categories` (需认证)

| 前端调用 | 后端路径 | 说明 |
|---------|---------|------|
| `GET /categories` | `GET /api/categories` | 当前用户分类列表 |
| `GET /categories/{id}` | `GET /api/categories/{id}` | 详情，非本用户 → 404 |
| `POST /categories` | `POST /api/categories` | 新增，同名 → 409 |
| `PUT /categories/{id}` | `PUT /api/categories/{id}` | 更新 |
| `DELETE /categories/{id}` | `DELETE /api/categories/{id}` | 删除 |

### 7.3 零食 (需认证)

| 前端调用 | 后端路径 | 说明 |
|---------|---------|------|
| `GET /snacks` | `GET /api/snacks` | 分页列表 |
| `GET /snacks/{id}` | `GET /api/snacks/{id}` | 详情 |
| `POST /snacks` | `POST /api/snacks` | 新增 |
| `PUT /snacks/{id}` | `PUT /api/snacks/{id}` | 更新 |
| `PATCH /snacks/{id}/quantity` | `PATCH /api/snacks/{id}/quantity` | 增减库存 `{delta}` |
| `DELETE /snacks/{id}` | `DELETE /api/snacks/{id}` | 删除 |
| `DELETE /snacks/batch` | `DELETE /api/snacks/batch` | 批量删除 `{ids:[...]}` |

### 7.4 统计 (需认证)

| 前端调用 | 后端路径 | 说明 |
|---------|---------|------|
| `GET /statistics/overview` | `GET /api/statistics/overview` | 总览统计 |
| `GET /statistics/category-distribution` | `GET /api/statistics/category-distribution` | 分类分布 |

---

## 8. 关键接口示例

### 登录

```json
// 请求 POST /auth/login
{ "username": "admin", "password": "123456" }

// 成功
{ "code": 200, "msg": "登录成功", "data": { "id": 1, "token": "...", "username": "admin", "nickname": "管理员" } }

// 失败（不区分用户不存在/密码错误）
{ "code": 400, "msg": "用户名或密码错误", "data": null }
```

### 新增零食

```json
// 请求 POST /snacks（不传 userId）
{ "name": "乐事薯片", "categoryId": 1, "quantity": 3, "unit": "包", "expiryDate": "2026-12-01" }

// 成功
{ "code": 200, "msg": "操作成功", "data": { "id": 1, "name": "乐事薯片", ... } }
```

### 库存增减

```json
// 请求 PATCH /snacks/{id}/quantity
{ "delta": -1 }

// 库存不足
{ "code": 400, "msg": "库存不足，当前库存为 2", "data": null }
```

### 批量删除

```json
// 请求 DELETE /snacks/batch
{ "ids": [1, 2, 3] }

// ids 中包含其他用户的数据
{ "code": 404, "msg": "部分零食不存在或无权操作", "data": null }
```

---

> **本文档是前后端开发的唯一接口契约。任何变更必须先更新本文档。**
