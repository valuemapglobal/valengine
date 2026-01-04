package com.value.decision.common.utils;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SM3Util {
    private static final String HMAC_SHA256 = "HmacSHA256";

    private SM3Util() {
    }

    public static String encrypt(String src, String key) throws Exception {
        return bytesToHex(getEncryptByKey(src, key)).toUpperCase();
    }

    public static byte[] getEncryptByKey(String src, String key) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        mac.init(secretKey);
        return mac.doFinal(src.getBytes(StandardCharsets.UTF_8));
    }

    public static String buildSource(Map<String, ?> paramsMap, String key) throws Exception {
        List<String> paramNames = new ArrayList<>(paramsMap.keySet());
        Collections.sort(paramNames);
        StringBuilder signData = new StringBuilder(key);
        for (String name : paramNames) {
            Object value = paramsMap.get(name);
            if (value != null) {
                signData.append(name).append(value);
            }
        }
        signData.append(key);
        return signData.toString();
    }

    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
