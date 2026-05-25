# 🍿 零食管理系统 — 最终项目文档

> 版本 v7.1 | 2026-05-24 | 本科毕业设计

---

## 1. 项目简介

零食管理系统是一个**管理端 + 客户端**的轻量级线上零食小卖铺。管理端负责商品库存维护、订单管理、统计分析；客户端面向消费者，支持商品浏览、购物车、模拟下单、订单追踪和商品评价。系统从 v1.0 迭代至 v7.1，最终形成完整供应链闭环。

### 核心闭环

```
管理端上架零食 → 客户端浏览购买 → 下单扣库存 →
生成订单 + 库存流水 → 管理端发货 → 客户确认收货 → 客户评价
```

---

## 2. 技术栈

| 层级 | 技术 | 说明 |
|---|---|---|
| 后端框架 | Spring Boot 3.x | RESTful API |
| ORM | MyBatis-Plus 3.5+ | Lambda 查询 |
| 数据库 | SQLite (JDBC) | 单文件嵌入式 |
| 认证 | JWT (jjwt 0.12+) | 双拦截器 (Admin + Shop) |
| 文档 | Knife4j (OpenAPI 3) | http://localhost:8080/doc.html |
| 管理端前端 | Vue 3 + TS + Element Plus + ECharts | 二次元萌系主题 |
| 客户端前端 | Vue 3 + TS + Element Plus | 珊瑚橙暖色主题 |
| 构建 | Vite 5.x | 管理端 :5173 / 客户端 :5174 |
| 状态管理 | Pinia 2.x | user / notification |

---

## 3. 项目结构

```
snack-manager/
├── README.md
├── backend/                          # Spring Boot 后端 (:8080)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/snack/
│       │   ├── SnackApplication.java
│       │   ├── common/               # Result / PageData / 全局异常
│       │   ├── config/               # CORS / JWT / MyBatis-Plus / Knife4j
│       │   ├── security/             # AdminJwt + ShopJwt 双拦截器
│       │   ├── entity/               # 17 个实体
│       │   ├── mapper/               # 15 个 Mapper
│       │   ├── dto/                  # 28 个请求体
│       │   ├── vo/                   # 22 个响应体
│       │   ├── service/              # 16 个服务接口 + 实现
│       │   └── controller/
│       │       ├── *.java            # 管理端 15 个 Controller
│       │       ├── shop/             # 客户端 5 个 Controller
│       │       └── admin/            # 管理端扩展 3 个 Controller
│       └── resources/
│           ├── application.yml
│           └── db/                   # schema / migration / test-data
├── frontend/                         # 管理端前端 (:5173)
│   └── src/
│       ├── api/                      # 13 个 API 模块
│       ├── types/                    # 12 个类型定义
│       ├── views/                    # 15 个页面
│       ├── layouts/BasicLayout.vue   # 二次元主题布局
│       ├── components/BearAssistant.vue  # 🐻 小熊助手
│       └── styles/                   # theme.css / anime.css + 4 套换肤
├── shop/                             # 客户端前端 (:5174)
│   └── src/
│       ├── api/                      # auth / products / cart / orders
│       ├── views/                    # 10 个页面
│       └── layouts/ShopLayout.vue    # 店铺布局
└── docs/                             # 项目文档
    ├── FINAL-REPORT.md               # 本文档
    ├── API-CONTRACT.md               # API 接口文档
    ├── BUSINESS_RULES.md             # 业务规则说明
    └── TROUBLESHOOTING.md            # 故障排查
```

---

## 4. 数据库设计 (17 张表)

### 管理端核心表 (v1.0 - v5.0)

| 表名 | 说明 | 字段数 |
|---|---|---|
| sys_user | 管理员用户 | 8 |
| snack_category | 零食分类 | 7 |
| snack | 零食（含 is_on_shelf/description） | 16 |
| stock_record | 库存流水 (INIT/IN/OUT/ADJUST) | 8 |
| shopping_list | 采购清单 | 15 |
| notification | 消息提醒 | 8 |
| system_config | 系统配置 | 5 |
| disposal_record | 丢弃处理记录 | 10 |
| inventory_check_record | 盘点记录 | 9 |
| supplier | 供应商 | 9 |
| operation_log | 操作日志 | 9 |

### 客户端扩展表 (v7.0)

| 表名 | 说明 | 字段数 |
|---|---|---|
| shop_user | 客户端用户 | 8 |
| shop_cart | 购物车 | 5 |
| shop_order | 订单 | 16 |
| shop_order_item | 订单明细（商品快照） | 10 |
| shop_announcement | 公告 | 8 |
| shop_review | 商品评价 | 9 |

---

## 5. API 接口 (85 个)

### 管理端 (62 个)

