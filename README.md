# 🍿 零食管理系统

> Spring Boot 3 + Vue 3 + SQLite | 管理端 + 客户端 | 本科毕业设计 v7.1

---

## 项目简介

零食管理系统是一个轻量级线上零食小卖铺，包含**管理端**（商品维护/库存/订单/统计）和**客户端**（浏览/购物车/下单/评价）。管理端维护商品上架，客户端浏览购买，下单自动扣库存生成订单，形成完整供应链闭环。

---

## 技术栈

| 层 | 技术 |
|---|---|
| 后端 | Spring Boot 3 + MyBatis-Plus + SQLite + JWT + Knife4j |
| 管理端前端 | Vue 3 + TypeScript + Element Plus + ECharts + Pinia |
| 客户端前端 | Vue 3 + TypeScript + Element Plus + Pinia |
| 构建 | Vite 5.x |

---

## 环境要求

| 工具 | 版本 |
|---|---|
| JDK | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |
| IDE | IntelliJ IDEA (推荐) |

---

## 快速启动

### 1. 后端

```bash
# IDEA 打开 backend/ 目录
# 等待 Maven 依赖下载完成
# 运行 SnackApplication.java
# 首次启动自动建表 → http://localhost:8080
# API 文档 → http://localhost:8080/doc.html
```

### 2. 管理端

```bash
cd frontend
npm install
npm run dev          # → http://localhost:5173
```

### 3. 客户端

```bash
cd shop
npm install
npm run dev          # → http://localhost:5174
```

### 4. 测试数据

DataGrip 连接 `backend/data/snack.db`，执行 `backend/src/main/resources/db/test-data.sql`

然后执行：
```sql
UPDATE snack SET is_on_shelf=1,shelf_time=datetime('now','localtime') WHERE user_id=1;
```

---

## 测试账号

| 端 | 账号 | 密码 |
|---|---|---|
| 管理端 | admin | 123456 |
| 客户端 | customer1 | 123456 |
| 客户端 | customer2 | 123456 |

---

## 项目结构

```
snack-manager/
├── backend/          # Spring Boot (:8080)
├── frontend/         # 管理端 Vue 3 (:5173)
├── shop/             # 客户端 Vue 3 (:5174)
├── docs/             # 项目文档
│   ├── FINAL-REPORT.md       # 最终项目文档
│   ├── API-CONTRACT.md       # API 接口文档
│   ├── BUSINESS_RULES.md     # 业务规则
│   └── TROUBLESHOOTING.md    # 故障排查
└── README.md
```

---

## 核心配置

```yaml
# backend/src/main/resources/application.yml
server.port: 8080
spring.datasource.url: jdbc:sqlite:C:/Users/Administrator/snack-manager/backend/data/snack.db
jwt.secret: ${JWT_SECRET:snack-manager-secret-key-2026-default}
jwt.expiration: 604800000      # 7天
shop.owner-user-id: 1           # 店铺归属的管理端用户ID
```

---

## 常见问题

| 问题 | 解决 |
|---|---|
| 数据库缺列 | 删 snack.db → 重启后端自动重建 |
| 客户端无商品 | 执行 `UPDATE snack SET is_on_shelf=1 WHERE user_id=1` |
| npm install 慢 | `npm config set registry https://registry.npmmirror.com` |
| 端口冲突 | 修改 application.yml / vite.config.ts |
| 登录 401 | 确认前后端 token key 独立（snack_admin_token / snack_shop_token） |

详见 `docs/TROUBLESHOOTING.md`

---

> 📄 完整文档：`docs/FINAL-REPORT.md`
