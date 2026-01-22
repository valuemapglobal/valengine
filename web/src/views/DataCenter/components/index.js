/**
 * @author chris
 * @date 2025/5/9 13:34
 */
/**
 * 校验值是否重复
 * @param rule
 * @param value
 * @param callback
 * @param validatorInner 提示文案
 */
export const checkValueRepeat = (rule, value, callback, validatorInner) => {
	if (value) {
		return callback(new Error(validatorInner));
	} else {
		callback();
	}
};
