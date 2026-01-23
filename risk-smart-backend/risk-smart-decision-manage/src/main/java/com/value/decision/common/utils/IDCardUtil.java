package com.value.decision.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * 身份证工具类
 */
public class IDCardUtil {

    private static final int FIFTEEN_ID_CARD = 15;
    private static final int EIGHTEEN_ID_CARD = 18;

    /**
     * 根据身份证号获取年龄
     */
    public static Integer getAge(String idCard) {
        if (StringUtils.isBlank(idCard) || !isValidLength(idCard)) {
            return 0;
        }

        try {
            LocalDate now = LocalDate.now();
            int birthYear;
            int birthMonth;

            if (idCard.length() == FIFTEEN_ID_CARD) {
                birthYear = 1900 + Integer.parseInt(idCard.substring(6, 8));
                birthMonth = Integer.parseInt(idCard.substring(8, 10));
            } else {
                birthYear = Integer.parseInt(idCard.substring(6, 10));
                birthMonth = Integer.parseInt(idCard.substring(10, 12));
            }

            int age = now.getYear() - birthYear;
            if (now.getMonthValue() < birthMonth) {
                age--;
            }
            return Math.max(age, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 身份证号脱敏：保留前6位和后4位
     */
    public static String idNumberDesensitization(String idNumber) {
        if (StringUtils.isBlank(idNumber) || idNumber.length() <= 10) {
            return idNumber;
        }
        return idNumber.substring(0, 6) + "********" + idNumber.substring(14);
    }

    /**
     * 姓名脱敏：保留第一个字
     */
    public static String userNameDesensitization(String userName) {
        if (StringUtils.isBlank(userName) || userName.length() <= 1) {
            return userName;
        }
        return userName.charAt(0) + "*".repeat(userName.length() - 1);
    }

    /**
     * 手机号脱敏：隐藏中间4位
     */
    public static String phoneDesensitization(String phone) {
        if (StringUtils.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private static boolean isValidLength(String id) {
        int len = id.length();
        return len == FIFTEEN_ID_CARD || len == EIGHTEEN_ID_CARD;
    }
}
