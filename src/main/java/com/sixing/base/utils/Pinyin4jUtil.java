package com.sixing.base.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Pinyin4jUtil {
	
	/**
	 * 汉字转换为汉语拼音首字母全部大写
	 * @param chines
	 * @return
	 */
	public static String  getConverterToFirstSpellUp(String chines) {
		return converterToFirstSpell(chines,HanyuPinyinCaseType.UPPERCASE);
		
	}
	/**
	 * 汉字转换为汉语拼音首字母全部小写
	 * @param chines
	 * @return
	 */
	public static String  getConverterToFirstSpellLo(String chines) {
		return converterToFirstSpell(chines,HanyuPinyinCaseType.LOWERCASE);
		
	}
	/**
	 * 汉字转换为汉语全拼，英文字符不变，特殊字符丢失  全部大写
	 * @param chines
	 * @return
	 */
	public static String  getConverterToSpellUp(String chines){
		return converterToSpell(chines, HanyuPinyinCaseType.UPPERCASE);	
	}
	/**
	 * 汉字转换为汉语全拼，英文字符不变，特殊字符丢失  全部小写
	 * @param chines
	 * @return
	 */
	public static String  getConverterToSpellLo(String chines){
		return converterToSpell(chines, HanyuPinyinCaseType.LOWERCASE);	
	}
	
	/** 
	 * 汉字转换为汉语拼音首字母，英文字符不变，特殊字符丢失 支持多音字，生成方式如（长沙市长:cssc,zssz,zssc,cssz） 
	 *  
	 * @param chines 
	 *            汉字 
	 * @return 拼音 
	 */  
	public static String converterToFirstSpell(String chines,HanyuPinyinCaseType caseType) {  
		StringBuffer pinyinName = new StringBuffer();  
		char[] nameChar = chines.toCharArray();  
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
		defaultFormat.setCaseType(caseType);  
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		for (int i = 0; i < nameChar.length; i++) {  
			if (nameChar[i] > 128) {  
				try {  
					// 取得当前汉字的所有全拼  
					String[] strs = PinyinHelper.toHanyuPinyinStringArray(  
							nameChar[i], defaultFormat);  
					if (strs != null) {  
						for (int j = 0; j < strs.length; j++) {  
							// 取首字母  
							pinyinName.append(strs[j].charAt(0));  
							if (j != strs.length - 1) {  
								pinyinName.append(",");  
							}  
						}  
					}  
					// else {  
					// pinyinName.append(nameChar[i]);  
					// }  
				} catch (BadHanyuPinyinOutputFormatCombination e) {  
					e.printStackTrace();  
				}  
			} else {  
				pinyinName.append(nameChar[i]);  
			}  
			pinyinName.append(" ");  
		}  
		// return pinyinName.toString();  
		return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));  
	}  

	/** 
	 * 汉字转换为汉语全拼，英文字符不变，特殊字符丢失 
	 * 支持多音字，生成方式如（重当参:zhongdangcen,zhongdangcan,chongdangcen 
	 * ,chongdangshen,zhongdangshen,chongdangcan） 
	 *  
	 * @param chines 
	 *            汉字 
	 * @return 拼音 
	 */  
	public static String converterToSpell(String chines,HanyuPinyinCaseType caseType) {  
		StringBuffer pinyinName = new StringBuffer();  
		char[] nameChar = chines.toCharArray();  
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
		defaultFormat.setCaseType(caseType);  
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
		for (int i = 0; i < nameChar.length; i++) {  
			if (nameChar[i] > 128) {  
				try {  
					// 取得当前汉字的所有全拼  
					String[] strs = PinyinHelper.toHanyuPinyinStringArray(  
							nameChar[i], defaultFormat);  
					if (strs != null) {  
						for (int j = 0; j < strs.length; j++) {  
							pinyinName.append(strs[j]);  
							if (j != strs.length - 1) {  
								pinyinName.append(",");  
							}  
						}  
					}  
				} catch (BadHanyuPinyinOutputFormatCombination e) {  
					e.printStackTrace();  
				}  
			} else {  
				pinyinName.append(nameChar[i]);  
			}  
			pinyinName.append(" ");  
		}  
		// return pinyinName.toString();  
		return parseTheChineseByObject(discountTheChinese(pinyinName.toString()));  
	}  

	/** 
	 * 去除多音字重复数据 
	 *  
	 * @param theStr 
	 * @return 
	 */  
	private static List<Map<String, Integer>> discountTheChinese(String theStr) {  
		// 去除重复拼音后的拼音列表  
		List<Map<String, Integer>> mapList = new ArrayList<Map<String, Integer>>();  
		// 用于处理每个字的多音字，去掉重复  
		Map<String, Integer> onlyOne = null;  
		String[] firsts = theStr.split(" ");  
		// 读出每个汉字的拼音  
		for (String str : firsts) {  
			onlyOne = new Hashtable<String, Integer>();  
			String[] china = str.split(",");  
			// 多音字处理  
			for (String s : china) {  
				Integer count = onlyOne.get(s);  
				if (count == null) {  
					onlyOne.put(s, new Integer(1));  
				} else {  
					onlyOne.remove(s);  
					count++;  
					onlyOne.put(s, count);  
				}  
			}  
			mapList.add(onlyOne);  
		}  
		return mapList;  
	}  

	/** 
	 * 解析并组合拼音，对象合并方案
	 *  
	 * @return 
	 */  
	private static String parseTheChineseByObject(  
			List<Map<String, Integer>> list) {  
		Map<String, Integer> first = null; // 用于统计每一次,集合组合数据  
		// 遍历每一组集合  
		for (int i = 0; i < list.size(); i++) {  
			// 每一组集合与上一次组合的Map  
			Map<String, Integer> temp = new Hashtable<String, Integer>();  
			// 第一次循环，first为空  
			if (first != null) {  
				// 取出上次组合与此次集合的字符，并保存  
				for (String s : first.keySet()) {  
					for (String s1 : list.get(i).keySet()) {  
						String str = s + s1;  
						temp.put(str, 1);  
					}  
				}  
				// 清理上一次组合数据  
				if (temp != null && temp.size() > 0) {  
					first.clear();  
				}  
			} else {  
				for (String s : list.get(i).keySet()) {  
					String str = s;  
					temp.put(str, 1);  
				}  
			}  
			// 保存组合数据以便下次循环使用  
			if (temp != null && temp.size() > 0) {  
				first = temp;  
			}  
		}  
		String returnStr = "";  
		if (first != null) {  
			// 遍历取出组合字符串  
			for (String str : first.keySet()) {  
				returnStr += (str + ",");  
			}  
		}  
		if (returnStr.length() > 0) {  
			returnStr = returnStr.substring(0, returnStr.length() - 1);  
		}  
		return returnStr;  
	}
