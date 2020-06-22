package com.sixing.base.utils.codegenerator;

import java.util.Calendar;

public class CodeGeneratorUtil {

    public static String getCurrentDate() {
        int year, month, day;

        String retStr;

        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);

        retStr = year + "年" + month + "月" + day + "日";
        return retStr;
    }


    /**
     * 实现：subject_balance --> subjectBalance
     *
     * @return 经过驼峰修饰的字符串
     */
    public static String getCamelString(String s) {
        while (s.indexOf("_") > 0) {
            int index = s.indexOf("_");
            s = s.substring(0, index) + s.substring(index + 1, index + 2).toUpperCase() + s.substring(index + 2);
        }
        return s;
    }

    /**
     * 目标字符串的首字母改成大写
     *
     * @param str
     * @return 首字母是大写的字符串
     */
    public static String firstCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return getCamelString(new String(ch));
    }

    /**
     * 目标字符串的首字母改成小写
     *
     * @param str
     * @return 首字母是小写的字符串
     */
    public static String initLower(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    public static String sqlTypeToMybatisJdbcType(String sqlType) {
        if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("tinytext") || sqlType.equalsIgnoreCase("mediumtext") || sqlType.equalsIgnoreCase("longtext")) {
            return "VARCHAR";
        } else if (sqlType.equalsIgnoreCase("char")) {
            return "CHAR";
        } else if (sqlType.equalsIgnoreCase("bit")) {
            return "BIT";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "INTEGER";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "BIGINT";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "TINYINT";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "SMALLINT";
        } else if (sqlType.equalsIgnoreCase("decimal")) {
            return "DECIMAL";
        } else if (sqlType.equalsIgnoreCase("double")) {
            return "DOUBLE";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "FLOAT";
        } else if (sqlType.equalsIgnoreCase("numeric")) {
            return "NUMERIC";
        } else if (sqlType.equalsIgnoreCase("datetime")) {
            return "TIMESTAMP";
        } else if (sqlType.equalsIgnoreCase("date")) {
            return "DATE";
        } else if (sqlType.equalsIgnoreCase("time")) {
            return "TIME";
        } else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "TIMESTAMP";
        } else if (sqlType.equalsIgnoreCase("real")) {
            return "REAL";
        }
        return null;
    }


    public static String sqlTypeToJavaType(String sqlType) {
        if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("tinytext") || sqlType.equalsIgnoreCase("mediumtext") || sqlType.equalsIgnoreCase("longtext")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("char")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("double")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        } else if (sqlType.equalsIgnoreCase("numeric")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("date")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("time")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "Timestamp";
        } else if (sqlType.equalsIgnoreCase("real")) {
            return "Double";
        }
        return null;
    }

    public static String getModuleName(String tableName) {
        String moduleName = tableName;
        if (tableName.indexOf("t_") == 0) {
            // 解析掉t_的前缀
            moduleName = moduleName.replaceFirst("t_", "");
        }
        if (tableName.indexOf("u_") == 0) {
            // 解析掉u_的前缀
            moduleName = moduleName.replace("u_", "");
        }
        // 把输入字符串的首字母改成大写
        return CodeGeneratorUtil.firstCap(moduleName);
    }

}
