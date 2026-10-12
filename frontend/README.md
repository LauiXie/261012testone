# 身份证管理系统前端（frontend）

Vue3 + Vite + Element Plus + axios + vue-router，对接后端 `/api` 接口。

## 环境要求

| 组件 | 版本 |
| --- | --- |
| Node.js | 18+（推荐 20 / 22） |
| npm | 9+ |

## 安装依赖

```bash
cd frontend
npm install
```

## 本地开发

先启动后端（默认 `http://localhost:8080`），再启动前端开发服务器：

```bash
npm run dev
```

访问 `http://localhost:5173`。开发环境通过 Vite proxy 把 `/api` 转发到 `http://localhost:8080`
（见 `vite.config.js`），因此不存在跨域问题，前端代码里请求路径统一写 `/api/...`。

## 构建与预览

```bash
npm run build     # 产物输出到 frontend/dist
npm run preview   # 本地预览构建产物
```

## 页面说明

| 路由 | 页面 | 功能 |
| --- | --- | --- |
| `/idcards` | 身份证管理 | 分页列表、按姓名/身份证号搜索、新增（弹窗表单 + 校验）、编辑、删除（二次确认） |
| `/stats` | 数据统计 | 总数、男女数量与占比、年龄段分布 |

## 目录结构

```
src/
├── api/                 # axios 实例与接口封装（request.js / idcard.js）
├── components/          # IdCardFormDialog.vue 新增 / 编辑弹窗
├── router/              # 路由配置
├── styles/              # 全局样式
├── utils/               # 身份证号校验与解析
├── views/               # IdCardList.vue / IdCardStats.vue
├── App.vue
└── main.js
```

## 对接的后端接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/idcards?keyword=&page=&size=` | 分页 + 关键字（姓名 / 身份证号模糊匹配） |
| GET | `/api/idcards/{id}` | 详情 |
| POST | `/api/idcards` | 新增 |
| PUT | `/api/idcards/{id}` | 修改 |
| DELETE | `/api/idcards/{id}` | 删除 |
| GET | `/api/idcards/stats` | 统计（总数、男女占比、年龄段分布） |

统一响应结构 `{ code, message, data }` 在 `src/api/request.js` 的响应拦截器里统一解包：
`code === 0` 时直接把 `data` 返回给调用方，否则弹出错误提示并 reject，页面只需处理成功分支。

## 表单校验

- 姓名必填，长度不超过 64；地址长度不超过 255。
- 身份证号必填，按 18 位格式 + GB 11643 校验位校验（`src/utils/idcard.js`）。
- 身份证号合法时自动带出性别与出生日期（第 17 位奇偶判断性别，第 7~14 位解析出生日期），也可手动修改。

## 交互说明

- 列表分页每页 10 / 20 / 50 / 100 条，搜索与重置都会回到第 1 页。
- 删除前二次确认（Element Plus MessageBox）；删除当前页最后一条时自动回退上一页。
- 列表与统计页都有加载态，统计页接口失败时展示错误提示与"重新加载"按钮。
