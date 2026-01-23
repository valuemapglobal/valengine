package com.value.decision.version.common.util;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.risksmart.common.core.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具封装类
 */
public class StringUtil {

    /**
     * 获取模型版本编号
     * 原始版本编号 versionNum
     * 产品名称 projectName
     * 业务名称 businessName
     * 企业和个人标识 personOrCompany
     */
    public static String getVersionNum(String versionNum,String projectName,String businessName,String personOrCompany){
        String projectNameS = getPinYinHeadChar(projectName);
        String businessNameS = getPinYinHeadChar(businessName);
        String versionNumStr = versionNum(versionNum);
        versionNum = projectNameS + "-" + businessNameS + "-" + personOrCompany + "-" + versionNumStr;
        System.out.println("拼接完成的版本号: "+projectNameS + "-" + businessNameS + "-" + personOrCompany + "-" + versionNumStr);
        return versionNum;
    }

    /**
     * 提取每个汉字的首字母(大写)
     *
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        if (isNull(str)) {
            return "";
        }
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinUtil.getPinyin(word).split(",");
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            }
            else {
                convert += word;
            }
        }

        //去除字符中包含的空格
        convert = string2AllTrim(convert);
        //字符转小写
        convert.toLowerCase();
        return convert.toUpperCase();
    }

    /*
     * 判断字符串是否为空
     */

    public static boolean isNull(Object strData) {
        if (strData == null || String.valueOf(strData).trim().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 去掉字符串包含的所有空格
     *
     * @param value
     * @return
     */
    public static String string2AllTrim(String value) {
        if (isNull(value)) {
            return "";
        }
        return value.trim().replace(" ", "");
    }

    /**
     * version 整个版本号 例如 CCZL-ZR-C-V4.0-20231229-1
     * versionStack V{N}.0；例如V1.0；N为整数进行累计，新增时默认为1.0，此后每次修改累计+1；累计计数无上限；
     * releaseDate YYYYMMDD；例如20231222；
     * num 序号：整数，例如：01；表示该模型的本次版本修改属于在当日的修改次数，每次累计+01；当日累计计数无上限；超过24点后重新从01开始计数；
     * @return
     */
    public static String versionNum(String version){

        String versionNum = null;
        String versionStack = null;
        String releaseDate = null;
        String num = null;
        if (StringUtils.isNotEmpty(version)){
            String[] split = version.split("-");
            versionStack = split[3];
            releaseDate = split[4];
            num = split[5];
        }
        //根据传过来的versionStack进行判断 如果为空就默认为V1.0
        if (StringUtils.isEmpty(versionStack)){
            versionStack = "V1.0";
        }else {
            int startIndex = versionStack.indexOf("V"); // 获取第一个特定字符"H"在字符串中的索引位置
            int endIndex = versionStack.lastIndexOf("."); // 获取最后一个特定字符"!"在字符串中的索引位置
            int i = 0;
            if (startIndex != -1 && endIndex != -1) {
                String result = versionStack.substring(startIndex + 1, endIndex); // 根据起始索引和结束索引进行子字符串截取
                i = Integer.parseInt(result);

                System.out.println("特定字符"+result); // 输出截取到的字符
            } else {
                System.out.println("未找到指定的特定字符！");
            }
            i++;
            versionStack = "V" + i + ".0";
        }

        // 获取当前日期
        Date currentDate = new Date();
        // 创建SimpleDateFormat对象，指定日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        // 格式化日期
        String formattedDate = dateFormat.format(currentDate);
        if (StringUtils.isEmpty(releaseDate)){
            releaseDate = formattedDate;
            num = "1";
        }else {
            if (formattedDate.equals(releaseDate)){
                num = String.valueOf(Integer.parseInt(num) + 1);
            }else {
                releaseDate = formattedDate;
                num = "1";
            }
        }

        versionNum = versionStack + "-" + releaseDate + "-" + num;

        return versionNum;
    }

}
