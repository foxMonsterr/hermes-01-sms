零食管理系统 
版本 v5.0 | 日期 2026-05-23 |

1. 项目概述
零食管理系统是一个面向个人/小团队使用的库存管理工具，帮助用户记录零食的采购、消耗、过期处理，并提供统计分析和智能提醒。系统从 v1.0 逐步迭代至 v5.0，从基础的分类+零食管理演进为具备配置化、流程闭环、审计追踪、数据维护能力的完整毕设级系统。

2. 技术栈
层级	技术	版本
后端框架	Spring Boot	3.x
ORM	MyBatis-Plus	3.5+
数据库	SQLite (JDBC)	—
认证	JWT (jjwt)	0.12+
API 文档	Knife4j (OpenAPI 3)	4.x
前端框架	Vue 3 + Composition API	3.4+
语言	TypeScript	5.x
UI 库	Element Plus	2.x
构建工具	Vite	5.x
图表	ECharts	5.x
状态管理	Pinia	2.x
HTTP 客户端	Axios	1.x
3. 项目结构
snack-manager/
├── backend/                          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/snack/
│       │   ├── SnackApplication.java       # 启动类
│       │   ├── common/                      # 公共模块 (5 个)
│       │   │   ├── Result.java              # 统一响应体
│       │   │   ├── PageData.java             # 分页数据
│       │   │   ├── BusinessException.java    # 业务异常
│       │   │   ├── ErrorCode.java            # 错误码常量
│       │   │   └── GlobalExceptionHandler.java # 全局异常处理
│       │   ├── config/                       # 配置 (4 个)
│       │   │   ├── WebMvcConfig.java         # CORS + JWT 拦截器
│       │   │   ├── SecurityConfig.java       # BCrypt 密码编码器
│       │   │   ├── MyBatisPlusConfig.java    # 自动填充
│       │   │   └── Knife4jConfig.java        # API 文档
│       │   ├── security/                     # 安全 (2 个)
│       │   │   ├── JwtUtil.java              # JWT 生成/解析
│       │   │   └── JwtInterceptor.java       # 请求拦截校验
│       │   ├── entity/                       # 实体 (11 个)
│       │   │   ├── User.java                 # 用户 (含 avatarUrl)
│       │   │   ├── Category.java             # 分类
│       │   │   ├── Snack.java                # 零食 (含 price/imageUrl)
│       │   │   ├── StockRecord.java          # 库存流水
│       │   │   ├── ShoppingList.java         # 采购清单
│       │   │   ├── Notification.java         # 消息提醒
│       │   │   ├── SystemConfig.java         # 系统配置
│       │   │   ├── DisposalRecord.java       # 丢弃记录
│       │   │   ├── InventoryCheckRecord.java # 盘点记录
│       │   │   ├── Supplier.java             # 供应商
│       │   │   └── OperationLog.java         # 操作日志
│       │   ├── mapper/                       # MyBatis-Plus Mapper (11 个)
│       │   ├── dto/                          # 请求体 (24 个)
│       │   ├── vo/                           # 响应体 (20 个)
│       │   ├── service/                      # 服务接口 (12 个)
│       │   ├── service/impl/                 # 服务实现 (12 个)
│       │   └── controller/                   # 控制器 (15 个)
│       │       ├── AuthController.java       # 认证 (注册/登录/密码/资料/头像)
│       │       ├── CategoryController.java   # 分类 CRUD
│       │       ├── SnackController.java      # 零食 CRUD + 批量删除 + 导入导出
│       │       ├── StockRecordController.java # 库存流水查询
│       │       ├── ShoppingListController.java # 采购清单 (含低库存生成/入库)
│       │       ├── NotificationController.java # 消息提醒
│       │       ├── StatisticsController.java   # 统计 (概览/趋势/价值/消耗)
│       │       ├── SettingsController.java     # 系统配置
│       │       ├── FileController.java         # 文件上传/访问
│       │       ├── SupplierController.java     # 供应商 CRUD
│       │       ├── DisposalController.java     # 过期处理
│       │       ├── InventoryCheckController.java # 库存盘点
│       │       ├── OperationLogController.java   # 操作日志
│       │       ├── SystemController.java         # 备份/健康检查/版本
│       │       └── ExportController.java         # 数据导出
│       └── resources/
│           ├── application.yml              # 应用配置
│           └── db/
│               ├── schema.sql               # v5.0 完整建表脚本
│               ├── migration.sql            # v3→v5 一键迁移
│               ├── migration-v4.sql         # v3→v4 迁移 (已合并)
│               ├── migration-v5.sql         # v4→v5 迁移 (已合并)
│               └── test-data.sql            # 测试数据
├── frontend/                          # Vue 3 前端
│   ├── package.json
│   └── src/
│       ├── main.ts                      # 入口
│       ├── App.vue                      # 根组件
│       ├── api/                         # API 层 (10 个)
│       │   ├── auth.ts                  # 认证 API
│       │   ├── category.ts              # 分类 API
│       │   ├── snack.ts                 # 零食 API
│       │   ├── stockRecord.ts           # 流水 API
│       │   ├── shoppingList.ts          # 采购 API
│       │   ├── statistics.ts            # 统计 API
│       │   ├── notification.ts          # 提醒 API
│       │   ├── systemConfig.ts          # 配置 API
│       │   ├── system.ts                # 备份/健康 API
│       │   ├── supplier.ts              # 供应商 API
│       │   ├── disposal.ts             # 处理 API
│       │   ├── inventoryCheck.ts       # 盘点 API
│       │   └── operationLog.ts         # 日志 API
│       ├── types/                       # 类型定义 (12 个)
│       ├── stores/                      # 状态管理 (2 个)
│       │   ├── user.ts                  # 用户状态 (含 avatarUrl)
│       │   └── notification.ts          # 未读消息
│       ├── router/                      # 路由 (2 个)
│       │   ├── index.ts                 # 14 条路由
│       │   └── guards.ts                # 登录守卫
│       ├── utils/                       # 工具 (3 个)
│       │   ├── request.ts               # Axios 封装 (含 upload/download)
│       │   ├── token.ts                 # localStorage token
│       │   └── download.ts              # Blob 下载
│       ├── layouts/
│       │   └── BasicLayout.vue          # 主布局 (侧栏+顶栏+头像)
│       └── views/                       # 页面 (14 个)
│           ├── LoginView.vue            # 登录页
│           ├── DashboardView.vue        # 首页仪表盘
│           ├── CategoryListView.vue     # 分类管理
│           ├── SnackListView.vue        # 零食管理 (含处理/盘点/导入/图片)
│           ├── StockRecordView.vue      # 库存流水 + 盘点记录
│           ├── ShoppingListView.vue     # 采购清单 (含供应商联动)
│           ├── StatisticsView.vue       # 统计看板 (3 Tab)
│           ├── NotificationView.vue     # 消息提醒
│           ├── SupplierListView.vue     # 供应商管理
│           ├── OperationLogView.vue     # 操作日志
│           ├── SettingsView.vue         # 系统设置 (4 Tab)
│           └── NotFoundView.vue         # 404
└── docs/                               # 项目文档 (10 个)
    ├── project-design.md                # 项目设计文档 v1.0
    ├── backend-plan.md                  # 后端实现计划 v2.0
    ├── frontend-plan.md                 # 前端实现计划 v2.0
    ├── midterm-plan.md                  # 中期方案 v4.0
    ├── v4-mid-term-enhancement.md       # v4.0 增强说明
    ├── final-plan.md                    # 后期收尾方案 v5.0
    ├── frontend-plan-v5.md              # 前端 v5.0 计划
    ├── API-CONTRACT.md                  # API 接口文档
    ├── BUSINESS_RULES.md                # 业务规则说明
    └── TROUBLESHOOTING.md               # 故障排查指南