| Controller | 路由 | 接口数 |
|---|---|---|
| AuthController | /api/auth | 7 |
| CategoryController | /api/categories | 5 |
| SnackController | /api/snacks | 10 |
| StockRecordController | /api/stock-records | 2 |
| ShoppingListController | /api/shopping-list | 8 |
| NotificationController | /api/notifications | 5 |
| StatisticsController | /api/statistics | 5 |
| SettingsController | /api/settings | 3 |
| FileController | /api/files | 2 |
| SupplierController | /api/suppliers | 6 |
| DisposalController | /api/disposals | 3 |
| InventoryCheckController | /api/inventory-checks | 3 |
| OperationLogController | /api/logs | 1 |
| SystemController | /api/system | 3 |
| ExportController | /api/export | 1 |
| AdminShopOrderController | /api/admin/shop-orders | 5 |
| AdminShopStatController | /api/admin/shop-statistics | 1 |
| AdminReviewController | /api/admin/reviews | 2 |

### 客户端 (23 个)

| Controller | 路由 | 接口数 |
|---|---|---|
| ShopAuthController | /api/shop/auth | 5 |
| ShopProductController | /api/shop/products + /categories | 4 |
| ShopCartController | /api/shop/cart | 5 |
| ShopOrderController | /api/shop/orders | 5 |
| ShopReviewController | /api/shop/products/:id/reviews + /reviews | 3 |
| Announcements (内联) | /api/shop/announcements | 1 |

---

## 6. 页面清单

### 管理端 (15 页)

| 路由 | 页面 | 功能 |
|---|---|---|
| /login | 登录 | 毛玻璃二次元登录页 |
| /dashboard | 首页仪表盘 | 6 卡片 + ECharts 趋势 + 动态 + 🐻欢迎语 |
| /categories | 分类管理 | 卡片网格 + 点击跳筛选 |
| /snacks | 零食管理 | 卡片网格 + 表格 + 上架/过期/处理/盘点/导入 |
| /stock-records | 库存流水 | 流水 + 盘点记录 Tab |
| /shopping-list | 采购清单 | 采购 + 供应商联动 + 入库 |
| /statistics | 统计看板 | 库存/消耗/损耗 + 销售统计 |
| /notifications | 消息提醒 | 过期/低库存提醒 |
| /suppliers | 供应商管理 | CRUD + 采购统计 |
| /logs | 操作日志 | 多维度筛选 |
| /shop-orders | 店铺订单 | 发货/完成/取消 |
| /settings | 系统设置 | 资料/密码/主题换肤/数据备份 |

### 客户端 (10 页)

| 路由 | 页面 | 功能 |
|---|---|---|
| /home | 首页 | 公告 + 分类入口 + 热销 |
| /products | 商品列表 | 搜索 + 分类筛选 + 排序 |
| /products/:id | 商品详情 | 大图 + 价格 + 加购 + 立即购买 + 评价 |
| /cart | 购物车 | 数量调整 + 删除 + 结算 |
| /checkout | 结算 | 收货信息 + 提交订单 |
| /orders | 我的订单 | 状态筛选 |
| /orders/:id | 订单详情 | 评价(已完成) + 取消 + 确认收货 |
| /login | 登录 | — |
| /register | 注册 | — |
| /profile | 个人中心 | — |

---

## 7. 特色功能

| 功能 | 说明 |
|---|---|
| 🐻 小熊店长 | 右下角悬浮助手，智能提示 + 对话气泡 |
| 🎨 主题换肤 | 樱花粉/薄荷绿/天空蓝/阳光橙 4 套 |
| 📊 数据可视化 | ECharts 粉彩主题，趋势图/饼图/面积图 |
| 🛒 完整电商闭环 | 浏览 → 加购 → 下单 → 扣库存 → 发货 → 评价 |
| 🔒 双 JWT 体系 | Admin JWT + Shop JWT 独立鉴权 |
| 💬 商品评价 | 已完成订单评价，商品详情页展示 |
| 🖼️ 文件上传 | 零食图片 + 头像上传，UUID 防猜 |
| 📋 操作审计 | 15 种操作类型全记录 |

---

## 8. 版本演进

| 版本 | 阶段 | 核心内容 |
|---|---|---|
| v1.0 | 基础 | 注册登录 + 8 默认分类 + 零食 CRUD |
| v2.0 | 完善 | 用户数据隔离、分页、批量删除、Knife4j |
| v3.0 | 优化 | 登录守卫、密码修改、JWT 拦截器 |
| v4.0 | 中期增强 | 价格、库存流水、采购清单、消息提醒、统计 |
| v5.0 | 后期收尾 | 文件上传、系统配置、过期处理、盘点、供应商、日志、备份 |
| v6.0 | UI 升级 | 二次元萌系主题、小熊助手、主题换肤、卡片网格 |
| v7.0 | 小卖铺 | 客户端前端、购物车、下单、订单管理、销售统计 |
| v7.1 | 评价 | 商品评价系统、Bug 修复 |

---

## 9. 文件统计

| 分类 | 数量 |
|---|---|
| 后端 Java 文件 | 150+ |
| 前端 TS/Vue 文件 | 80+ |
| 数据库表 | 17 |
| API 接口 | 85 |
| 管理端页面 | 15 |
| 客户端页面 | 10 |
