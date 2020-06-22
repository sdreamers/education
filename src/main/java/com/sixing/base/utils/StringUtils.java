package com.sixing.base.utils;

import org.apache.commons.lang.ArrayUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * String工具 主要对 StringUtils 的一些方法进行重写,达到更方便的使用
 * 
 * @author zhou-baicheng
 *
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {
    
    /**
     * 判断目标字符串的值是否等于true
     * 
     * @param val
     */
    public static boolean isTrue(String val) {
        val = StringUtils.trimToEmpty(val);
        return StringUtils.isNotBlank(val) && StringUtils.equals(val, "true") ? true : false;
    }

	/**
	 * 一次性判断多个或单个对象为空。
	 * 
	 * @param objects
	 * @author zhou-baicheng
	 * @return 只要有一个元素为Blank，则返回true
	 */
	public static boolean isBlank(Object... objects) {
		Boolean result = false;
		for (Object object : objects) {
			if (null == object || "".equals(object.toString().trim()) || "null".equals(object.toString().trim())) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 把数组转换成set
	 * 
	 * @param array
	 * @return
	 */
	public static Set<?> array2Set(Object[] array) {
		Set<Object> set = new TreeSet<Object>();
		for (Object id : array) {
			if (null != id) {
				set.add(id);
			}
		}
		return set;
	}

	/**
	 * <p>
	 * Checks if the String contains only unicode digits. A decimal point is not
	 * a unicode digit and returns false.
	 * </p>
	 *
	 * <p>
	 * <code>null</code> will return <code>false</code>. An empty String
	 * (length()=0) will return <code>true</code>.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.isNumeric(null)   = false
	 * StringUtils.isNumeric("")     = false
	 * StringUtils.isNumeric("  ")   = false
	 * StringUtils.isNumeric("123")  = true
	 * StringUtils.isNumeric("12 3") = false
	 * StringUtils.isNumeric("ab2c") = false
	 * StringUtils.isNumeric("12-3") = false
	 * StringUtils.isNumeric("12.3") = true
	 * </pre>
	 *
	 * @param strs
	 *            the String to check, may be null
	 * @return <code>true</code> if only contains digits, and is non-null
	 */
	public static boolean isNumeric(String strs) {
	    // 新增去除左右空格
	    strs = StringUtils.trimToEmpty(strs);
		if (StringUtils.isBlank(strs)) {
			return false;
		}
		if (strs.indexOf(".") == 0) {
			return false;
		}
		if (strs.lastIndexOf(".") == strs.length() - 1) {
			return false;
		}
		String[] strArr = strs.split("\\.");
		if (!CollectionUtils.isNotEmpty(strArr)) {
			return false;
		}
		if (strArr.length > 2) {
			return false;
		}
		for (String str : strArr) {
			if (StringUtils.isBlank(str)) {
				return false;
			}
			int sz = str.length();
			for (int i = 0; i < sz; i++) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 实数换算成百分比格式，小数点最多保留两位
	 * 
	 * @param value 实数
	 * @return 百分比格式的字符串
	 */
	public static String toPercentage(String value) {
	    if (StringUtils.isBlank(value)) {
	        return value;
	    }
	    try {
	        NumberFormat percent = NumberFormat.getPercentInstance();
	        percent.setMaximumFractionDigits(2);
	        value = percent.format(Double.parseDouble(value));
	    }
	    catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return value;
	}

	/**
	 * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
	 *
	 * @param parameterMap 需要转化的键值对集合
	 * @return 字符串
	 */
	public static String convertStringParameter(Map<String, String> parameterMap) {
		StringBuilder parameterBuilder = new StringBuilder();
		if (parameterMap == null) {
			return null;
		}

		Iterator<Map.Entry<String, String>> iterator = parameterMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			String value = entry.getValue();
			if (isEmpty(value)) {
				continue;
			}
			if ("sign".equals(value)) {
				continue;
			}
			parameterBuilder.append(entry.getKey()).append("=").append(entry.getValue());
			if (iterator.hasNext()) {
				parameterBuilder.append("&");
			}
		}
		return parameterBuilder.toString();
	}

	public static boolean equalsWithAny(String target, String[] searchStrings) {
		if (!isEmpty(target) && !ArrayUtils.isEmpty(searchStrings)) {
			for(int i = 0; i < searchStrings.length; ++i) {
				String searchString = searchStrings[i];
				if (target.equals(searchString)) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public static boolean nullOrZero(BigDecimal target) {
		return target == null || BigDecimal.ZERO.compareTo(target) == 0;
	}
}