//=======分割线===========以下第二种方式===========================================
	/** 
	 * 将文字转为汉语拼音
	 * @param chineselanguage 要转成拼音的中文
	 */
	public static String toHanyuPinyin(String ChineseLanguage){
		char[] cl_chars = ChineseLanguage.trim().toCharArray();
		String hanyupinyin = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);// 输出拼音全部小写
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
		defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V) ;
		try {
			for (int i=0; i<cl_chars.length; i++){
				if (String.valueOf(cl_chars[i]).matches("[\u4e00-\u9fa5]+")){// 如果字符是中文,则将中文转为汉语拼音
					hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0];
				} else {// 如果字符不是中文,则不转换
					hanyupinyin += cl_chars[i];
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			System.out.println("字符不能转成汉语拼音");
		}
		return hanyupinyin;
	}

	public static String getFirstLettersUp(String ChineseLanguage){
		return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.UPPERCASE);
	}

	public static String getFirstLettersLo(String ChineseLanguage){
		return getFirstLetters(ChineseLanguage ,HanyuPinyinCaseType.LOWERCASE);
	}

	public static String getFirstLetters(String ChineseLanguage,HanyuPinyinCaseType caseType) {
		char[] cl_chars = ChineseLanguage.trim().toCharArray();
		String hanyupinyin = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(caseType);// 输出拼音全部大写
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
		try {
			for (int i = 0; i < cl_chars.length; i++) {
				String str = String.valueOf(cl_chars[i]);
				if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
					hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(cl_chars[i], defaultFormat)[0].substring(0, 1);
				} else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
					hanyupinyin += cl_chars[i];
				} else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母
					hanyupinyin += cl_chars[i];
				} else {// 否则不转换
					hanyupinyin += cl_chars[i];//如果是标点符号的话，带着
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			System.out.println("字符不能转成汉语拼音");
		}
		return hanyupinyin;
	}

	public static String getPinyinString(String ChineseLanguage){
		char[] cl_chars = ChineseLanguage.trim().toCharArray();
		String hanyupinyin = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部大写
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
		try {
			for (int i = 0; i < cl_chars.length; i++) {
				String str = String.valueOf(cl_chars[i]);
				if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
					hanyupinyin += PinyinHelper.toHanyuPinyinStringArray(
							cl_chars[i], defaultFormat)[0];
				} else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
					hanyupinyin += cl_chars[i];
				} else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

					hanyupinyin += cl_chars[i];
				} else {// 否则不转换
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			System.out.println("字符不能转成汉语拼音");
		}
		return hanyupinyin;
	}
	/**
	 * 取第一个汉字的第一个字符
	 * @Title: getFirstLetter 
	 * @Description: TODO 
	 * @return String   
	 * @throws
	 */
	public static String getFirstLetter(String ChineseLanguage){
		char[] cl_chars = ChineseLanguage.trim().toCharArray();
		String hanyupinyin = "";
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部大写
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
		try {
			String str = String.valueOf(cl_chars[0]);
			if (str.matches("[\u4e00-\u9fa5]+")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
				hanyupinyin = PinyinHelper.toHanyuPinyinStringArray(
						cl_chars[0], defaultFormat)[0].substring(0, 1);;
			} else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
				hanyupinyin += cl_chars[0];
			} else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母

				hanyupinyin += cl_chars[0];
			} else {// 否则不转换

			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			System.out.println("字符不能转成汉语拼音");
		}
		return hanyupinyin;
	}

}
