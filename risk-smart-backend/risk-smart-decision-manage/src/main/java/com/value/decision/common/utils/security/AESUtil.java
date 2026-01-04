package com.value.decision.common.utils.security;

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
 * @Author: Elijah
 * @DateTime: 2022/7/26 9:09
 */
public class AESUtil {

    private static final String UTF8 = "UTF-8";
    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    //加密密码
    private static final String PASSWORD = "ID_CARD_AES_ENCRYPTION";

    private static final String ALGORITHM_CIPHER = "AES/ECB/PKCS5Padding";

    private static final int LIMIT_LEN = 16;

    /**
     * 指定随机字符串（密码）生成密钥
     *
     * @param randomKey 加解密的密码
     * @throws Exception
     */
    public static byte[] getSecretKey(String randomKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM); //秘钥生成器，指定秘钥算法
        //指定密码
        if (StringUtils.isBlank(randomKey)) {
            throw new IllegalArgumentException();
        }
        if (!StringUtils.equals(randomKey,PASSWORD)) {
            throw new IllegalArgumentException();
        }
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(randomKey.getBytes());
        keyGenerator.init(128, random);

        SecretKey secretKey = keyGenerator.generateKey();   //生成密钥
        return secretKey.getEncoded();
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  二进制密钥
     * @return byte[]   加密数据
     * @throws Exception
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
     * @return byte[]   加密数据
     * @throws Exception
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
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);    //获取算法
        cipher.init(Cipher.ENCRYPT_MODE, key);                  //设置加密模式，并指定秘钥
        return cipher.doFinal(data);                            //加密数据
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  二进制密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[]   解密数据
     * @throws Exception
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
     * @return byte[]   解密数据
     * @throws Exception
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
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);     //获取算法
        cipher.init(Cipher.DECRYPT_MODE, key);                   //设置解密模式，并指定秘钥
        return cipher.doFinal(data);                             //解密数据
    }

    /**
     * 转换密钥
     *
     * @param secretKey 二进制密钥
     * @return 密钥
     */
    public static Key toKey(byte[] secretKey) {
        return new SecretKeySpec(secretKey, KEY_ALGORITHM);   //生成密钥
    }

    /**
     * 明文
     * aes加密并返回加密字符串
     * @param plaintext 明文
     * @return
     */
    public static String aesEncrypt(String plaintext){
        byte[] secretKey = new byte[0];
        try {
            secretKey = getSecretKey(PASSWORD);
            Key key = toKey(secretKey);
            byte[] encryptData = encrypt(plaintext.getBytes(), key);
            String encryptDataHex = Hex.encodeHexString(encryptData);   //把密文转为16进制
            return encryptDataHex;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * aes解密并返回明文
     * @param ciphertext 密文
     * @return
     * @throws Exception
     */
    public static String aesDecodeHex(String ciphertext){
        byte[] secretKey = new byte[0];
        try {
            secretKey = getSecretKey(PASSWORD);
            Key key = toKey(secretKey);
            byte[] decryptData = decrypt(Hex.decodeHex(ciphertext.toCharArray()), key);
            return new String(decryptData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * 生成一个SecretKey
     * @param password 长度必须小于等于16
     * @return 返回SecretKey
     */
    public static SecretKey getSecretKeyNew(String password) {
        byte[] passwordData = password.getBytes();
        if(passwordData.length > LIMIT_LEN) {
            throw new IllegalArgumentException("password 长度必须小于等于16");
        }
        // 创建一个空的16位字节数组（默认值为0）,16byte（128bit）
        byte[] keyData = new byte[16];
        System.arraycopy(passwordData, 0, keyData, 0, passwordData.length);

        return new SecretKeySpec(keyData, KEY_ALGORITHM);
    }

    /**
     * 解密
     * @param data 待解密数据
     * @param password 密码
     * @return 返回解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password) throws Exception {
        SecretKey secretKey = getSecretKeyNew(password);
        // Cipher完成加密或解密工作类
        Cipher cipher = Cipher.getInstance(ALGORITHM_CIPHER);
        // 对Cipher初始化，解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        // 解密data
        return cipher.doFinal(data);
    }

    /**
     * 文本解密
     * @param base64String 待解密文本
     * @param password 密码
     * @return 返回明文
     * @throws Exception
     */
    public static String decryptFromBase64String(String base64String, String password) throws Exception {
        byte[] data = Base64.decodeBase64(base64String);
        byte[] contentData = decrypt(data, password);
        return new String(contentData, UTF8);
    }


    public static void main(String[] args) throws Exception {

        String data = "610125199910080810";
        String encryptDataHex = aesEncrypt(data);
        System.out.println("加密 : " + encryptDataHex);

        String decryptData = aesDecodeHex(encryptDataHex);
        System.out.println("解密 : " + decryptData);
    }
}
