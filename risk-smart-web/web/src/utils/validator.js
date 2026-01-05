/*
 * 公共正则判断
 * */
export const validatorPass = (rule, value, callback, name) => {
  if (value < 0) {
    callback('请输入正数')
  } else if (Number(value) === 0) {
    callback(`${name}必须大于0`)
  } else {
    callback()
  }
}

/*
 * 正则区间
 * 输入值不在start和end之间则相应提示
 * */
export const sectionValidator = (
  rule,
  value,
  callback,
  name,
  startValue,
  endValue,
  tip
) => {
  if (value && isNaN(value)) {
    callback(`${name}必须是数字`)
  }
  if (
    (startValue || startValue === 0 || startValue === '0') &&
    (endValue || endValue === 0 || endValue === '0')
  ) {
    if (value < startValue || value > endValue) {
      callback(tip)
    }
  }
  callback()
}

/**
 * 区间值对比——终
 * @param rule
 * @param value
 * @param callback
 * @param contrastValue  需要对比的值
 * @param tips 提示文案
 * @param name
 * @param isNumber 是否校验正数、大于0
 */
export const endValidatorPass = (
  rule,
  value,
  callback,
  contrastValue,
  tips,
  name = '',
  isNumber = false
) => {
  if (!contrastValue) callback()
  if (!isNumber && !name) {
    if (Number(value) < Number(contrastValue)) {
      callback(tips)
    } else {
      callback()
    }
  } else {
    if (Number(value) < 0) {
      callback('请输入正数')
    } else if (Number(value) === 0) {
      callback(`${name}必须大于0`)
    } else if (Number(value) < Number(contrastValue)) {
      callback(tips)
    } else {
      callback()
    }
  }
}

/**
 * 区间值对比——起
 * @param rule
 * @param value
 * @param callback
 * @param contrastValue  需要对比的值
 * @param tips 提示文案
 * @param name
 * @param isNumber 是否校验正数、大于0
 */
export const startValidatorPass = (
  rule,
  value,
  callback,
  contrastValue,
  tips,
  name = '',
  isNumber = false
) => {
  console.log(contrastValue, 'contrastValue')
  if (!contrastValue) callback()
  if (!isNumber && !name) {
    if (Number(value) > Number(contrastValue) && Number(contrastValue) >= 0) {
      callback(tips)
    } else {
      callback()
    }
  } else {
    if (Number(value) < 0) {
      callback('请输入正数')
    } else if (Number(value) === 0) {
      callback(`${name}必须大于0`)
    } else if (Number(value) > Number(contrastValue)) {
      callback(tips)
    } else {
      callback()
    }
  }
}
