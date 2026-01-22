/**
 * @author chris
 * @date 2025/4/24 15:02
 */
/**
 * @description:常用运算符
 * @type {[{label: string, value: string},{label: string, value: string},{label: string, value: string},{label: string, value: string}]}
 */
export const CommonOperators = [
  {
    label: '加',
    value: '+',
    paraphrase:
      '"对两个或多个数值进行相加。\n' +
      '用法: =数字1 + 数字2\n' +
      '示例: = 2 + 8 返回结果为 10"',
  },
  {
    label: '减',
    value: '-',
    paraphrase:
      '"对两个数值进行相减。\n' +
      '用法: =数字1 - 数字2\n' +
      '示例: = 8 - 2 返回结果为 6"',
  },
  {
    label: '乘',
    value: '*',
    paraphrase:
      '"对两个或多个数值进行相乘。\n' +
      '用法: =数字1 * 数字2 \n' +
      '示例: = 20 * 30 返回结果为 600"',
  },
  {
    label: '除',
    value: '/',
    paraphrase:
      '"对两个数值进行相除。\n' +
      '用法: =数字1 / 数字2 \n' +
      '示例: = 100 / 20 返回结果为 5"',
  },
]
/**
 * 统计函数
 * @type {[{label: string, value: string},{label: string, value: string},{label: string, value: string},{label: string, value: string},{label: string, value: string},null,null,null,null]}
 */
export const Statistical = [
  {
    label: '平均值',
    value: 'AVG',
    paraphrase:
      '"AVERAGE 函数可以获取一组数据的算数平均值。\n' +
      '用法: AVERAGE(数字1, 数字2, ...)\n' +
      '示例: =AVERAGE(10, 20, 30) 返回结果为 20"',
  },
  {
    label: '最大值',
    value: 'MAX',
    paraphrase:
      '"MAX 函数返回一组数值中的最大值。\n' +
      '用法: MAX(数字1, 数字2, ...)\n' +
      '示例: =MAX(10, 20, 30) 返回结果为 30"',
  },
  {
    label: '最小值',
    value: 'MIN',
    paraphrase:
      '"MIN 函数返回一组数值中的最小值。\n' +
      '用法: MIN(数字1, 数字2, ...)\n' +
      '示例: =MIN(10, 20, 30) 返回结果为 10"',
  },
  {
    label: '中位数',
    value: 'MEDIAN',
    paraphrase:
      '"MEDIAN 函数返回一组数值的中位数（即将数值按大小顺序排列后位于中间的数）。\n' +
      '用法: MEDIAN(数字1, 数字2, ...)\n' +
      '示例: =MEDIAN(10, 20, 30) 返回结果为 20"',
  },
  {
    label: '总体标准偏差',
    value: 'STDEV.P',
    paraphrase:
      '"STDEV.P 函数基于整个总体计算标准偏差（忽略逻辑值和文本）。\n' +
      '用法: STDEV.P(数字1, 数字2, ...)\n' +
      '示例: =STDEV.P(10, 20, 30) 返回该组数据的标准偏差"',
  },
  {
    label: '样本标准偏差',
    value: 'STDEV.S',
    paraphrase:
      '"STDEV.S 函数基于样本估算标准偏差（忽略逻辑值和文本）。\n' +
      '用法: STDEV.S(数字1, 数字2, ...)\n' +
      '示例: =STDEV.S(10, 20, 30) 返回该组数据作为样本时的标准偏差估计值"',
  },
  {
    label: '总体方差',
    value: 'VAR.P',
    paraphrase:
      '"VAR.P 函数计算基于整个总体的方差（忽略逻辑值和文本）。\n' +
      '用法: VAR.P(数字1, 数字2, ...)\n' +
      '示例: =VAR.P(10, 20, 30) 返回该组数据的总体方差"',
  },
  {
    label: '样本方差',
    value: 'VAR.S',
    paraphrase:
      '"VAR.S 函数基于样本估算方差（忽略逻辑值和文本）。\n' +
      '用法: VAR.S(数字1, 数字2, ...)\n' +
      '示例: =VAR.S(10, 20, 30) 返回该组数据作为样本时的方差估计值"',
  },
  {
    label: '求和',
    value: 'SUM',
    paraphrase:
      '"SUM 函数用于计算一组数值的总和。\n' +
      '用法: SUM(数字1, 数字2, ...)\n' +
      '示例: =SUM(10, 20, 30) 返回结果为 60"',
  },
]
/**
 * 数学函数
 * @type {[{label: string, value: string},{label: string, value: string},{label: string, value: string}]}
 */
export const Mathematical = [
  {
    label: '绝对值',
    value: 'ABS',
    paraphrase:
      '"ABS 函数返回一个数的绝对值（即去掉负号后的值）。\n' +
      '用法: ABS(数字)\n' +
      '示例: =ABS(-10) 返回结果为 10"',
  },
  {
    label: '四舍五入',
    value: 'ROUND',
    paraphrase:
      '"ROUND 函数按指定的小数位数四舍五入一个数值。\n' +
      '用法: ROUND(数字, 小数位数)\n' +
      '示例: =ROUND(3.14159, 2) 返回结果为 3.14"',
  },
  {
    label: '幂运算',
    value: 'POWER',
    paraphrase:
      '"POWER 函数计算一个数的指定次幂（指数）。\n' +
      '用法: POWER(底数, 指数)\n' +
      '示例: =POWER(2, 3) 返回结果为 8（即 2 的 3 次方）"',
  },
  {
    label: '开平方',
    value: 'SQRT',
    paraphrase:
      '"SQRT 函数计算一个数的平方根。\n' +
      '用法: SQRT(数字)\n' +
      '示例: =SQRT(16) 返回结果为 4"',
  },
]
/**
 * 逻辑函数
 * @type {[{label: string, value: string},{label: string, value: string},{label: string, value: string}]}
 */
export const LogicList = [
  {
    label: '条件判断',
    value: 'IF',
    paraphrase:
      '"IF 函数用于执行条件判断，并根据判断结果返回不同的值。\n' +
      '用法: IF(条件, 值如果为真, 值如果为假)\n' +
      '示例: =IF(A1 > 10, ""大于10"", ""小于等于10"")"',
  },
  {
    label: '逻辑与',
    value: 'AND',
    paraphrase:
      '"AND 函数用于测试多个条件，所有条件都为真时返回 TRUE，否则返回 FALSE。\n' +
      '用法: AND(条件1, 条件2, ...)\n' +
      '示例: =AND(A1 > 10, B1 < 5)"',
  },
  {
    label: '逻辑或',
    value: 'OR',
    paraphrase:
      '"OR 函数用于测试多个条件，只要有一个条件为真就返回 TRUE，所有条件都为假时返回 FALSE。\n' +
      '用法: OR(条件1, 条件2, ...)\n' +
      '示例: =OR(A1 > 10, B1 < 5)"',
  },
]
export const POUNDList = [
  ...CommonOperators,
  ...Statistical,
  ...Mathematical,
  ...LogicList,
].map((item) => {
  return {
    name: item.label,
    value: item.value,
  }
})