4. 数据库设计 (11 张表)
#	表名		字段数	说明
1	sys_user	8	用户表
2	snack_category	7	分类表
3	snack 	13	零食表 
4	stock_record		8	库存流水表
5	shopping_list	15	采购清单表
6	notification		8	消息提醒表
7	system_config	5	系统配置表
8	disposal_record	10	过期/丢弃记录表
9	inventory_check_record		9	库存盘点记录表
10	supplier	9	供应商表
11	operation_log 	9	操作日志表
核心设计原则
用户数据隔离：所有业务表含 user_id，查询强制过滤，跨用户数据返回 404
逻辑删除：user / category / snack / shopping_list / supplier 支持 is_deleted
快照设计：stock_record.snack_name、shopping_list.supplier_name 采用快照，源数据变更不影响历史记录
流水审计：库存变动生成 stock_record（INIT/IN/OUT/ADJUST 四种类型）
5. API 接口清单 (后端 15 个 Controller)
Controller	路由前缀	接口数	核心功能
AuthController	/api/auth	7	注册/登录/密码/资料/头像上传
CategoryController	/api/categories	5	分类 CRUD + 列表
SnackController	/api/snacks	10	零食 CRUD + 批量删除 + 数量调整 + 导入导出
StockRecordController	/api/stock-records	2	流水查询 + 统计
ShoppingListController	/api/shopping-list	8	采购清单 CRUD + 低库存生成 + 入库联动
NotificationController	/api/notifications	5	未读数/列表/已读/生成/全部已读
StatisticsController	/api/statistics	5	概览/分类分布/出入库趋势/库存价值/消耗分析
SettingsController	/api/settings	3	系统配置(读/写/重置)
FileController	/api/files	2	图片上传/访问 (限2MB, UUID文件名)
SupplierController	/api/suppliers	6	供应商 CRUD + 统计
DisposalController	/api/disposals	3	过期处理 + 列表 + 统计
InventoryCheckController	/api/inventory-checks	3	盘点 + 列表 + 统计
OperationLogController	/api/logs	1	多维度筛选查询
SystemController	/api/system	3	数据库备份/健康检查/版本信息
ExportController	/api/export	1	数据导出
总计: 62 个 API 接口

