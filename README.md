# 身份证管理系统 Demo（261012testone）

一个简单的身份证管理 demo：**后端 SpringBoot 3（JDK 17）+ MySQL 8**，前端 Vue3 将在下一阶段加入同一仓库的 `/frontend` 目录。

## 目录结构

```
261012testone/
├── backend/          # SpringBoot 3 + MySQL 8 后端（当前阶段）
│   ├── pom.xml
│   └── src/main/java/com/example/idcard/
└── frontend/         # 预留目录，Vue3 前端（下一阶段填充）
```

## 环境要求

| 组件 | 版本 |
| --- | --- |
| JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.x |

## 初始化数据库

用 root（或任意有建库权限的账号）执行：

```sql
CREATE DATABASE IF NOT EXISTS idcard_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

建表与示例数据由应用启动时自动执行（可重复启动）：

- `backend/src/main/resources/schema.sql` —— 建表，`CREATE TABLE IF NOT EXISTS`
- `backend/src/main/resources/data.sql` —— 8 条示例数据，`INSERT IGNORE`

> 连接串带 `createDatabaseIfNotExist=true`，账号有权限时不手动建库也能启动。

## 启动

```bash
cd backend
mvn spring-boot:run
# 或者
mvn -DskipTests package
java -jar target/idcard-backend-1.0.0.jar
```

服务地址：`http://localhost:8080`，接口统一前缀 `/api`。

数据库连接默认 `localhost:3306 / idcard_demo / root / 空密码`，可用环境变量覆盖：

| 环境变量 | 默认值 |
| --- | --- |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `idcard_demo` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | 空 |

## 数据模型

表 `id_card`：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT PK 自增 | 主键 |
| `name` | VARCHAR(64) NOT NULL | 姓名 |
| `id_number` | VARCHAR(32) NOT NULL UNIQUE | 身份证号（唯一） |
| `gender` | VARCHAR(8) | 性别：男 / 女 |
| `birth_date` | DATE | 出生日期 |
| `address` | VARCHAR(255) | 地址 |
| `created_at` | DATETIME NOT NULL | 创建时间 |
| `updated_at` | DATETIME NOT NULL | 更新时间 |

## 统一返回结构

成功：

```json
{ "code": 0, "message": "success", "data": {} }
```

失败（同时返回对应 HTTP 状态码）：

```json
{ "code": 40400, "message": "身份证记录不存在：99", "data": null }
```

| code | HTTP | 含义 |
| --- | --- | --- |
| 0 | 200 / 201 | 成功 |
| 40000 | 400 | 参数校验失败 / 身份证号格式错误 |
| 40400 | 404 | 记录不存在 |
| 40900 | 409 | 身份证号已存在等冲突 |
| 50000 | 500 | 服务器内部错误 |

## 接口清单

日期格式：请求体 `birthDate` 为 `yyyy-MM-dd`；响应中 `createdAt` / `updatedAt` 为 `yyyy-MM-dd HH:mm:ss`。

### 1. 分页查询（姓名 / 身份证号模糊搜索）

`GET /api/idcards?keyword=张&page=1&size=10`

| 参数 | 必填 | 默认 | 说明 |
| --- | --- | --- | --- |
| `keyword` | 否 | 无 | 姓名或身份证号模糊匹配 |
| `page` | 否 | 1 | 页码，从 1 开始 |
| `size` | 否 | 10 | 每页条数，1~100 |

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "张伟",
        "idNumber": "110101199003070011",
        "gender": "男",
        "birthDate": "1990-03-07",
        "address": "北京市东城区景山前街4号",
        "createdAt": "2026-10-12 08:30:00",
        "updatedAt": "2026-10-12 08:30:00"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
}
```

### 2. 查询详情

`GET /api/idcards/{id}` —— `data` 为单条记录对象；不存在返回 404 / code 40400。

### 3. 新增

`POST /api/idcards`（成功返回 **HTTP 201**）

```json
{
  "name": "张伟",
  "idNumber": "110101199003070011",
  "gender": "男",
  "birthDate": "1990-03-07",
  "address": "北京市东城区景山前街4号"
}
```

- `name`、`idNumber` 必填；`idNumber` 需为 18 位身份证基本格式，且全局唯一。
- `gender`、`birthDate` 可省略：省略时后端按身份证号自动推导（性别取第 17 位奇偶，出生日期取第 7~14 位）。
- `gender` 只能为 `男` 或 `女`。

### 4. 修改

`PUT /api/idcards/{id}`，请求体同新增。

### 5. 删除

`DELETE /api/idcards/{id}` —— 成功时 `data` 为 `null`。

### 6. 统计

`GET /api/idcards/stats`

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "total": 8,
    "maleCount": 4,
    "femaleCount": 4,
    "otherCount": 0,
    "maleRatio": 0.5,
    "femaleRatio": 0.5,
    "ageGroups": [
      { "label": "0-17", "count": 1 },
      { "label": "18-30", "count": 2 },
      { "label": "31-45", "count": 3 },
      { "label": "46-60", "count": 2 },
      { "label": "60岁以上", "count": 0 }
    ]
  }
}
```

> `maleRatio` / `femaleRatio` 为占比（0~1 的小数，计算时保留 4 位小数，响应中不补零）。
> `ageGroups` 中的年龄段基于 `birth_date` 按当前日期实时计算。

## 构建与测试

```bash
cd backend
mvn -q -DskipTests package   # 打包
mvn test                     # 单元测试
```

## 跨域

后端已允许 `/api/**` 跨域（`allowedOriginPatterns("*")`），前端开发服务器（Vite 默认 `http://localhost:5173`）可直接调用。
