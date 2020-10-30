package com.sixing.base.utils;

import org.springframework.cglib.beans.BeanCopier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanCopier工具类
 *
 * @author sixing
 */
public class BeanCopierUtil {

    /**
     * BeanCopier的缓存
     */
    private static final ConcurrentHashMap<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();
 
    /**
     * BeanCopier的copy
     * @param source 源文件的
     * @param target 目标文件
     */
    public static void copy(Object source, Object target) {
        if (source == null) {
            return;
        }
        String key = genKey(source.getClass(), target.getClass());
        BeanCopier beanCopier;
        if (BEAN_COPIER_CACHE.containsKey(key)) {
            beanCopier = BEAN_COPIER_CACHE.get(key);
        } else {
            beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            BEAN_COPIER_CACHE.put(key, beanCopier);
        }
        beanCopier.copy(source, target, null);
    }

    /**
     * BeanCopier的copy
     * @param source 源文件的
     * @param targetClass 目标class
     */
    public static <T> T copy(Object source, Class<T> targetClass) {
        T target;
       try {
           target = targetClass.newInstance();
           if (source == null) {
               return target;
           }
           copy(source, target);
           return target;
       } catch (Exception e) {
            return null;
       }
    }

    /**
     * 生成key
     * @param srcClazz 源文件的class
     * @param tgtClazz 目标文件的class
     * @return string
     */
    private static String genKey(Class<?> srcClazz, Class<?> tgtClazz) {
        return srcClazz.getName() + tgtClazz.getName();
    }
}