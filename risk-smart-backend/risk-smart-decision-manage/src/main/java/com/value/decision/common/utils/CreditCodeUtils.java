package com.value.decision.common.utils;


import java.util.regex.Pattern;

/**
 * @author Austin
 * Create by 2025/6/20 14:51
 * 统一社会信用代码工具类
 */
public class CreditCodeUtils {

    /**
     * 统一社会信用代码正则表达式
     * 18位：字母和数字组成
     */
    private static final String CREDIT_CODE_PATTERN = "^[A-Z0-9]{18}$";

    /**
     * 校验统一社会信用代码是否合法
     * @param creditCode 统一社会信用代码
     * @return 是否合法
     */
    public static boolean isValid(String creditCode) {
        if (StringUtils.isEmpty(creditCode)) {
            return false;
        }

        // 长度必须为18位
        if (creditCode.length() != 18) {
            return false;
        }

        // 必须符合正则表达式
        return Pattern.matches(CREDIT_CODE_PATTERN, creditCode);
    }

    /**
     * 格式化统一社会信用代码（统一转为大写）
     * @param creditCode 统一社会信用代码
     * @return 格式化后的统一社会信用代码
     */
    public static String format(String creditCode) {
        if (StringUtils.isEmpty(creditCode)) {
            return creditCode;
        }
        return creditCode.toUpperCase();
    }
}
