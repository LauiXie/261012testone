import request from './request'

/** 分页查询：keyword 同时匹配姓名与身份证号 */
export function fetchIdCards(params) {
  return request.get('/idcards', { params: params })
}

/** 查询单条详情 */
export function fetchIdCard(id) {
  return request.get('/idcards/' + id)
}

/** 新增 */
export function createIdCard(payload) {
  return request.post('/idcards', payload)
}

/** 修改 */
export function updateIdCard(id, payload) {
  return request.put('/idcards/' + id, payload)
}

/** 删除 */
export function deleteIdCard(id) {
  return request.delete('/idcards/' + id)
}

/** 统计：总数、男女数量与占比、年龄段分布 */
export function fetchStats() {
  return request.get('/idcards/stats')
}
