import axios from 'axios'
import { ElMessage } from 'element-plus'

// 统一 axios 实例：开发环境通过 Vite proxy 把 /api 转发到 http://localhost:8080
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

/**
 * 统一处理后端返回结构 { code, message, data }：
 * - code === 0 时把 data 直接交给调用方
 * - 其他 code 弹出错误提示并 reject
 */
request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) {
        return body.data
      }
      const message = body.message || '请求失败'
      ElMessage.error(message)
      return Promise.reject(new Error(message))
    }
    return body
  },
  (error) => {
    const data = error && error.response && error.response.data
    let message = '网络异常，请稍后重试'
    if (data && data.message) {
      message = data.message
    } else if (error && error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    } else if (error && error.message) {
      message = error.message
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  }
)

export default request
