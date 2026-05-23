# 🍿 零食管理系统

> Vue 3 + Spring Boot 3 + SQLite | 个人/团队零食库存管理工具

---

## 项目简介

零食管理系统是一个面向个人和小团队的库存管理工具，支持零食的采购入库、消耗出库、过期处理、库存盘点、供应商管理、统计分析等功能。系统从 v1.0 迭代至 v5.0，具备用户数据隔离、JWT 认证、操作审计、数据备份等完整能力。

---

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Element Plus + ECharts + Vite |
| 后端 | Spring Boot 3 + MyBatis-Plus + Knife4j |
| 数据库 | SQLite (JDBC) |
| 认证 | JWT (jjwt) + BCrypt |
| 状态管理 | Pinia |

---

## 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.8+
- **Node.js** 18+
- **npm** 9+
- **IDE** IntelliJ IDEA (推荐)

### 1. 克隆项目

```bash
git clone <仓库地址>
cd snack-manager
```

### 2. 启动后端

用 IDEA 打开 `backend/` 目录，等待 Maven 依赖下载完成后运行 `SnackApplication.java`。

> 首次启动会自动在 `backend/data/` 下创建 `snack.db` 并执行建表脚本。

后端默认运行在 **http://localhost:8080**

API 文档地址: **http://localhost:8080/doc.html**

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 **http://localhost:5173**

### 4. 灌入测试数据

项目内置了演示数据脚本，使用 DataGrip 或任何 SQLite 客户端连接 `backend/data/snack.db`，执行：

```
backend/src/main/resources/db/test-data.sql
```

> 测试账户: **admin** / **123456**

---

## 项目结构

```
snack-manager/
├── backend/                        # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/snack/
│       │   ├── SnackApplication.java    # 启动类
│       │   ├── common/                   # 公共模块(Result/异常处理/分页)
│       │   ├── config/                   # 配置(CORS/JWT/MyBatis-Plus/Knife4j)
│       │   ├── security/                 # JWT 生成与拦截
│       │   ├── entity/                   # 实体 (11 个)
│       │   ├── mapper/                   # MyBatis-Plus Mapper
│       │   ├── dto/                      # 请求体 (24 个)
│       │   ├── vo/                       # 响应体 (20 个)
│       │   ├── service/                  # 服务接口 + 实现
│       │   └── controller/               # 控制器 (15 个, 62 个 API)
│       └── resources/
│           ├── application.yml           # 应用配置
│           └── db/                        # SQL 脚本
├── frontend/                       # Vue 3 前端
│   ├── package.json
│   └── src/
│       ├── api/                          # API 调用层
│       ├── types/                        # TypeScript 类型
│       ├── stores/                       # Pinia 状态管理
│       ├── router/                       # 路由 + 登录守卫
│       ├── utils/                        # 工具函数
│       ├── layouts/                      # 页面布局
│       └── views/                        # 页面组件 (14 个)
└── docs/                           # 项目文档
    ├── PROJECT-REPORT.md                 # 项目收尾报告
    ├── API-CONTRACT.md                   # API 接口文档
    ├── BUSINESS_RULES.md                 # 业务规则说明
    └── TROUBLESHOOTING.md                # 故障排查指南
```

---

## 功能模块

| 模块 | 功能 |
|---|---|
| 用户认证 | 注册 / 登录 / 修改密码 / 修改资料 / 头像上传 |
| 分类管理 | 8 个默认分类 + 自定义 CRUD |
| 零食管理 | CRUD + 数量调整 + 批量删除 + 图片上传 + 批量导入 |
| 库存流水 | 入库 / 出库 / 调整 全记录 |
| 采购清单 | 待采购 / 已采购 / 已取消 + 低库存自动生成 + 供应商联动 |
| 统计看板 | 库存概览 / 出入库趋势 / 分类占比 / 消耗分析 / 损耗统计 |
| 消息提醒 | 过期提醒 / 低库存提醒 / 库存告罄提醒 |
| 过期处理 | 过期 / 损坏 / 手动处理记录 + 损耗金额统计 |
| 库存盘点 | 系统库存 vs 实际库存 + 差异调整 |
| 供应商 | 供应商 CRUD + 采购统计 |
| 操作日志 | 15 种操作类型多维度筛选 |
| 系统设置 | 个人资料 / 密码 / 系统偏好 / 数据备份 |
| 数据维护 | 数据库备份下载 |

---

## 配置说明

核心配置文件: `backend/src/main/resources/application.yml`

```yaml
# 数据库路径
spring.datasource.url: jdbc:sqlite:C:/Users/Administrator/snack-manager/backend/data/snack.db

# JWT 密钥 (可通过环境变量 JWT_SECRET 覆盖)
jwt.secret: snack-manager-secret-key-2026-default-very-long-secret

# JWT 过期时间 (默认 7 天)
jwt.expiration: 604800000

# 文件上传
app.upload.base-path: ./uploads
app.upload.max-size: 2MB
```

---

## 数据库迁移

如果需要从旧版本升级:

1. 备份 `backend/data/snack.db`
2. DataGrip 连接数据库，执行 `backend/src/main/resources/db/migration.sql`
3. ALTER TABLE 报错 `duplicate column` 可忽略，`CREATE TABLE IF NOT EXISTS` 幂等安全

---

## 常见问题

| 问题 | 解决 |
|---|---|
| 登录 500 | 数据库缺列 → 执行 `migration.sql` |
| npm install 失败 | 切换镜像: `npm config set registry https://registry.npmmirror.com` |
| Maven 依赖下载慢 | IDEA → Settings → Maven → 使用阿里云镜像 |
| 数据库锁定 | 关掉 IDEA 数据库工具窗口再重启后端 |
| 端口占用 | 修改 `application.yml` 中 `server.port` |

详见 `docs/TROUBLESHOOTING.md`

---

## 许可证

本项目为本科毕业设计项目，仅供学习参考。

---

> 📄 完整文档: `docs/PROJECT-REPORT.md`
