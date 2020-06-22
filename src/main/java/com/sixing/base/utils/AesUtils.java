package com.sixing.base.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES对称加密工具类
 */
public class AesUtils {

    /** 默认的秘钥，长度16位 */
    public static final String DEFAULT_AES_KEY = "947222abcb6827ea";

    /**
     * 加密目标字节数组
     * 
     * @param targetBytes 目标字节数组
     * @param aesKey 用于加密/解密的秘钥
     * @return 加密后的字节数组
     */
    public static byte[] encrypt(byte[] targetBytes, String aesKey) {
        byte[] result = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec((StringUtils.isNotBlank(aesKey) ? aesKey : DEFAULT_AES_KEY).getBytes("utf-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            result = cipher.doFinal(targetBytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 加密目标字符串
     * 
     * @param targetBytes 目标字符串
     * @param aesKey 用于加密/解密的秘钥
     * @return 加密后的字符串
     */
    public static String encryptToString(String targetString, String aesKey) {
        String result = null;
        byte[] targetBytes = null;
        try {
            targetBytes = targetString.getBytes("utf-8");
            targetBytes = encrypt(targetBytes, aesKey);
            result = Base64Utils.encode(targetBytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 解密目标字节数组
     * 
     * @param targetBytes 目标字节数组
     * @param aesKey 用于加密/解密的秘钥
     * @return 解密后的字节数组
     */
    public static byte[] decrypt(byte[] bytes, String aesKey) {
        byte[] result = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec((StringUtils.isNotBlank(aesKey) ? aesKey : DEFAULT_AES_KEY).getBytes("utf-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            result = cipher.doFinal(bytes);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 解密目标字符串
     * 
     * @param targetBytes 目标字符串
     * @param aesKey 用于加密/解密的秘钥
     * @return 解密后的字符串
     */
    public static String decryptToString(String targetString, String aesKey) {
        String result = null;
        try {
            result = new String(decrypt(Base64Utils.decode(targetString), aesKey));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }



}