package com.value.decision.common.utils;

import java.util.Scanner;

/**
 * 输入的金额转换成中文大写，16位数字精确到小数点后2位数
 *
 */
public class NumberToCNFormat {
    public static String getCNNumber(int number) {
        //数字对应的汉字
        String[] num = {"零","一","二","三","四","五","六","七","八","九"};
        //单位
        String[] unit = {"","十","百","千","万","十","百","千","亿","十","百","千","万亿"};
        //将输入数字转换为字符串
        String result = String.valueOf(number);
        //将该字符串分割为数组存放
        char[] ch = result.toCharArray();
        //结果 字符串
        String str = "";
        int length = ch.length;
        for (int i = 0; i < length; i++) {
            int c = (int)ch[i]-48;
            if(c != 0) {
                str += num[c]+unit[length-i-1];
            } else {
                str += num[c];
            }
        }

        while(!str.isEmpty() && str.substring(str.length()-1, str.length()).equals("零")){
            str = str.substring(0, str.length()-1);
        }
        if(str.equals("一十")){
            str = "十";
        }

        return str;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("请输入要进行转换的数字......");
            String str = scanner.next();
            if("-1".equals(str))
                return;
            System.out.println(NumberToCNFormat.getCNNumber(Integer.parseInt(str)));
        }

    }
}
