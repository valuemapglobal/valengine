package com.value.decision.common.utils;

//import org.quartz.CronExpression;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateToCronUtil {
    /***
     *  功能描述：日期转换cron表达式
     * @param date
     * @param dateFormat : e.g:yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDateByPattern(Date date, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }
    /***
     * convert Date to cron ,eg.  "0 07 10 15 1 ? 2016"
     * @param date  : 时间点
     * @return
     */
    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    public static boolean isVaildByExpression(String cron){
//        Date date;
//        CronExpression exp;
//        try {
//            exp = new CronExpression(cron);
//            date = exp.getNextValidTimeAfter(new Date());
//            if(date!=null){
//                return true;
//            }else {
//                return false;
//            }
//        } catch(ParseException e){
//            e.printStackTrace();
//        }
        return false;
    }
}
