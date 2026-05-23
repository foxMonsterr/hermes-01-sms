# 业务规则文档 (v3.0)

> 本文档沉淀所有业务规则。开发和测试必须遵守。  
> **v3.0 变更**: 新增用户数据隔离设计，所有数据按 user_id 隔离。

---

## 1. 项目定位

MVP 阶段定位：**面向个人/家庭场景的零食库存管理系统，MVP 阶段支持多用户登录与用户级数据隔离。每个用户只能访问自己的分类和零食数据。**

- 有用户注册和登录（注册后自动初始化 8 个默认分类）。
- 有分类管理和零食管理。
- 所有数据通过 user_id 隔离。
- 暂不做复杂角色权限、管理员后台、家庭共享、协作权限。

---

## 2. 数据库规则

### 2.1 表名

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `snack_category` | 分类表（含 user_id） |
| `snack` | 零食表（含 user_id） |

### 2.2 用户隔离机制

所有业务数据（分类、零食）通过 `user_id` 字段与用户关联：

- `snack_category.user_id` → 分类所属用户
- `snack.user_id` → 零食所属用户
- currentUserId 从 JWT 中解析，由 JwtInterceptor 放入 `request.setAttribute("currentUserId", userId)`
- 前端**不传 userId**，后端**不信任前端传入 userId**
- 所有查询、新增、更新、删除都必须基于 currentUserId 过滤
- 访问其他用户数据统一返回 **404**（不暴露数据存在性）

### 2.3 逻辑删除规则

- 所有表启用逻辑删除（is_deleted: 0=正常, 1=删除）
- MyBatis-Plus 使用 `@TableLogic` 注解
- is_deleted 建议使用 `Integer` 类型，`NOT NULL DEFAULT 0`

### 2.4 唯一性规则

使用 SQLite partial unique index，同一用户下唯一：

```sql
-- 用户名唯一（全局，仅针对未删除数据）
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_user_username_active
    ON sys_user(username) WHERE is_deleted = 0;

-- 分类名唯一（同一用户下，仅针对未删除数据）
CREATE UNIQUE INDEX IF NOT EXISTS uk_snack_category_user_name_active
    ON snack_category(user_id, name) WHERE is_deleted = 0;
```

**规则**: 不同用户可以有同名分类。同一用户下不能有同名分类。

### 2.5 SQLite 外键说明

MVP 阶段不强制使用数据库外键。user_id、category_id 的有效性由 **Service 层校验**。文档中所有表设计不包含 FOREIGN KEY 约束。

---

## 3. 用户隔离规则

### 3.1 通用规则

1. 每个用户登录后只能看到、管理自己的零食数据。
2. 每个用户拥有自己的分类数据。
3. 不同用户之间分类名、零食数据互不影响。
4. 登录用户只能操作自己名下的数据。
5. 如果访问不存在或**不属于当前用户**的数据，统一返回 **404**，避免暴露其他用户数据存在性。

### 3.2 分类隔离规则

- 分类名称在**当前用户未删除数据中唯一**。
- 新用户注册后，系统应为该用户初始化 8 个默认分类。
- 默认分类属于具体用户，不再是全局共享分类。
- 分类列表只返回当前用户自己的分类。
- 分类删除时，只检查当前用户该分类下是否存在未删除零食。

### 3.3 零食隔离规则

- 零食必须属于当前用户。
- 新增零食时，category_id 必须是当前用户自己的有效分类。
- 更新零食时，如果修改 category_id，也必须校验该分类属于当前用户。
- 删除、查询详情、库存增减都必须校验 snack.user_id == currentUserId。
- 访问其他用户的零食返回 404。

---

## 4. 分类规则

1. 分类名称不能为空，最多 50 字符。
2. 分类名称在**同一用户未删除数据中**唯一（见 2.4）。
3. 分类按 sort_order 升序排列。
4. 系统预设 8 个默认分类（注册时按用户初始化）：
   - 膨化食品 🍿
   - 糖果巧克力 🍬
   - 坚果炒货 🥜
   - 饼干糕点 🍪
   - 饮料 🥤
   - 肉干肉脯 🍖
   - 果脯蜜饯 🍑
   - 其他 📦
5. 默认分类属于具体用户，不再是全局共享分类。
6. 分类下存在当前用户未删除零食时，不允许删除分类。
   - 返回: `{ "code": 400, "msg": "该分类下有 N 个零食，请先删除或转移零食" }`
7. **MVP 阶段所有分类接口都需要登录。**

---

## 5. 零食规则

