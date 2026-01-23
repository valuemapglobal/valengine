package com.value.data.tool;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * HMAC-SHA256 签名工具类
 * 用于 API 接口的签名生成和验证
 *
 * @author RiskSmart Team
 * @since 2025-12-25
 */
public class SignatureUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String CHARSET = "UTF-8";

    /**
     * 生成 HMAC-SHA256 签名
     *
     * @param data 待签名的数据
     * @param secret 密钥
     * @return 签名的十六进制字符串（大写）
     * @throws NoSuchAlgorithmException 算法不存在
     * @throws InvalidKeyException 密钥无效
     */
    public static String generateSignature(String data, String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSHA256 = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash).toUpperCase();
    }

    /**
     * 构建签名数据源
     * 将参数按字典序排列，拼接成字符串
     * 格式: secret + key1value1 + key2value2 + ... + secret
     *
     * @param paramsMap 参数映射
     * @param secret 密钥
     * @return 签名数据源字符串
     */
    public static String buildSignData(Map<String, ?> paramsMap, String secret) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return secret + secret;
        }

        // 获取所有参数键并排序
        List<String> paramNames = new ArrayList<>(paramsMap.keySet());
        Collections.sort(paramNames);

        // 拼接参数
        StringBuilder signData = new StringBuilder(secret);
        for (String paramName : paramNames) {
            Object value = paramsMap.get(paramName);
            if (value != null) {
                signData.append(paramName).append(value);
            }
        }
        signData.append(secret);

        return signData.toString();
    }

    /**
     * 验证签名
     *
     * @param data 待验证的数据
     * @param secret 密钥
     * @param signature 客户端提供的签名
     * @return 签名是否有效
     */
    public static boolean verifySignature(String data, String secret, String signature) {
        try {
            String computedSignature = generateSignature(data, secret);
            return computedSignature.equalsIgnoreCase(signature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证时间戳是否在有效期内
     *
     * @param currentTime 当前时间戳（秒）
     * @param requestTime 请求时间戳（秒）
     * @param expirationSeconds 过期时间（秒）
     * @return 时间戳是否有效
     */
    public static boolean isTimestampValid(long currentTime, long requestTime, long expirationSeconds) {
        return Math.abs(currentTime - requestTime) <= expirationSeconds;
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
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

    /**
     * 测试示例
     */
    public static void main(String[] args) throws Exception {
        // 示例参数
        Map<String, String> params = new LinkedHashMap<>();
        params.put("appKey", "testAppKey123");
        params.put("timestamp", "2025-01-01 10:00:00");
        params.put("orderId", "ORDER123456789");
        params.put("manageNo", "MANAGE001");

        String secret = "mySecretKey123456";

        // 生成签名
        String signData = buildSignData(params, secret);
        System.out.println("签名数据源: " + signData);

        String signature = generateSignature(signData, secret);
        System.out.println("生成的签名: " + signature);

        // 验证签名
        boolean isValid = verifySignature(signData, secret, signature);
        System.out.println("签名验证结果: " + isValid);

        // 测试时间戳验证
        long currentTime = System.currentTimeMillis() / 1000;
        long requestTime = currentTime - 100; // 100秒前
        boolean timestampValid = isTimestampValid(currentTime, requestTime, 300);
        System.out.println("时间戳验证结果 (100秒前, 有效期300秒): " + timestampValid);
    }
}