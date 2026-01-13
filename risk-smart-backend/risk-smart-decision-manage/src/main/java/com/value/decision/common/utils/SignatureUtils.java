package com.value.decision.common.utils;


import com.risksmart.common.core.utils.StringUtils;

import java.security.MessageDigest;
import java.util.*;

/**
 * @className: SignatureUtils
 * @description: 签名和验证签名工具类
 * @date: 2020/8/21
 * @author: cakin
 */
public class SignatureUtils {
    /**
     * sign 签名 （参数名按ASCII码从小到大排序（字典序）+key+MD5+转大写签名）
     * @param map
     * @return
     */
    public static String encodeSign(SortedMap<String,String> map, String key){
        if(StringUtils.isEmpty(key)){
            throw new RuntimeException("签名key不能为空");
        }
        Set<Map.Entry<String, String>> entries = map.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        List<String> values =new ArrayList();

        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            String k = String.valueOf(entry.getKey());
            String v = String.valueOf(entry.getValue());
            if (StringUtils.isNotEmpty(v) && entry.getValue() !=null && !"sign".equals(k) && !"key".equals(k)) {
                values.add(k + "=" + v);
            }
        }
        values.add("key="+ key);
        String sign = StringUtils.join(values, "&");
        return encodeByMD5(sign).toUpperCase();
    }
    /**
     * 通过MD5加密
     *
     * @param algorithmStr
     * @return String
     */
    public static String encodeByMD5(String algorithmStr) {
        if (algorithmStr==null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(algorithmStr.getBytes("utf-8"));
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String getFormattedText(byte[] digest){
        StringBuffer buffer = new StringBuffer();
        //把每一个byte，做一个与运算，0xff
        for (byte b :
                digest) {
            int number=b & 0xff;//加盐
            String str = Integer.toHexString(number);
            if (str.length() == 1){
                buffer.append("0");
            }
            buffer.append(str);
        }
        //标准的md5加密后的结果
        return buffer.toString();
    }

    public static Boolean verifySign(TreeMap<String, String> stringStringTreeMap, String sign, String key){
        String s = SignatureUtils.encodeSign(stringStringTreeMap, key);
        return (s.equals(sign));
    }
}
