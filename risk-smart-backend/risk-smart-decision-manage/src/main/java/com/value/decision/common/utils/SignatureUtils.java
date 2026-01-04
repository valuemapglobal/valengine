package com.value.decision.common.utils;


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
        //System.out.println(s);
        return (s.equals(sign));
    }


    public static void main(String[] args) {

        TreeMap<String, String> stringStringTreeMap = new TreeMap<>();
        stringStringTreeMap.put("taskCode", "VMe6dee9352c4948389ef5f356164f034");
        stringStringTreeMap.put("companyName", "估图（上海）科技有限公司");
        stringStringTreeMap.put("loginType", "1");
        stringStringTreeMap.put("loginPwd", "JW4QEiUCPCTZ6jz1DkQtDV+3u3pUQzu4h83xz1x/YGJhjz0d0Tg5H3RXRQxBDjlIrAvDijZ6ds/IfceWi1QvAR6DAfwcKgaGk8xfFLXyOi/u6zlZLNRgjqqPB5SQ3ZlWwFWIurRo1p3FsTWn/zgwyQ+vrSdL4leFlXzZk25/Jhc=");
        stringStringTreeMap.put("password", "UpS0rb0fRxHDN0F2SPgQ6Y+B8StPxlkoAnfOprZuWbGgeeCXxsyx+KHIOgem2/KR6t7WEGqe2LE8bhMyF4qn1DfCSTBo+qBtssA2uOFyt+OycdqLvsDC8ixUbACiPQvYusMqrZGhlW3qlumc5zUHPhQmANHZG0D6PgSOAezVBXQ=");
        stringStringTreeMap.put("userName", "ayGunECrWKgyBBXyl1xrxvZgYXOzEyeUOr1y0bb7DxoYRg2sGFbgCP0+H5P0QS3kVCA+txaWXmILwjSNUv+yXZdfx/HamxGB1sOxIdBokjfEbsytIUE+NTsAeR/7n7yUm+04mZzt0SSLrI9eLKWxyIbTs2sBqQYC0KI2FGnRg3Y=");

        String s = SignatureUtils.encodeSign(stringStringTreeMap, "5rlv7OHaCbXMBHIW");
        System.out.println(s);
        System.out.println(s.equals("322C77067A392E9A8960CDEBCE147B5E"));

        /*TreeMap<String, String> stringStringTreeMap = new TreeMap<>();
        stringStringTreeMap.put("taskCode", "VMe6dee9352c4948389ef5f356164f034");
        stringStringTreeMap.put("companyName", "估图（上海）科技有限公司");
        stringStringTreeMap.put("loginPwd", "JW4QEiUCPCTZ6jz1DkQtDV+3u3pUQzu4h83xz1x/YGJhjz0d0Tg5H3RXRQxBDjlIrAvDijZ6ds/IfceWi1QvAR6DAfwcKgaGk8xfFLXyOi/u6zlZLNRgjqqPB5SQ3ZlWwFWIurRo1p3FsTWn/zgwyQ+vrSdL4leFlXzZk25/Jhc=");
        stringStringTreeMap.put("phone", "W/OcSPkFzOT/EVuj/kiJ17sUrtKiIlNsCid9hwyUW2z6DcF7uB8ZWdinwkhyyYOoKNjUf8SAVjiYZDSRXebutIbqR3jJa01OggJmfO10FzsNhif5Ptm/oi+7vqUgkS4Un+tob4cvEZTsyq3llQwhlCSLs1sSH/3W5uUr3XiH3CQ=");
        stringStringTreeMap.put("userName", "ayGunECrWKgyBBXyl1xrxvZgYXOzEyeUOr1y0bb7DxoYRg2sGFbgCP0+H5P0QS3kVCA+txaWXmILwjSNUv+yXZdfx/HamxGB1sOxIdBokjfEbsytIUE+NTsAeR/7n7yUm+04mZzt0SSLrI9eLKWxyIbTs2sBqQYC0KI2FGnRg3Y=");

        String s = SignatureUtils.encodeSign(stringStringTreeMap, "5rlv7OHaCbXMBHIW");
        System.out.println(s);
        System.out.println(s.equals("322C77067A392E9A8960CDEBCE147B5E"));*/
    }
}
