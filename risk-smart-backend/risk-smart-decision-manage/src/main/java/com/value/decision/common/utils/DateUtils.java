package com.value.decision.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"
    };

    /**
     * 获取当前Date型日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期时间字符串，格式：yyyyMMddHHmmss
     */
    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    /**
     * 获取当前日期时间字符串，指定格式
     */
    public static String dateTimeNow(String format) {
        return parseDateToStr(format, new Date());
    }

    /**
     * 格式化日期为字符串，格式：yyyy-MM-dd
     */
    public static String dateTime(Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    /**
     * 格式化日期为字符串
     */
    public static String parseDateToStr(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 日期路径，格式：yyyy/MM/dd
     */
    public static String datePath() {
        return DateFormatUtils.format(new Date(), "yyyy/MM/dd");
    }

    /**
     * 字符串转日期，支持多种格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }
}
