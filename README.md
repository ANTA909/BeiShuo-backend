# 碑说（Beishuo）后端开发文档

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [核心设计理念](#核心设计理念)
- [接口规范](#接口规范)
- [代码规范](#代码规范)
- [配置说明](#配置说明)
- [数据库服务对接指南](#数据库服务对接指南)
- [LLM与RAG服务对接指南](#llm与rag服务对接指南)
- [开发环境搭建](#开发环境搭建)
- [常见问题](#常见问题)

---

## 项目概述

**碑说（Beishuo）** 是一个专注于古代碑文识别、阐释和知识管理的智能系统。系统通过OCR技术识别碑文图片，利用大语言模型（LLM）和检索增强生成（RAG）技术提供智能阐释，并构建知识库供用户浏览和搜索。

### 核心功能模块

1. **用户认证模块**：用户注册、登录、JWT Token管理
2. **碑文管理模块**：碑文图片上传、OCR识别、校对、CRUD操作
3. **知识库模块**：知识库浏览、搜索、收藏、推荐
4. **AI阐释模块**：基于LLM的碑文阐释生成、智能对话
5. **需求文档模块**：需求文档的存储和检索

### 系统架构

```
┌─────────────┐
│   前端应用   │
└──────┬──────┘
       │ HTTP/REST
┌──────▼─────────────────────────────────────┐
│         碑说后端服务 (本服务)                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │Controller│→ │ Service  │→ │  Client  │ │
│  └──────────┘  └──────────┘  └────┬─────┘ │
└───────────────────────────────────┼───────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
┌───────▼──────┐          ┌────────▼────────┐        ┌────────▼────────┐
│ 数据库服务    │          │   Redis服务      │        │  LLM/RAG服务    │
│ (MySQL)      │          │   (缓存)        │        │  (AI服务)       │
└──────────────┘          └─────────────────┘        └─────────────────┘
```

本后端服务作为**核心业务逻辑层**，负责：
- 接收前端请求并处理业务逻辑
- 调用外部服务（数据库、Redis、LLM、RAG）
- 统一响应格式和异常处理
- JWT认证和权限管理

---

## 技术栈

- **框架**：Spring Boot 2.7.18
- **语言**：Java 17
- **认证**：JWT (JSON Web Token)
- **HTTP客户端**：Spring RestTemplate
- **构建工具**：Maven
- **日志**：SLF4J + Logback
- **工具库**：Lombok

---

## 项目结构

```
src/main/java/com/beishuo/
├── beishuoApplication.java          # 应用启动类
│
├── client/                          # 外部服务客户端（HTTP调用）
│   ├── DatabaseClient.java          # 数据库服务客户端
│   ├── RedisClient.java             # Redis服务客户端
│   └── LLMClient.java               # LLM服务客户端
│
├── common/                          # 公共工具类
│   ├── Result.java                  # 统一响应封装
│   ├── ResultCode.java              # 响应码枚举
│   ├── PageResult.java              # 分页结果封装
│   ├── JwtUtil.java                 # JWT工具类
│   └── exception/                   # 异常处理
│       ├── BusinessException.java
│       ├── UnauthorizedException.java
│       └── GlobalExceptionHandler.java
│
├── config/                          # 配置类
│   ├── SecurityConfig.java          # Spring Security配置
│   ├── CorsConfig.java              # 跨域配置
│   ├── RestTemplateConfig.java      # HTTP客户端配置
│   └── WebMvcConfig.java            # Web MVC配置
│
├── controller/                      # 控制器层（REST API）
│   ├── AuthController.java          # 认证相关接口
│   ├── InscriptionController.java   # 碑文相关接口
│   ├── KnowledgeController.java     # 知识库相关接口
│   ├── InterpretationController.java # AI阐释相关接口
│   └── RequirementDocController.java # 需求文档接口
│
├── service/                         # 业务逻辑层
│   ├── AuthService.java             # 认证服务
│   ├── InscriptionService.java     # 碑文服务
│   ├── KnowledgeService.java        # 知识库服务
│   ├── InterpretationService.java   # 阐释服务
│   └── RequirementDocService.java   # 需求文档服务
│
├── filter/                          # 过滤器
│   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   └── JwtTokenProvider.java        # JWT Token提供者
│
└── dto/                             # 数据传输对象（部分已定义，待完善）
    ├── request/                     # 请求DTO
    ├── response/                     # 响应DTO
    └── external/                     # 外部服务DTO
```

### 分层架构说明

1. **Controller层**：接收HTTP请求，参数验证，调用Service层，返回统一响应
2. **Service层**：核心业务逻辑，调用Client层访问外部服务，处理缓存
3. **Client层**：封装外部服务HTTP调用，统一异常处理
4. **Common层**：公共工具类、统一响应格式、异常处理

---

## 核心设计理念

### 1. 统一响应格式

所有API接口统一使用 `Result<T>` 封装响应：

```java
{
    "code": 200,                    // 状态码
    "message": "success",           // 消息
    "data": {...},                  // 数据（泛型）
    "timestamp": 1699123456789      // 时间戳
}
```

**成功响应示例**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "test"
    },
    "timestamp": 1699123456789
}
```

**错误响应示例**：
```json
{
    "code": 401,
    "message": "未授权，请先登录",
    "data": null,
    "timestamp": 1699123456789
}
```

### 2. 分页响应格式

列表接口使用 `PageResult<T>` 封装分页数据：

```java
{
    "list": [...],           // 数据列表
    "total": 100,            // 总记录数
    "page": 0,               // 当前页码（从0开始）
    "size": 10,              // 每页大小
    "totalPages": 10         // 总页数
}
```

### 3. JWT认证机制

- **Token格式**：`Bearer <token>`
- **请求头**：`Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...`
- **Token有效期**：
    - Access Token：24小时（86400000ms）
    - Refresh Token：7天（604800000ms）
- **Token内容**：包含 `userId` 和 `username`

### 4. 外部服务调用模式

所有外部服务通过HTTP REST API调用，使用 `RestTemplate` 进行通信：

- **数据库服务**：`http://localhost:8081/api/database`
- **Redis服务**：`http://localhost:8082/api/redis`
- **LLM服务**：`https://api.openai.com/v1`（示例）
- **RAG服务**：`http://localhost:8083/api/rag`

### 5. 缓存策略

- **列表查询**：使用Redis缓存，TTL可配置
- **搜索结果**：使用Redis缓存，TTL可配置
- **缓存Key规范**：`模块:操作:参数1:参数2:...`
    - 示例：`inscription:list:1:0:10:created:desc`
    - 示例：`knowledge:search:keyword:page:size`

---

## 接口规范

### 基础路径

所有接口的基础路径为：`/api`

### 接口列表

#### 1. 认证相关接口 (`/api/auth`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/auth/register` | 用户注册 | 否 |
| POST | `/auth/login` | 用户登录 | 否 |
| POST | `/auth/logout` | 用户登出 | 是 |
| GET | `/auth/info` | 获取当前用户信息 | 是 |
| POST | `/auth/refresh` | 刷新Token | 否 |

**登录请求示例**：
```json
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

**登录响应示例**：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzUxMiJ9...",
        "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
        "userInfo": {
            "id": 1,
            "username": "test",
            "email": "user@example.com"
        }
    },
    "timestamp": 1699123456789
}
```

#### 2. 碑文相关接口 (`/api/inscription`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/inscription/upload` | 上传碑文图片 | 是 |
| POST | `/inscription/recognize` | 提交识别任务 | 是 |
| GET | `/inscription/status/{taskId}` | 查询识别状态 | 是 |
| GET | `/inscription/{id}/result` | 获取识别结果 | 是 |
| PUT | `/inscription/{id}/save-proofread` | 保存校对结果 | 是 |
| POST | `/inscription/{id}/re-recognize` | 重新识别 | 是 |
| GET | `/inscription/list` | 获取我的碑文列表 | 是 |
| GET | `/inscription/{id}` | 获取碑文详情 | 是 |
| PUT | `/inscription/{id}` | 更新碑文 | 是 |
| DELETE | `/inscription/{id}` | 删除碑文 | 是 |
| POST | `/inscription/{id}/favorite` | 收藏碑文 | 是 |
| DELETE | `/inscription/{id}/favorite` | 取消收藏 | 是 |
| POST | `/inscription/{id}/publish` | 发布到知识库 | 是 |
| GET | `/inscription/search` | 搜索碑文 | 否 |

**获取碑文列表请求示例**：
```
GET /api/inscription/list?page=0&size=10&sort=created:desc&keyword=碑文
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

#### 3. 知识库相关接口 (`/api/knowledge`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| GET | `/knowledge/list` | 获取知识库列表 | 否 |
| GET | `/knowledge/{id}` | 获取知识库详情 | 否 |
| GET | `/knowledge/search` | 搜索知识库 | 否 |
| POST | `/knowledge/{id}/favorite` | 收藏知识库 | 是 |
| DELETE | `/knowledge/{id}/favorite` | 取消收藏 | 是 |
| GET | `/knowledge/recommend` | 推荐知识库 | 否 |
| GET | `/knowledge/latest` | 最新收录 | 否 |

#### 4. AI阐释相关接口 (`/api/interpretation`)

| 方法 | 路径 | 说明 | 需要认证 |
|------|------|------|----------|
| POST | `/interpretation/generate` | 生成AI阐释 | 是 |
| POST | `/interpretation/chat` | AI对话 | 是 |
| GET | `/interpretation/{id}` | 获取阐释详情 | 是 |

**生成AI阐释请求示例**：
```json
POST /api/interpretation/generate
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

{
    "inscriptionId": 1,
    "text": "碑文内容...",
    "dynasty": "唐代"
}
```

### 请求参数规范

1. **路径参数**：使用 `@PathVariable`，如 `/inscription/{id}`
2. **查询参数**：使用 `@RequestParam`，如 `?page=0&size=10`
3. **请求体**：使用 `@RequestBody`，Content-Type: `application/json`
4. **文件上传**：使用 `@RequestParam("file") MultipartFile`，Content-Type: `multipart/form-data`

### 响应码规范

| 状态码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 所有成功请求 |
| 400 | 请求参数错误 | 参数验证失败 |
| 401 | 未授权 | Token无效或过期 |
| 403 | 无权限 | 权限不足 |
| 404 | 资源不存在 | 资源未找到 |
| 500 | 服务器内部错误 | 系统异常 |

业务错误码定义在 `ResultCode` 枚举中，如：
- `1001`: 用户不存在
- `2001`: 碑文不存在
- `3001`: 知识库内容不存在

---

## 代码规范

### 1. 命名规范

- **类名**：大驼峰（PascalCase），如 `InscriptionService`
- **方法名**：小驼峰（camelCase），如 `getInscriptionById`
- **变量名**：小驼峰（camelCase），如 `userId`
- **常量名**：全大写下划线分隔，如 `MAX_FILE_SIZE`
- **包名**：全小写，如 `com.beishuo.service`

### 2. 注释规范

- **类注释**：说明类的职责
- **方法注释**：使用JavaDoc格式，说明方法功能、参数、返回值
- **复杂逻辑**：添加行内注释说明

**示例**：
```java
/**
 * 获取碑文详情
 * @param id 碑文ID
 * @return 碑文详情数据
 * @throws BusinessException 当碑文不存在时抛出
 */
public Map<String, Object> getInscriptionDetail(Long id) {
    // 实现逻辑
}
```

### 3. 异常处理规范

- **业务异常**：使用 `BusinessException`，会被 `GlobalExceptionHandler` 统一处理
- **未授权异常**：使用 `UnauthorizedException`
- **参数验证**：使用 `@Valid` 注解和 `@RequestBody` 配合使用

**示例**：
```java
if (inscription == null) {
    throw new BusinessException(ResultCode.INSCRIPTION_NOT_FOUND);
}
```

### 4. 日志规范

- **使用SLF4J**：`private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`
- **日志级别**：
    - `logger.error()`: 错误信息，需要立即处理
    - `logger.warn()`: 警告信息，可能的问题
    - `logger.info()`: 重要业务信息，如用户操作
    - `logger.debug()`: 调试信息，开发时使用

**示例**：
```java
logger.info("用户登录成功: email={}", email);
logger.error("查询用户失败: id={}", id, e);
```

### 5. 代码组织规范

- **一个类一个职责**：每个类只负责一个功能模块
- **方法长度**：单个方法不超过50行（建议）
- **避免深层嵌套**：if嵌套不超过3层
- **使用Lombok**：减少样板代码，如 `@Data`, `@AllArgsConstructor`

---

## 配置说明

### 配置文件位置

- **主配置**：`src/main/resources/application.yml`
- **开发环境配置**：`src/main/resources/application-dev.yml`

### 关键配置项

#### 1. 服务器配置

```yaml
server:
  port: 8080                              # 服务端口
  servlet:
    context-path: /api                    # 上下文路径
```

#### 2. 数据库服务配置

```yaml
database:
  api:
    base-url: http://localhost:8081/api/database  # 数据库服务地址
    connect-timeout: 5000                 # 连接超时（毫秒）
    read-timeout: 10000                   # 读取超时（毫秒）
    retry-times: 3                        # 重试次数
```

#### 3. Redis服务配置

```yaml
redis:
  api:
    base-url: http://localhost:8082/api/redis     # Redis服务地址
    connect-timeout: 3000                 # 连接超时（毫秒）
    read-timeout: 5000                    # 读取超时（毫秒）
    retry-times: 3                         # 重试次数
```

#### 4. LLM服务配置

```yaml
llm:
  api:
    base-url: https://api.openai.com/v1   # LLM服务地址
    api-key: your-llm-api-key-here        # API密钥
    model: gpt-4                           # 模型名称
    temperature: 0.7                       # 温度参数
    max-tokens: 2000                       # 最大token数
    timeout: 30000                        # 超时时间（毫秒）
```

#### 5. RAG服务配置

```yaml
rag:
  api:
    base-url: http://localhost:8083/api/rag      # RAG服务地址
    connect-timeout: 5000                 # 连接超时（毫秒）
    read-timeout: 15000                   # 读取超时（毫秒）
    top-k: 5                              # 检索top-k结果
```

#### 6. JWT配置

```yaml
jwt:
  secret: K8mN2pQ7vX4wZ9aB3cD5eF6gH1jL8nM0qR2sT4uV6wY8zA1bC3dE5fG7hI9jK0  # 密钥（需修改）
  expiration: 86400000                    # Token有效期（24小时）
  refresh-expiration: 604800000           # Refresh Token有效期（7天）
  header: Authorization                   # 请求头名称
  token-prefix: Bearer                    # Token前缀
```

#### 7. 文件上传配置

```yaml
file:
  upload:
    path: ./uploads                        # 上传目录
    max-size: 10485760                    # 最大文件大小（10MB）
    allowed-types: jpg,jpeg,png,webp       # 允许的文件类型
    url-prefix: /uploads                   # URL前缀
```

#### 8. 缓存配置

```yaml
cache:
  inscription-list-ttl: 300                # 碑文列表缓存TTL（秒）
  knowledge-list-ttl: 600                   # 知识库列表缓存TTL（秒）
  search-result-ttl: 600                   # 搜索结果缓存TTL（秒）
  user-info-ttl: 1800                      # 用户信息缓存TTL（秒）
```

#### 9. 跨域配置（开发环境）

```yaml
cors:
  allowed-origins:                         # 允许的源
    - http://localhost:3000
    - http://localhost:8080
  allowed-methods:                         # 允许的HTTP方法
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:                         # 允许的请求头
    - "*"
  allow-credentials: true                  # 是否允许携带凭证
  max-age: 3600                           # 预检请求有效期（秒）
```

---

## 数据库服务对接指南

### 概述

数据库服务负责所有数据的持久化存储，包括MySQL数据库的部署、管理和数据查询。本后端服务通过HTTP REST API调用数据库服务。

### 对接接口规范

数据库服务需要提供以下REST API接口，基础路径为：`/api/database`

#### 1. 用户相关接口

##### 1.1 根据邮箱查询用户

```
GET /api/database/user/email/{email}
```

**响应格式**：
```json
{
    "id": 1,
    "email": "user@example.com",
    "username": "test",
    "password": "hashed_password",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
}
```

##### 1.2 根据ID查询用户

```
GET /api/database/user/{id}
```

**响应格式**：同上

##### 1.3 创建用户

```
POST /api/database/user
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "hashed_password",
    "username": "test"
}
```

**响应格式**：返回创建的用户对象

##### 1.4 更新用户信息

```
PUT /api/database/user/{id}
Content-Type: application/json

{
    "username": "new_username",
    "password": "new_hashed_password"
}
```

#### 2. 碑文相关接口

##### 2.1 查询碑文列表（支持分页、排序、搜索）

```
GET /api/database/inscription/list?userId={userId}&page={page}&size={size}&sort={sort}&keyword={keyword}
```

**参数说明**：
- `userId`: 用户ID（必需）
- `page`: 页码，从0开始（必需）
- `size`: 每页大小（必需）
- `sort`: 排序方式，如 `created:desc`（可选）
- `keyword`: 搜索关键词（可选）

**响应格式**：
```json
{
    "list": [
        {
            "id": 1,
            "userId": 1,
            "title": "碑文标题",
            "imageUrl": "/uploads/image.jpg",
            "text": "识别出的文本",
            "correctedText": "校对后的文本",
            "dynasty": "唐代",
            "status": "proofread",
            "createdAt": "2024-01-01T00:00:00",
            "updatedAt": "2024-01-01T00:00:00"
        }
    ],
    "total": 100,
    "page": 0,
    "size": 10,
    "totalPages": 10
}
```

##### 2.2 根据ID查询碑文详情

```
GET /api/database/inscription/{id}
```

**响应格式**：返回完整的碑文对象

##### 2.3 创建碑文记录

```
POST /api/database/inscription
Content-Type: application/json

{
    "userId": 1,
    "title": "碑文标题",
    "imageUrl": "/uploads/image.jpg",
    "text": "识别出的文本",
    "dynasty": "唐代",
    "status": "recognizing"
}
```

**响应格式**：返回创建的碑文对象

##### 2.4 更新碑文

```
PUT /api/database/inscription/{id}
Content-Type: application/json

{
    "title": "新标题",
    "correctedText": "校对后的文本",
    "status": "proofread"
}
```

##### 2.5 删除碑文

```
DELETE /api/database/inscription/{id}
```

##### 2.6 模糊搜索碑文

```
GET /api/database/inscription/search?keyword={keyword}
```

**响应格式**：返回匹配的碑文列表（数组）

#### 3. 知识库相关接口

##### 3.1 查询知识库列表

```
GET /api/database/knowledge/list?page={page}&size={size}&keyword={keyword}&dynasty={dynasty}&category={category}
```

**参数说明**：
- `page`: 页码（必需）
- `size`: 每页大小（必需）
- `keyword`: 搜索关键词（可选）
- `dynasty`: 朝代筛选（可选）
- `category`: 分类筛选（可选）

**响应格式**：同碑文列表格式

##### 3.2 根据ID查询知识库详情

```
GET /api/database/knowledge/{id}
```

##### 3.3 搜索知识库

```
GET /api/database/knowledge/search?keyword={keyword}&dynasty={dynasty}&tags={tags}
```

**响应格式**：返回匹配的知识库列表（数组）

#### 4. 收藏相关接口

##### 4.1 添加收藏

```
POST /api/database/favorite
Content-Type: application/json

{
    "userId": 1,
    "type": "inscription",  // 或 "knowledge"
    "targetId": 1
}
```

##### 4.2 取消收藏

```
DELETE /api/database/favorite?userId={userId}&type={type}&targetId={targetId}
```

##### 4.3 查询收藏列表

```
GET /api/database/favorite/list?userId={userId}&type={type}
```

**响应格式**：返回收藏列表（数组）

### 数据模型建议

#### 用户表（user）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| email | VARCHAR(255) | 邮箱，唯一索引 |
| username | VARCHAR(100) | 用户名 |
| password | VARCHAR(255) | 加密后的密码 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### 碑文表（inscription）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID，外键 |
| title | VARCHAR(200) | 标题 |
| image_url | VARCHAR(500) | 图片URL |
| text | TEXT | 识别出的原始文本 |
| corrected_text | TEXT | 校对后的文本 |
| dynasty | VARCHAR(50) | 朝代 |
| status | VARCHAR(50) | 状态：recognizing, proofread, published |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### 知识库表（knowledge）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(200) | 标题 |
| content | TEXT | 内容 |
| dynasty | VARCHAR(50) | 朝代 |
| category | VARCHAR(50) | 分类 |
| tags | VARCHAR(500) | 标签（JSON或逗号分隔） |
| source_id | BIGINT | 来源碑文ID（可选） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### 收藏表（favorite）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| type | VARCHAR(50) | 类型：inscription 或 knowledge |
| target_id | BIGINT | 目标ID |
| created_at | DATETIME | 创建时间 |

### 对接注意事项

1. **统一响应格式**：建议数据库服务也使用统一的响应格式，便于错误处理
2. **分页参数**：页码从0开始，每页大小建议默认10
3. **错误处理**：当资源不存在时，返回404或空结果，不要抛出异常
4. **性能优化**：列表查询建议使用索引，支持分页和排序
5. **模糊搜索**：使用MySQL的 `LIKE` 或全文索引，支持中文搜索

### 代码对接示例

数据库服务同学可以参考 `DatabaseClient.java` 中的调用方式：

```java
// 查询用户
Map<String, Object> user = databaseClient.getUserByEmail(email);

// 创建碑文
Map<String, Object> inscriptionData = new HashMap<>();
inscriptionData.put("userId", userId);
inscriptionData.put("title", title);
Map<String, Object> inscription = databaseClient.createInscription(inscriptionData);
```

---

## LLM与RAG服务对接指南

### 概述

LLM服务负责调用大语言模型API生成AI阐释和对话，RAG服务负责检索增强生成，从知识库中检索相关背景知识。

### LLM服务对接

#### 1. 生成AI阐释接口

**调用方式**：`LLMClient.generateInterpretation(text, dynasty, context)`

**参数说明**：
- `text`: 碑文文本内容
- `dynasty`: 朝代信息（可选）
- `context`: 上下文信息（可选）

**返回**：生成的阐释文本（String）

**实现要求**：
- 调用大语言模型API（如OpenAI、阿里云等）
- 构建合适的Prompt，包含系统提示词和用户消息
- 处理API响应，提取生成的文本
- 处理超时和异常情况

**代码位置**：`LLMClient.java` 的 `generateInterpretation` 方法

#### 2. AI对话接口

**调用方式**：`LLMClient.chat(question, context)`

**参数说明**：
- `question`: 用户问题
- `context`: 上下文列表（List<String>），用于RAG检索结果

**返回**：AI回答文本（String）

**实现要求**：
- 支持多轮对话（需要维护对话历史）
- 将RAG检索的上下文作为系统消息或用户消息的一部分
- 处理流式响应（如果支持）

**代码位置**：`LLMClient.java` 的 `chat` 方法

#### 3. 配置说明

LLM服务配置在 `application.yml` 中：

```yaml
llm:
  api:
    base-url: https://api.openai.com/v1    # 修改为实际的LLM API地址
    api-key: your-llm-api-key-here         # 修改为实际的API密钥
    model: gpt-4                            # 修改为实际使用的模型
    temperature: 0.7                       # 温度参数（0-1）
    max-tokens: 2000                       # 最大生成token数
    timeout: 30000                         # 超时时间（毫秒）
```

#### 4. 当前实现状态

`LLMClient.java` 中已经实现了基本的调用逻辑，但需要：
- 根据实际使用的LLM服务调整API调用方式
- 处理不同LLM服务的响应格式差异
- 实现错误重试机制
- 支持流式响应（可选）

### RAG服务对接

#### 1. 检索相关背景知识

**调用位置**：`InterpretationService.java` 的 `getRagContext` 方法

**当前状态**：方法已定义，但未实现，返回空列表

**需要实现的功能**：
- 接收查询文本和碑文ID
- 调用RAG服务检索相关文档
- 返回检索到的上下文列表

**建议接口设计**：

```
POST /api/rag/retrieve
Content-Type: application/json

{
    "query": "碑文内容或问题",
    "inscriptionId": 1,        // 可选
    "topK": 5                  // 返回top-k结果
}
```

**响应格式**：
```json
{
    "contexts": [
        "相关背景知识1...",
        "相关背景知识2...",
        ...
    ]
}
```

#### 2. RAG服务配置

```yaml
rag:
  api:
    base-url: http://localhost:8083/api/rag    # 修改为实际的RAG服务地址
    connect-timeout: 5000                      # 连接超时
    read-timeout: 15000                        # 读取超时
    top-k: 5                                   # 默认返回top-k结果
```

#### 3. 实现建议

1. **创建RAG客户端**：参考 `DatabaseClient` 和 `RedisClient`，创建 `RagClient.java`
2. **在InterpretationService中调用**：在 `getRagContext` 方法中调用RAG客户端
3. **错误处理**：RAG服务不可用时，应降级处理，不影响主要功能

**示例代码结构**：

```java
@Service
public class RagClient {
    @Value("${rag.api.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public List<String> retrieve(String query, Long inscriptionId, Integer topK) {
        // 调用RAG API
        // 返回检索到的上下文列表
    }
}
```

### 对接流程

1. **LLM服务对接**：
    - 修改 `application.yml` 中的LLM配置
    - 根据实际LLM服务调整 `LLMClient.java` 中的API调用逻辑
    - 测试生成阐释和对话功能

2. **RAG服务对接**：
    - 创建 `RagClient.java` 客户端
    - 实现 `InterpretationService.getRagContext` 方法
    - 修改 `application.yml` 中的RAG配置
    - 测试RAG检索功能

3. **集成测试**：
    - 测试完整的AI阐释流程（包含RAG检索）
    - 测试AI对话功能
    - 验证错误处理和降级机制

### 注意事项

1. **API密钥安全**：不要将API密钥提交到代码仓库，使用环境变量或配置中心
2. **超时处理**：LLM和RAG服务可能响应较慢，需要设置合理的超时时间
3. **错误处理**：外部服务不可用时，应优雅降级，不影响核心功能
4. **成本控制**：注意LLM API的调用成本，可以添加缓存机制
5. **流式响应**：如果支持，可以实现流式响应提升用户体验

---

## 开发环境搭建

### 前置要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- IDE（推荐IntelliJ IDEA或Eclipse）

### 步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd beishuo
   ```

2. **配置Maven**
    - 确保Maven已正确配置
    - 检查 `pom.xml` 中的依赖

3. **配置应用**
    - 复制 `application.yml` 并根据实际情况修改配置
    - 修改数据库、Redis、LLM、RAG服务的地址和密钥

4. **启动服务**
   ```bash
   mvn spring-boot:run
   ```
   或使用IDE直接运行 `beishuoApplication.java`

5. **验证启动**
    - 访问 `http://localhost:8080/api/auth/info` 验证服务是否启动
    - 查看日志确认无错误

### 开发工具推荐

- **Postman**：用于测试API接口
- **IntelliJ IDEA**：推荐使用的IDE
- **Maven Helper**：Maven依赖管理插件

---

## 常见问题

### 1. 如何添加新的API接口？

1. 在对应的 `Controller` 中添加方法
2. 在对应的 `Service` 中实现业务逻辑
3. 如需调用外部服务，在对应的 `Client` 中添加方法
4. 更新接口文档

### 2. 如何处理文件上传？

参考 `InscriptionService.uploadImage` 方法：
- 使用 `MultipartFile` 接收文件
- 验证文件类型和大小
- 保存到配置的目录
- 返回文件URL

### 3. 如何实现权限控制？

当前使用JWT认证，权限控制逻辑：
- 从Token中提取 `userId`
- 在Service层验证资源所有权
- 无权限时抛出 `BusinessException(ResultCode.FORBIDDEN)`

### 4. 如何添加新的外部服务？

1. 创建新的 `Client` 类（如 `NewServiceClient.java`）
2. 注入 `RestTemplate`
3. 使用 `@Value` 注入配置
4. 实现HTTP调用方法

### 5. 缓存失效策略？

- 数据更新时，清除相关缓存
- 使用模糊搜索删除匹配的缓存key
- 参考 `InscriptionService.clearInscriptionCache` 方法

### 6. 如何处理外部服务不可用？

- Client层捕获异常并记录日志
- 返回 `null` 或空结果，由Service层处理
- 实现降级策略，如使用缓存数据

---

## 联系方式

如有问题，请联系后端负责人或查看项目Wiki。

