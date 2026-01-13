package com.risksmart.common.core.utils.sign;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * AES加密工具类
 *
 * @author tanghc, Elijah
 */
@Slf4j
public class AESUtil {

    private static final String UTF8 = "UTF-8";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM_CIPHER = "AES/ECB/PKCS5Padding";
    private static final int LIMIT_LEN = 16;

    /**
     * 指定随机字符串（密码）生成密钥
     *
     * @param randomKey 加解密的密码
     * @return 密钥字节数组
     * @throws Exception 生成失败
     */
    public static byte[] getSecretKey(String randomKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        if (StringUtils.isBlank(randomKey)) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(randomKey.getBytes());
        keyGenerator.init(128, random);
        SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 生成一个SecretKey（简单密码方式）
     *
     * @param password 长度必须小于等于16
     * @return 返回SecretKey
     */
    public static SecretKey getSecretKeySimple(String password) {
        byte[] passwordData = password.getBytes();
        if (passwordData.length > LIMIT_LEN) {
            throw new IllegalArgumentException("password 长度必须小于等于16");
        }
        byte[] keyData = new byte[16];
        System.arraycopy(passwordData, 0, keyData, 0, passwordData.length);
        return new SecretKeySpec(keyData, KEY_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return 加密数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  二进制密钥
     * @return 加密数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return 加密数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return 加密数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 加密（简单密码方式）
     *
     * @param data     待加密数据
     * @param password 密码（长度<=16）
     * @return 加密数据
     * @throws Exception 加密失败
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKeySimple(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  二进制密钥
     * @return 解密数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return 解密数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return 解密数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return 解密数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * 解密（简单密码方式）
     *
     * @param data     待解密数据
     * @param password 密码（长度<=16）
     * @return 解密数据
     * @throws Exception 解密失败
     */
    public static byte[] decrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKeySimple(password);
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    /**
     * 转换密钥
     *
     * @param secretKey 二进制密钥
     * @return 密钥
     */
    public static Key toKey(byte[] secretKey) {
        return new SecretKeySpec(secretKey, KEY_ALGORITHM);
    }

    /**
     * 文本加密（Base64编码）
     *
     * @param content  明文
     * @param password 密码（长度<=16）
     * @return base64编码的密文
     * @throws Exception 加密失败
     */
    public static String encryptToBase64String(String content, String password) throws Exception {
        byte[] data = content.getBytes(UTF8);
        byte[] result = encrypt(data, password);
        return Base64.encodeBase64String(result);
    }

    /**
     * 文本解密（Base64解码）
     *
     * @param base64String base64编码的密文
     * @param password     密码（长度<=16）
     * @return 明文
     * @throws Exception 解密失败
     */
    public static String decryptFromBase64String(String base64String, String password) throws Exception {
        byte[] data = Base64.decodeBase64(base64String);
        byte[] contentData = decrypt(data, password);
        return new String(contentData, UTF8);
    }

    /**
     * 文本加密（Hex编码）
     *
     * @param content  明文
     * @param password 密码（长度<=16）
     * @return 16进制编码的密文
     * @throws Exception 加密失败
     */
    public static String encryptToHex(String content, String password) throws Exception {
        byte[] data = content.getBytes(UTF8);
        byte[] result = encrypt(data, password);
        return Hex.encodeHexString(result);
    }

    /**
     * 文本解密（Hex解码）
     *
     * @param hex      16进制编码的密文
     * @param password 密码（长度<=16）
     * @return 明文
     * @throws Exception 解密失败
     */
    public static String decryptFromHex(String hex, String password) throws Exception {
        byte[] data = Hex.decodeHex(hex);
        byte[] contentData = decrypt(data, password);
        return new String(contentData, UTF8);
    }

    /**
     * 使用指定密钥加密并返回Hex字符串
     *
     * @param plaintext 明文
     * @param password  密钥
     * @return Hex编码的密文
     */
    public static String aesEncrypt(String plaintext, String password) {
        try {
            byte[] secretKey = getSecretKey(password);
            Key key = toKey(secretKey);
            byte[] encryptData = encrypt(plaintext.getBytes(), key);
            return Hex.encodeHexString(encryptData);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * 使用指定密钥解密Hex字符串
     *
     * @param ciphertext Hex编码的密文
     * @param password   密钥
     * @return 明文
     */
    public static String aesDecryptHex(String ciphertext, String password) {
        try {
            byte[] secretKey = getSecretKey(password);
            Key key = toKey(secretKey);
            byte[] decryptData = decrypt(Hex.decodeHex(ciphertext.toCharArray()), key);
            return new String(decryptData);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败", e);
        }
    }
}
