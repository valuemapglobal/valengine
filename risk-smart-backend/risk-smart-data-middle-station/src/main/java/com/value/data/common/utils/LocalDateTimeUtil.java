package com.value.data.common.utils;

import cn.hutool.core.date.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * @author Vida
 * @date 2025年04月22日 11:42
 * @description
 */
public class LocalDateTimeUtil {
    public LocalDateTime parse(String date) {
        final DateTime parse = cn.hutool.core.date.DateUtil.parse(date);
        return LocalDateTime.ofInstant(parse.toInstant(), ZoneId.systemDefault());
    }

    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /***
     * 计算两个日期之间相差多少年
     * @param start
     * @param end
     * @return
     */
    public long betweenYear(LocalDateTime start, LocalDateTime end) {
        return  ChronoUnit.YEARS.between(start, end);
    }

    public long betweenMonth(LocalDateTime start, LocalDateTime end) {
        return  ChronoUnit.MONTHS.between(start, end);
    }

    public long betweenDay(LocalDateTime start, LocalDateTime end) {
        return  ChronoUnit.DAYS.between(start, end);
    }

}