1. 零食名称不能为空，最多 100 字符。
2. 数量不能小于 0（`quantity >= 0`）。
3. 分类必须存在且属于当前用户（category_id 对应当前用户 snack_category 表中的有效 id）。
4. 零食名称不要求唯一（不同用户可有同名零食）。
5. 删除零食使用逻辑删除。
6. 删除前前端必须二次确认。
7. 快速减少库存时，减少后不能小于 0。
   - 返回: `{ "code": 400, "msg": "库存不足，当前库存为 X" }`
8. 批量删除时，只删除当前用户自己的零食。传入的 ids 中存在不属于当前用户的数据 → 返回 404，提示"部分零食不存在或无权操作"。
9. 所有零食查询、新增、更新、删除、库存修改都必须基于 currentUserId。

---

## 6. 库存规则

### 6.1 MVP 阶段

- 库存数量直接存放在 snack.quantity。
- 快速增减库存通过 `PATCH /snacks/{id}/quantity`（delta 正数增加，负数减少）。
- delta 不能为 0。
- 不单独设计库存表。

### 6.2 后续扩展

可新增 stock_record 表记录库存变动流水。

---

## 7. 过期规则

| 条件 | 状态 | 前端颜色 |
|------|------|----------|
| expiryDate == null | unknown | 灰色 |
| expiryDate < 当前日期 | expired | 红色 |
| 0 ≤ daysUntilExpiry ≤ 30 | soon | 橙色 |
| daysUntilExpiry > 30 | fresh | 默认色 |

**重要**: 所有过期查询、筛选、统计都必须只针对当前用户自己的 snack 数据。

---

## 8. 认证规则

### 8.1 密码规则

1. 密码不能明文存储，必须使用 BCrypt 加密。
2. 登录时使用 BCryptPasswordEncoder.matches() 校验。
3. 响应中永远不能返回 password 字段。
4. 注册时密码最少 6 位。

### 8.2 Token 规则

1. 登录成功后返回 JWT token。
2. JWT Payload: `{ sub: userId, username: xxx, iat, exp }`。
3. 默认有效期: 7 天（604800000 毫秒）。
4. JWT 密钥必须足够长（≥256 bit）：
   ```yaml
   jwt:
     secret: ${JWT_SECRET:snack-manager-secret-key-2026-default-very-long-secret}
   ```
5. JwtUtil 使用 `Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8))`。

### 8.3 注册规则

1. MVP 阶段开放注册，方便测试。
2. 用户名在**全局未删除数据中**唯一。
3. **注册成功后，系统自动为该用户创建 8 个默认分类**。
4. 初始化分类过程放在事务中（`@Transactional`），失败则整体回滚。
5. 生产环境可关闭注册。

### 8.4 鉴权范围

以下接口**不需要登录**:
- `POST /auth/login`
- `POST /auth/register`
- Knife4j 文档相关路径

**其他所有 `/api/**` 接口都需要登录。**

### 8.5 JWT 拦截器

- 放行路径见 API-CONTRACT.md。
- 解析 token 后设置:
  - `request.setAttribute("currentUserId", userId)`
  - `request.setAttribute("currentUsername", username)`
- 401 必须返回统一 JSON: `{ "code": 401, "msg": "未登录或 token 已过期", "data": null }`
- Controller 从 request attribute 获取 currentUserId，再传给 Service。

### 8.6 Controller 获取当前用户

可选封装:
```java
protected Long getCurrentUserId(HttpServletRequest request) {
    return (Long) request.getAttribute("currentUserId");
}
```

### 8.7 currentUserId 传递规则

- 前端**不传 userId**。
- 后端**不信任前端传入 userId**。
- DTO 中**不包含 userId 字段**。
- userId 只从 JWT token 解析得到。
- Service 层所有涉及数据操作的方法都必须接收 currentUserId 参数。

---

## 9. 接口鉴权放行清单

JwtInterceptor 排除路径:

```
/api/auth/login
/api/auth/register
/doc.html
/doc.html/**
/webjars/**
/v3/api-docs/**
/swagger-ui/**
/swagger-ui.html
/swagger-resources/**
/favicon.ico
```

---

## 10. 开发阶段说明

### schema.sql 自动执行

- 开发阶段使用 `spring.sql.init.mode=always`。
- schema.sql **只负责建表和建索引**，不再插入全局默认分类。
- 默认分类改为注册后按用户初始化。
- 部署或答辩演示阶段可改为 `never`，避免误执行初始化脚本。

### 注册流程

```
用户提交注册
  → 校验用户名是否已存在
  → BCrypt 加密密码
  → 事务开始
    → 创建 sys_user 记录
    → 为该用户插入 8 条 snack_category 默认分类
  → 事务提交
  → 返回注册成功
```

---

> **业务规则变更时，必须先更新本文档。**
