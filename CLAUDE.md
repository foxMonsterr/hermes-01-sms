# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指引。

## 项目概述

零食管理系统 — 基于 Spring Boot 3 + Vue 3 + SQLite 的全栈应用，包含管理端和客户端（小卖铺）。本科毕业设计项目。

## 模块结构

```
backend/     → Spring Boot 3 (:8080), Maven, Java 17
frontend/    → 管理端 SPA (:5173), Vue 3 + TypeScript + Element Plus
shop/        → 客户端 SPA (:5174), Vue 3 + TypeScript + Element Plus
docs/        → API 契约、业务规则、故障排查、最终报告
```

## 开发命令

**后端**：用 IntelliJ IDEA 打开 `backend/` 目录，等待 Maven 同步完成后运行 `SnackApplication.java`。或通过命令行：
```bash
cd backend && mvn spring-boot:run
```

**管理端前端**：
```bash
cd frontend && npm install && npm run dev    # → localhost:5173
```

**客户端前端**：
```bash
cd shop && npm install && npm run dev        # → localhost:5174
```

项目暂无测试（`src/test` 目录为空）。

API 文档地址：`http://localhost:8080/doc.html`（Knife4j/OpenAPI）。

## 架构

### 后端分层

```
controller/  → 处理 HTTP 请求，从 request attribute 中提取 currentUserId，委托给 Service
  ├── admin/       → 管理端专属接口（店铺订单、评价、统计）
  └── shop/        → 客户端接口（商品浏览、购物车、下单、评价）
service/     → 业务逻辑，从 Controller 接收 currentUserId
mapper/      → MyBatis-Plus 数据访问（继承 BaseMapper<Entity>）
entity/      → 数据库实体类，使用 @TableLogic 和 @TableField(fill = ...)
dto/         → 请求体，使用 Jakarta Validation 注解校验
vo/          → 响应对象（永远不包含 password 字段）
common/      → Result<T>、PageData<T>、BusinessException、ErrorCode、GlobalExceptionHandler
config/      → MyBatisPlusConfig（分页插件 + 自动填充时间戳）、WebMvcConfig（CORS + 拦截器）、Knife4j、SecurityConfig
security/    → JWT 工具类和拦截器（管理端与客户端分开）
```

### 双 JWT 认证体系

系统同时运行两套独立的认证体系：

| | 管理端 | 客户端（小卖铺） |
|---|---|---|
| 拦截器 | `JwtInterceptor` | `ShopJwtInterceptor` |
| Token 工具 | `JwtUtil` | `ShopJwtUtil` |
| 用户实体 | `User` (sys_user) | `ShopUser` (shop_user) |
| Token 存储键 | `snack_admin_token` | `snack_shop_token` |
| Request 属性 | `currentUserId` | `shopUserId` |

拦截器路径映射在 `WebMvcConfig` 中配置：管理端 JWT 拦截所有 `/api/**`（排除放行路径）；客户端 JWT 仅拦截需要认证的路径如 `/api/shop/auth/profile`、`/api/shop/cart/**` 等。

### 数据隔离

所有用户数据严格隔离。`currentUserId` 由拦截器从 JWT 中解析并写入 request attribute。前端**绝不**传递 `userId`。后端**绝不**信任请求体中的 `userId`——始终从 request attribute 中读取。访问其他用户的数据统一返回 **404**（而非 403）。

### API 约定

- 统一响应：`Result<T>`，包含 `code`（Integer）、`msg`（String，**不是** `message`）、`data`（T，可为 null）
- 成功码固定为 `200`
- 错误码：`400`（参数校验/业务校验失败）、`401`（未登录）、`404`（资源不存在或不属于当前用户）、`409`（资源冲突）、`500`（服务器错误）
- 分页接口返回 `Result<PageData<T>>`，`PageData` 包含 `records`、`total`、`page`、`size`
- 业务异常统一抛出 `BusinessException`，禁止直接抛 `RuntimeException`
- 密码使用 BCrypt 加密，响应中永远不返回密码

### 前端代理模式

Vite 开发服务器将 `/api` 代理到 `http://localhost:8080`。前端 API 文件中**不要**加 `/api` 前缀——`baseURL` 已配置为 `/api`。后端 Controller 的 `@RequestMapping` **必须**写 `/api/...`。

正确：`request.post('/auth/login')`
错误：`request.post('/api/auth/login')`

### 数据库

使用 SQLite，连接字符串为 `jdbc:sqlite:<绝对路径>/backend/data/snack.db`。表结构在启动时自动创建（`spring.sql.init.mode=always`，执行 `schema.sql`）。全局开启逻辑删除（`is_deleted` 字段，0=正常, 1=删除），唯一性通过部分唯一索引实现（`WHERE is_deleted = 0`）。

核心表：`sys_user`、`snack_category`、`snack`、`stock_record`、`shopping_list`、`notification`、`disposal_record`、`inventory_check_record`、`supplier`、`operation_log`、`system_config`、`shop_user`、`shop_cart`、`shop_order`、`shop_order_item`、`shop_review`。

### 前端代码结构（管理端和客户端通用）

```
src/api/       → API 请求函数（组件中禁止直接使用 axios）
src/types/     → TypeScript 类型定义
src/stores/    → Pinia 状态管理
src/utils/     → token.ts、request.ts（axios 实例）
src/router/    → Vue Router 路由配置 + 导航守卫
src/views/     → 页面组件
src/components/ → 公共组件
```

全部使用 TypeScript，禁止使用 `any`（除非有充分理由）。表格页面必须包含 loading、empty、error 三种状态。删除操作必须二次确认。表单提交按钮必须有 loading 状态。
