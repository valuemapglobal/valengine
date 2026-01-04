package com.value.data.common.utils;

import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class EncryptTool {
    public EncryptTool() {
    }

    public static String createEncryptBody(String name, String appKey, String secret, Object param, String orderNo) {
        try {
            String data = JSON.toJSONString(param);
            data = URLEncoder.encode(data, "utf-8");
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("name", name);
            paramMap.put("app_key", appKey);
            paramMap.put("data", data);
            paramMap.put("timestamp", getTime());
            paramMap.put("version", "");
            paramMap.put("secret", secret);
            paramMap.put("order_no", orderNo);
            String sign = buildSign(paramMap, secret);
            paramMap.put("sign", sign);
            String encryptDTO = JSON.toJSONString(paramMap);
            return encryptDTO;
        } catch (Exception var9) {
            throw new RuntimeException(var9);
        }
    }

    public static String buildSign(Map<String, ?> paramsMap, String secret) throws IOException {
        Set<String> keySet = paramsMap.keySet();
        List<String> paramNames = new ArrayList(keySet);
        Collections.sort(paramNames);
        StringBuilder paramNameValue = new StringBuilder();
        Iterator var5 = paramNames.iterator();

        while(var5.hasNext()) {
            String paramName = (String)var5.next();
            paramNameValue.append(paramName).append(paramsMap.get(paramName));
        }

        String source = secret + paramNameValue.toString() + secret;
        return md5(source);
    }

    public static String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            return byte2hex(buff);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();

        for(int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 255);
            if (hex.length() == 1) {
                sign.append("0");
            }

            sign.append(hex.toUpperCase());
        }

        return sign.toString();
    }

    public static String getTime() {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }
}
