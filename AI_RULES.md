# AI 开发规则

## 项目说明

这是一个前后端分离的零食管理系统，支持多用户登录与用户级数据隔离，采用后台管理系统形式实现。每个用户只能访问自己的分类和零食数据。

## 技术栈

### 前端

- Vue 3
- TypeScript
- Vite
- Element Plus
- Vue Router
- Pinia
- Axios

### 后端

- Java 17
- Spring Boot 3
- Maven
- MyBatis-Plus
- SQLite
- JWT
- Knife4j
- Lombok
- Validation

## 通用规则

1. 修改代码前先说明计划。
2. 每次只实现一个 Task。
3. 不要一次性大规模修改无关文件。
4. 不要引入新依赖，除非先说明原因。
5. 如果文档和代码不一致，先指出并询问。
6. 不确定的内容不要猜，列为"需要确认"。
7. 代码必须保持类型清晰。

## 后端规则

1. 所有接口返回 Result<T>。
   - 成功 code 统一为 200。
   - 响应字段统一使用 msg，不使用 message。
   - data 为 null 时也要返回。
2. 分页接口返回 Result<PageData<T>>。
   - PageData<T> 包含 records、total、page、size。
   - PageData 不再包含 code/msg。
3. 业务异常使用 BusinessException。
   - 不要直接抛 RuntimeException 表达业务错误。
   - BusinessException 包含 code 和 msg。
4. Controller 不写复杂业务逻辑。
5. Service 负责业务规则。
6. Mapper 负责数据访问。
7. Controller 和 DTO/VO 必须添加 Knife4j/OpenAPI 注解：
   - @Tag(name, description) 在 Controller 类上。
   - @Operation(summary, description) 在接口方法上。
   - @Parameter(description, example) 在参数上。
   - @Schema(description, example) 在 DTO/VO 类及字段上。
8. 请求参数必须添加 Validation 注解（@NotBlank、@NotNull、@Size 等）。
9. 密码必须使用 BCrypt 加密，响应中永远不能返回 password。
10. JWT 拦截失败必须返回统一 JSON，不能只 setStatus。
11. 删除优先使用逻辑删除。
12. SQLite 表初始化必须遵守 application.yml 中的 sql.init 配置。

## 前端规则

1. 所有代码使用 TypeScript。
2. 不允许使用 any，除非说明原因。
3. API 请求统一放在 src/api。
4. 类型定义统一放在 src/types。
5. 不允许在组件中直接写 axios。
6. token 操作统一使用 src/utils/token.ts。
7. baseURL 使用 /api，接口路径不要重复写 /api。
   正确: `request.post('/auth/login')`
   错误: `request.post('/api/auth/login')`
8. 表格页面必须包含 loading、empty、error 状态。
9. 删除操作必须二次确认。
10. 表单提交按钮必须有 loading 状态。

## 接口契约

- 统一响应: `{ "code": 200, "msg": "操作成功", "data": {} }`
- 成功 code: 200
- 错误码: 400、401、404、409、500（不使用 403，其他用户数据返回 404）
- 认证: `Authorization: Bearer <token>`
- 用户隔离: 前端不传 userId，后端从 JWT 解析 currentUserId
- 详细规范见 docs/API-CONTRACT.md

## 数据库

- 表名: sys_user、snack_category、snack
- 逻辑删除: is_deleted (0=正常, 1=删除)
- 唯一性: 对未删除数据使用 partial unique index
- 详细规范见 docs/BUSINESS_RULES.md
