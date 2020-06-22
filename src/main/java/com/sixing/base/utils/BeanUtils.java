package com.sixing.base.utils;

import com.sixing.base.utils.exception.ServiceException;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.beans.FatalBeanException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaBean工具类
 * 
 * @author HeZijiang@Gmail.com
 * @version 1.0 2012-10-10
 * @版权说明 任何人可以得到这份源代码，但需经过本作者的书面授权才可以进行无限制的使用、复制、修改、合并、出版、发行、发放从属证书、或者出售该源代码。
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 复制一份对象的副本，并清除副本中属性类型是字符串的值的左右空格
     * <pre>
     * 案例：
     * Base source = new Base();
     * Base target = new Base();
     * source.setName("  hello world   ");
     * Z3Beans.copyAndTrimProperties(source, target);
     * target.getName() == "hello world";
     * </pre>
     * 
     * @param source 源对象
     * @param target 副本对象
     * @throws Exception 
     */
    public static void copyAndTrimProperties(Object source, Object target) {
        if (source != null) {
            Class<?> actualEditable = target.getClass();
            PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
            for (PropertyDescriptor targetPd : targetPds) {
                if (targetPd.getWriteMethod() != null) {
                    PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
                    if (sourcePd != null && sourcePd.getReadMethod() != null) {
                        try {
                            Method readMethod = sourcePd.getReadMethod();
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            Method writeMethod = targetPd.getWriteMethod();
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            if (readMethod.getReturnType().toString().equals("class java.lang.String")) {
                                if (value != null) {
                                    writeMethod.invoke(target, value.toString().trim());
                                }
                                else {
                                    writeMethod.invoke(target, value);
                                }
                            }
                            else if (readMethod.getReturnType().toString().equals("class java.lang.Boolean")) {
                                if (value != null) {
                                    writeMethod.invoke(target, value);
                                }
                            }
                            else {
                                writeMethod.invoke(target, value);
                            }
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException("Could not copy properties from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static <T> Object invoke(T t, String methodNamed) {
        Method method = null;
        Object value = null;
        try {
            method = t.getClass().getMethod(methodNamed, new Class[] {});
            value = method.invoke(t, new Object[] {});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static <T> Object invoke(T t, String methodNamed, Object[] paramValues) {
        Method method = null;
        Object value = null;
        try {
            Class<?>[] parameterTypes = new Class[paramValues.length];
            int i = 0;
            int length = parameterTypes.length;
            for (; i < length; i++) {
                parameterTypes[i] = Object.class;
            }
            method = t.getClass().getMethod(methodNamed, parameterTypes);
            value = method.invoke(t, paramValues);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 值对象转换成持久对象
     *
     * @param vo 值对象
     * @param po 持久对象
     * @return 持久对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parsePo(Object vo, Object po) {
        BeanUtils.copyAndTrimProperties(vo, po);
        return (T) po;
    }

    /**
     * 持久对象转换成值对象
     * 
     * @param po 持久对象
     * @param vo 值对象
     * @return 值对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseVo(Object po, Object vo) {
        if (po != null) {
            BeanUtils.copyAndTrimProperties(po, vo);
            return (T) vo;
        }
        else {
            return null;
        }
    }

    public static Map toStringMap(Object object) throws ServiceException {
        Map<String, String> params = new HashMap<>();
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] propertyDescriptors = propertyUtilsBean.getPropertyDescriptors(object);
            for(PropertyDescriptor descriptor : propertyDescriptors){
                String name = descriptor.getName();
                if (StringUtils.equals("class", name)){
                    continue;
                }
                Object value = propertyUtilsBean.getNestedProperty(object, name);
                if (value != null){
                    params.put(name, value.toString());
                }
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }


    public static void main(String[] args) {

    }
}