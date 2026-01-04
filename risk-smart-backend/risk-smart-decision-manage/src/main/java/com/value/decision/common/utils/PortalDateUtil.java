package com.value.decision.common.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取当前前12个月，4个季度工具类
 *
 * @author hxp
 * @date 2021/3/1
 **/
public class PortalDateUtil {
    //获取当前12个月
    public static List<String> getLatestMonth(LocalDate date, int num){
        List<String> monthList = new ArrayList<>();
        for(int i = 0;i <= num-1; i++){
            LocalDate localDate = date.minusMonths(i);
            String month = localDate.toString().substring(0,7);
            monthList.add(month);
        }
        return monthList;
    }

    //获取当前4个季度
    public static List<String> getLatest4Quater(LocalDate date,int num){
        List<String> quaterList = new ArrayList<>();
        for(int i = 0;i <= num-1; i++){
            LocalDate localDate = date.minusMonths(3*i);
            String year = localDate.toString().substring(0,4);
            String monthstr = localDate.toString().substring(5, 7);
            int quaterNum = ((Integer.valueOf(monthstr)+2)/3);
            String quater = year + "-q" + quaterNum;
            quaterList.add(quater);
        }
        return quaterList;
    }




    public static void main(String[] args) {
        System.out.println(getLatestMonth(LocalDate.now(),12));
        System.out.println(getLatest4Quater(LocalDate.now().minusMonths(5L),4));
    }
}