6. 前端页面清单 (14 个页面)
路由	页面	核心功能
/login	LoginView	登录表单
/dashboard	DashboardView	6 张统计卡片 + 出入库趋势图 + 分类饼图 + 最近操作
/categories	CategoryListView	分类 CRUD 表格
/snacks	SnackListView	零食表格 + 数量调整 + 图片上传 + 过期处理 + 库存盘点 + 批量导入
/stock-records	StockRecordView	库存流水 Tab + 盘点记录 Tab
/shopping-list	ShoppingListView	采购清单(待采购/已采购/已取消) + 低库存生成 + 入库 + 供应商选择
/statistics	StatisticsView	3 Tab: 库存统计 + 消耗分析 + 损耗统计
/notifications	NotificationView	消息提醒列表
/suppliers	SupplierListView	供应商 CRUD + 采购统计弹窗
/logs	OperationLogView	操作日志多维度筛选
/settings	SettingsView	4 Tab: 个人资料/修改密码/系统偏好/数据维护
*	NotFoundView	404 页面
7. 版本演进
版本	阶段	新增内容
v1.0	基础	用户注册登录 + 默认分类 + 零食 CRUD
v2.0	完善	用户数据隔离、分页、批量删除、Knife4j 文档
v3.0	优化	登录守卫、密码修改、JWT 拦截器、前端完善
v4.0	中期增强	价格管理、库存流水、采购清单、消息提醒、统计看板
v5.0	后期收尾	文件上传(头像/图片)、系统配置、过期处理、库存盘点、供应商管理、操作日志、数据备份、消耗分析
8. 启动方式
后端
# IDEA 打开 backend/ 目录
# 等待 Maven 依赖下载完成
# 运行 SnackApplication.java (端口 8080)
# API 文档: http://localhost:8080/doc.html
前端
cd frontend
npm install
npm run dev          # 开发服务器 (端口 5173)
首次运行（数据库）
启动后端会自动执行 schema.sql 建表
如需迁移旧数据库：DataGrip 执行 db/migration.sql
灌测试数据：DataGrip 执行 db/test-data.sql（账户 admin/123456）
9. 关键配置
配置项	位置	默认值
数据库路径	application.yml → spring.datasource.url	C:/Users/Administrator/snack-manager/backend/data/snack.db
JWT 密钥	application.yml → jwt.secret	环境变量 JWT_SECRET 或默认值
JWT 过期	application.yml → jwt.expiration	604800000ms (7天)
上传目录	application.yml → app.upload.base-path	./uploads
文件大小限制	application.yml → app.upload.max-size	2MB
低库存阈值	system_config 表	2
过期提醒天数	system_config 表	30
默认分页大小	system_config 表	10
前端 API 地址	.env → VITE_API_BASE_URL	(需配置)
10. 已知限制与改进方向
限制	说明
SQLite 并发	单文件数据库，不适合高并发生产环境
文件存储	本地磁盘存储，未使用对象存储 (OSS)
备份功能	仅 dev/demo 环境适用，生产需额外方案
移动端	未做响应式适配 (Phase 25 未实施)
图片访问	无需登录（URL 通过 UUID 防猜，但无鉴权）
数据导出	仅 CS V格式（未实现 Excel 带样式导出）
11. 文件统计
分类	数量
后端 Java 文件	124 个
前端 TypeScript 文件	35 个
前端 Vue 组件	14 个
SQL 脚本	5 个
项目文档 (Markdown)	10 个
API 接口	62 个
数据库表	11 张
