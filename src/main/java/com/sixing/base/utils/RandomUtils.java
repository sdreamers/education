package com.sixing.base.utils;

import org.apache.commons.lang.RandomStringUtils;

/**
 * 随机数工具类
 */
public class RandomUtils {

    /**
     * 生成指定长度的数字字母组合随机数
     * 
     * @param length 长度
     * @return 随机数
     */
    public static String random(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * 生成指定长度的纯数字随机数
     * 
     * @param length 长度
     * @return 纯数字随机数
     */
    public static String randomNumberic(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * 生成指定长度的非数字随机数
     * 
     * @param length 长度
     * @return 非数字随机数
     */
    public static String randomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }


}