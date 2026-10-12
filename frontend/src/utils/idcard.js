// 18 位身份证号加权因子与校验码映射（GB 11643-1999）
const WEIGHTS = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
const CHECK_CODES = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

const PATTERN = /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/

/**
 * 校验 18 位身份证号：格式 + 校验位。
 */
export function isValidIdNumber(value) {
  const id = String(value == null ? '' : value).trim().toUpperCase()
  if (!PATTERN.test(id)) {
    return false
  }
  let sum = 0
  for (let i = 0; i < 17; i += 1) {
    sum += Number(id.charAt(i)) * WEIGHTS[i]
  }
  return CHECK_CODES[sum % 11] === id.charAt(17)
}

/**
 * 从身份证号推导性别与出生日期，非法时返回空值。
 */
export function parseIdNumber(value) {
  const id = String(value == null ? '' : value).trim().toUpperCase()
  if (!isValidIdNumber(id)) {
    return { gender: '', birthDate: '' }
  }
  const sequence = Number(id.charAt(16))
  const gender = sequence % 2 === 1 ? '男' : '女'
  const birthDate = id.slice(6, 10) + '-' + id.slice(10, 12) + '-' + id.slice(12, 14)
  return { gender: gender, birthDate: birthDate }
}

/**
 * 把 0~1 的占比格式化成百分比文本，例如 0.5 -> 50.0%。
 */
export function toPercentText(ratio) {
  const value = Number(ratio)
  if (!Number.isFinite(value)) {
    return '0.0%'
  }
  return (value * 100).toFixed(1) + '%'
}
