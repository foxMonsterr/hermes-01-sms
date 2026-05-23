# 常见问题排查 (v3.0)

> 开发过程中遇到问题时，先查阅本文档。  
> **v3.0 新增**: 用户隔离相关排查项。

---

## Knife4j doc.html 访问不了

**检查**:
1. `application.yml` 中 `knife4j.enable: true`
2. `Knife4jConfig.java` 中 `GroupedOpenApi` Bean 是否创建
3. JwtInterceptor 是否放行: `/doc.html`, `/doc.html/**`, `/webjars/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`, `/swagger-resources/**`

---

## 前端请求变成 /api/api/xxx

**原因**: `request.ts` baseURL 已是 `/api`，但 API 文件又写了 `/api/auth/login`。

**解决**: API 文件中去掉 `/api`，写 `/auth/login`。

---

## SQLite 表没有自动创建

**检查**:
1. `application.yml`: `spring.sql.init.mode=always`, `schema-locations=classpath:db/schema.sql`
2. `schema.sql` 是否在 `resources/db/` 下
3. 数据库路径: `jdbc:sqlite:./data/snack.db`
4. SQL 语法是否符合 SQLite

---

## schema.sql 中不再插入默认分类

**v3.0 变更**: schema.sql 只负责建表和建索引，不再 INSERT 全局分类。

默认分类改为用户注册时由 `UserServiceImpl.register()` 按用户初始化。

如果发现 schema.sql 中还有 INSERT 语句，请删除。

---

## 新用户登录后没有默认分类

**排查**:
1. `UserServiceImpl.register()` 是否调用了初始化默认分类方法
2. 初始化分类时是否写入了 `user_id = newUser.getId()`
3. `register()` 方法是否加了 `@Transactional`
4. 分类查询时是否按 `currentUserId` 过滤

---

## 用户能看到其他用户的分类或零食

**排查**:
1. 查询条件是否加了 `user_id = currentUserId`
2. Controller 是否从 `request.getAttribute("currentUserId")` 获取
3. Service 方法是否使用 currentUserId 参数过滤
4. 是否错误地信任了前端传入的 userId（绝对不应该）

---

## 分类重名判断异常

**排查**:
1. 唯一索引是否是 `(user_id, name) WHERE is_deleted = 0`
2. Service 查重 SQL 是否加了 `user_id = currentUserId`
3. 不同用户允许创建同名分类（这是预期行为）

---

## 登录成功但刷新后用户名丢失

**原因**: Pinia 状态刷新后丢失。

**解决**:
1. 确认后端有 `GET /auth/profile`
2. 前端 guards.ts 中检测 token 存在但 store 无用户信息时调用 `fetchProfile()`

---

## 401 后重复弹出多条提示

**解决**: request.ts 中 `isRedirecting` 标记 + 500ms 延迟复位。

---

## 访问其他用户的数据返回 403 而不是 404

**修正**: 统一返回 404，不暴露数据存在性。

```java
// Service 层
if (!snack.getUserId().equals(currentUserId)) {
    throw BusinessException.notFound("资源不存在");
}
```

---

## 注册时事务回滚不生效

**检查**:
1. `UserServiceImpl.register()` 是否加 `@Transactional`
2. 是否在同一个类内部调用（内部调用不触发 AOP）
3. 异常是否被 catch 吞掉了

---

## 编译时 Lombok 报错

- 确认 pom.xml 有 Lombok 依赖
- IDE 安装 Lombok 插件
- 开启注解处理器

---

## SQLite datetime 默认值

```sql
-- ✅ SQLite 语法
create_time DATETIME DEFAULT (datetime('now', 'localtime'))

-- ❌ MySQL 语法，SQLite 不支持
create_time DATETIME DEFAULT CURRENT_TIMESTAMP
```

---

## 部署或答辩时误执行 schema.sql

**建议**: 将 `spring.sql.init.mode` 改为 `never`，避免误执行初始化脚本覆盖已有数据。

---

> **遇到新问题时，先记录到本文档。**
