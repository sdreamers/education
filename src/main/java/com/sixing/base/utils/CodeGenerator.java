package com.sixing.base.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

/**
 * 代码生成者
 * 
 * 描述：根据数据表生成基础代码
 */
public class CodeGenerator {

    private Connection con    = null;
    private String     driver = "com.mysql.jdbc.Driver";
    private String     url    = "jdbc:mysql://119.23.142.208:3306/dongpinyun_dev?characterEncoding=utf-8&autoReconnect=true&failOverReadOnly=false";
    private String     usr    = "com/dongpinyun";
    private String     passwd = "dongpinyun208ceshi@hk2018";

    // 数据表
    private String     dbTable;
    // 字段的名称数组
    private String[]   colNames;
    // 字段的类型数组
    private String[]   colTypes;
    // 字段的长度数组
    private int[]      colSizes;

    // 字段对应的Java属性的数组
    private String[]   attrs;
    // 字段对应的Java方法的数组
    private String[]   attrGetMethods;
    // 字段的类型对应的myBatisJdbcType的数组
    private String[]   myBatisJdbcTypes;

    // 表对应的模块对象类名
    private String     moduleName;

    // 指定Domain层存储的路径
    private String     domainPath;
    // 指定DAO层存储的路径
    private String     daoPath;
    // 指定MAPPER文件的包路径
    private String     mapperPath;
    // 指定Service层存储的路径
    private String     servicePath;
    // 指定ServiceImpl层存储的路径
    private String     serviceImplPath;
    // 指定RemoteService层存储的路径
    private String     remoteServicePath;
    // 指定RemoteServiceImpl层存储的路径
    private String     remoteServiceImplPath;
    // 指定validation的存储路径
    private String     validationPath;

    public CodeGenerator() {
        super();
    }

    /**
     * 构造方法
     * 
     * @param outputPath 文件导出的路径
     * @param javaPackage 包路径
     * @param dbTable 数据表
     * @param moduleCnName 数据表名对应的模块中文名
     */
    public CodeGenerator(String outputPath, String javaPackage, String dbTable, String moduleCnName) {
        // 初始化各个代码的存储路径
        this.initPath(javaPackage);
        // 初始化JDBC
        this.initJdbc(dbTable);
        // 生成代码
        this.generateMain(outputPath, domainPath, moduleName, moduleCnName);
    }

    /**
     * 初始化各个代码的存储路径
     */
    private void initPath(String javaPackage) {
        // 指定持久对象和值对象存储的路径
        this.domainPath = javaPackage + ".domain";
        // 指定DAO层存储的路径
        this.daoPath = javaPackage + ".dao";
        // 指定MAPPER文件的包路径
        this.mapperPath = javaPackage + ".mapper";
        // 指定Service层存储的路径
        this.servicePath = javaPackage + ".domain";
        // 指定ServiceImpl层存储的路径
        this.serviceImplPath = javaPackage + ".domain.impl";
        // 指定RemoteService层存储的路径
        this.remoteServicePath = javaPackage + ".domain.remote";
        // 指定RemoteServiceImpl层存储的路径
        this.remoteServiceImplPath = javaPackage + ".domain.remote.impl";
        // 指定validation的存储路径
        this.validationPath = javaPackage + ".validation";
    }

    private void generateMain(String outputPath, String domainPath, String moduleName, String moduleCnName) {
        // 1、生成PO
        this.generateDomain(outputPath, domainPath, moduleName, moduleCnName, "PO");
        // 2、生成VO
        this.generateDomain(outputPath, domainPath, moduleName, moduleCnName, "VO");
        // 3、生成DAO
        this.generateDAO(outputPath, daoPath, domainPath, moduleName, moduleCnName);
        // 4、生成Mapper
        this.generateMapper(outputPath, mapperPath, moduleName);
        // 5、生成Service
        this.generateService(outputPath, servicePath, domainPath, moduleName, moduleCnName);
        // 6、生成ServiceImpl
        this.generateServiceImpl(outputPath, daoPath, servicePath, serviceImplPath, domainPath, moduleName, moduleCnName);
        // 7、生成RemoteService
        this.generateRemoteService(outputPath, remoteServicePath, domainPath, moduleName, moduleCnName);
        // 8、生成RemoteServiceImpl
        this.generateRemoteServiceImpl(outputPath, remoteServiceImplPath, remoteServicePath, servicePath, domainPath, moduleName, moduleCnName);
        // 9、生成Validation
        this.generateValidation(outputPath, validationPath, domainPath, moduleName, moduleCnName);
    }

    /**
     * 初始化JDBC
     * 
     * @param javaPackage 包路径
     * @param dbTable 数据表
     */
    private void initJdbc(String dbTable) {
        this.dbTable = dbTable;
        try {
            Class.forName(this.driver);
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            this.con = DriverManager.getConnection(this.url, this.usr, this.passwd);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (this.con != null) {
                try {
                    this.con.close();
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
        PreparedStatement pstmt = null;
        String strsql = "select * from " + dbTable;
        try {
            pstmt = this.con.prepareStatement(strsql);
            ResultSetMetaData rsmd = pstmt.getMetaData();
            // 字段数
            int size = rsmd.getColumnCount();
            this.colNames = new String[size];
            this.colTypes = new String[size];
            this.colSizes = new int[size];

            this.attrs = new String[size];
            this.attrGetMethods = new String[size];
            this.myBatisJdbcTypes = new String[size];

            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                this.colNames[i] = rsmd.getColumnName(i + 1);
                this.colTypes[i] = rsmd.getColumnTypeName(i + 1).replace("UNSIGNED", "").trim();
                this.colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
                this.attrs[i] = getCamelString(rsmd.getColumnName(i + 1).toLowerCase());
                this.myBatisJdbcTypes[i] = sqlTypeToMybatisJdbcType(this.colTypes[i]);
            }

            // 获得持久对象和值对象的类名
            this.moduleName = dbTable;
            if (dbTable.indexOf("t_") == 0) {
                // 解析掉t_的前缀
                this.moduleName = this.moduleName.replace("t_", "");
            }
            if (dbTable.indexOf("u_") == 0) {
                // 解析掉u_的前缀
                this.moduleName = this.moduleName.replace("u_", "");
            }
            // 把输入字符串的首字母改成大写
            this.moduleName = initCap(this.moduleName);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                this.con.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 生成 Domain
     * 
     * @param outputPath 文件导出的路径
     * @param javaPackage 包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块的名称
     * @param concatSuffix 后缀名
     */
    private void generateDomain(String outputPath, String javaPackage, String moduleName, String moduleCnName, String concatSuffix) {
        String content = generateDomainCore(colNames, colTypes, colSizes, javaPackage, moduleName, moduleCnName, concatSuffix);
        outputPath = outputPath + "/" + javaPackage.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + concatSuffix + ".java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content, "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**  
     * 解析处理(生成实体类主体代码)  
     */
    private String generateDomainCore(String[] colNames, String[] colTypes, int[] colSizes, String packagePath, String moduleName, String moduleCnName, String concatSuffix) {
        StringBuffer sb = new StringBuffer();
        sb.append("package " + packagePath + ";\r\n");
        sb.append("\r\n");
        sb.append("import java.io.Serializable;\r\n");
        if (StringUtils.equals(concatSuffix, "VO")) {
            sb.append("\r\n");
            sb.append("import com.com.dongpinyun.base.domain.BaseVO;\r\n");
        }
        sb.append("\r\n");
        if (StringUtils.equals(concatSuffix, "PO")) {
            sb.append("/**\r\n");
            sb.append(" * " + moduleCnName + "的持久对象\r\n");
            sb.append(" */\r\n");
            sb.append("public class " + moduleName + concatSuffix + " implements Serializable {\r\n");
        }
        if (StringUtils.equals(concatSuffix, "VO")) {
            sb.append("/**\r\n");
            sb.append(" * " + moduleCnName + "的值对象\r\n");
            sb.append(" */\r\n");
            sb.append("public class " + moduleName + concatSuffix + " extends BaseVO implements Serializable {\r\n");
        }
        sb.append("\r\n");
        sb.append("    private static final long serialVersionUID = 1L;\r\n");
        processAllAttrs(sb);
        sb.append("\r\n");
        sb.append("    public " + moduleName + concatSuffix + "() {\r\n");
        sb.append("        super();\r\n");
        sb.append("    }\r\n");
        sb.append("\r\n");
        processAllMethod(sb);
        sb.append("}\r\n");
        return sb.toString();
    }

    /**
     * 生成 DAO
     * 
     * @param outputPath 文件导出的路径
     * @param daoPath 持久层的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateDAO(String outputPath, String daoPath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + daoPath + ";\r\n");
        content.append("\r\n");
        content.append("import java.util.List;\r\n");
        content.append("\r\n");
        content.append("import org.apache.ibatis.annotations.Param;\r\n");
        content.append("\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageVO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "VO;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的持久层接口类\r\n");
        content.append(" */\r\n");
        content.append("public interface " + moduleName + "DAO {\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 新增" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams 待新增的" + moduleCnName + "实例\r\n");
        content.append("     * @return 自增型主键\r\n");
        content.append("     */ \r\n");
        content.append("    int insert(@Param(\"insertParams\") " + moduleName + "PO insertParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 修改结果\r\n");
        content.append("     */ \r\n");
        content.append("    int update(@Param(\"setParams\") " + moduleName + "PO setParams, @Param(\"whereParams\") " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 删除结果\r\n");
        content.append("     */ \r\n");
        content.append("    int delete(@Param(\"whereParams\") " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，统计" + moduleCnName + "的数量\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 统计结果\r\n");
        content.append("     */ \r\n");
        content.append("    int count(@Param(\"whereParams\") " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO get(@Param(\"whereParams\") " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛选出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO select(@Param(\"whereParams\") " + moduleName + "VO whereParams, @Param(\"selectFields\") String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> list(@Param(\"whereParams\") " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛选出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> selects(@Param(\"whereParams\") " + moduleName + "VO whereParams, @Param(\"selectFields\") String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页对象\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> listPaging(@Param(\"whereParams\") " + moduleName + "VO whereParams, @Param(\"pageParams\") PageVO pageParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页对象\r\n");
        content.append("     * @param selectFields 筛选出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> selectsPaging(@Param(\"whereParams\") " + moduleName + "VO whereParams, @Param(\"pageParams\") PageVO pageParams, @Param(\"selectFields\") String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("}");

        outputPath = outputPath + "/" + daoPath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "DAO.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateMapper(String outputPath, String mapperPath, String moduleName) {
        StringBuffer content = new StringBuffer();
        content.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
        content.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n");
        content.append("<mapper namespace=\"" + daoPath + "." + moduleName + "DAO\">\r\n");
        content.append("\r\n");
        content.append("    <resultMap id=\"baseResultPO\" type=\"" + domainPath + "." + moduleName + "PO\">\r\n");
        for (int i = 0; i < this.colNames.length; i++) {
            if (i == 0) {
                content.append("        <id column=\"" + this.colNames[i] + "\" property=\"" + this.attrs[i] + "\" jdbcType=\"" + this.myBatisJdbcTypes[i] + "\" />\r\n");

            }
            else {
                content.append("        <result column=\"" + this.colNames[i] + "\" property=\"" + this.attrs[i] + "\" jdbcType=\"" + this.myBatisJdbcTypes[i] + "\" />\r\n");

            }
        }
        content.append("    </resultMap>\r\n");
        content.append("\r\n");

        content.append("    <sql id=\"selectAll\">\r\n");
        content.append("        <choose>\r\n");
        content.append("            <when test=\"selectFields != null\">\r\n");
        content.append("                <trim suffixOverrides=\",\">\r\n");
        content.append("                    <foreach collection=\"selectFields\" item=\"selectField\">\r\n");
        content.append("                        ${selectField},\r\n");
        content.append("                    </foreach>\r\n");
        content.append("                </trim>\r\n");
        content.append("            </when>\r\n");
        content.append("            <otherwise>*</otherwise>\r\n");
        content.append("        </choose>\r\n");
        content.append("    </sql>\r\n");
        content.append("\r\n");

        content.append("    <sql id=\"setAll\">\r\n");
        content.append("        <set>\r\n");
        for (int i = 1; i < this.colNames.length; i++) {
            content.append("            <if test=\"setParams." + this.attrs[i] + " != null\">`" + this.colNames[i] + "` = #{setParams." + this.attrs[i] + "},</if>\r\n");
        }
        content.append("        </set>\r\n");
        content.append("    </sql>\r\n");
        content.append("\r\n");
        
        content.append("    <sql id=\"whereAll\">\r\n");
        content.append("        <where>\r\n");
        for (int i = 0; i < this.colNames.length; i++) {
            content.append("            <if test=\"whereParams." + this.attrs[i] + " != null\">and `" + this.colNames[i] + "` = #{whereParams." + this.attrs[i] + "} </if>\r\n");
        }
        content.append("        </where>\r\n");
        content.append("        <if test=\"whereParams.sortBy != null and whereParams.sortBy != '' and whereParams.direction != null and whereParams.direction != ''\">\r\n");
        content.append("            order by ${whereParams.sortBy} ${whereParams.direction}, " + this.colNames[0] + "\r\n");
        content.append("        </if>\r\n");
        content.append("    </sql>\r\n");
        content.append("\r\n");
        
        content.append("    <sql id=\"pagingAll\">\r\n");
        content.append("        <if test=\"pageParams != null and pageParams.offset != null and pageParams.limit != null\">\r\n");
        content.append("            limit #{pageParams.offset}, #{pageParams.limit}\r\n");
        content.append("        </if>\r\n");
        content.append("    </sql>\r\n");
        content.append("\r\n");
        
        content.append("    <insert id=\"insert\" useGeneratedKeys=\"true\" keyProperty=\"insertParams." + this.attrs[0] + "\">\r\n");
        content.append("        insert into " + this.dbTable + "\r\n");
        content.append("        <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\r\n");
        for (int i = 1; i < this.colNames.length; i++) {
            content.append("            <if test=\"insertParams." + this.attrs[i] + " != null\">`" + this.colNames[i] + "`,</if>\r\n");
        }
        content.append("        </trim>\r\n");
        content.append("        <trim prefix=\"values(\" suffix=\")\" suffixOverrides=\",\">\r\n");
        for (int i = 1; i < this.colNames.length; i++) {
            content.append("            <if test=\"insertParams." + this.attrs[i] + " != null\">#{insertParams." + this.attrs[i] + "},</if>\r\n");
        }
        content.append("        </trim>\r\n");
        content.append("    </insert>\r\n");
        content.append("\r\n");
        
        content.append("    <update id=\"update\">\r\n");
        content.append("        update " + this.dbTable + "\r\n");
        content.append("        <include refid=\"setAll\" />\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </update>\r\n");
        content.append("\r\n");
        
        content.append("    <delete id=\"delete\">\r\n");
        content.append("        delete from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </delete>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"count\" resultType=\"java.lang.Integer\">\r\n");
        content.append("        select count(" + this.colNames[0] + ") from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"get\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select * from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"select\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select\r\n");
        content.append("        <include refid=\"selectAll\" />\r\n");
        content.append("        from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"list\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select * from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"selects\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select\r\n");
        content.append("        <include refid=\"selectAll\" />\r\n");
        content.append("        from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"listPaging\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select * from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("        <include refid=\"pagingAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        
        content.append("    <select id=\"selectsPaging\" resultMap=\"baseResultPO\">\r\n");
        content.append("        select\r\n");
        content.append("        <include refid=\"selectAll\" />\r\n");
        content.append("        from " + this.dbTable + "\r\n");
        content.append("        <include refid=\"whereAll\" />\r\n");
        content.append("        <include refid=\"pagingAll\" />\r\n");
        content.append("    </select>\r\n");
        content.append("\r\n");
        content.append("</mapper>");
        outputPath = outputPath + "/" + mapperPath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "Mapper.xml";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成 Service
     * 
     * @param outputPath 文件导出的路径
     * @param servicePath 业务层的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateService(String outputPath, String servicePath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + servicePath + ";\r\n");
        content.append("\r\n");
        content.append("import java.util.List;\r\n");
        content.append("\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageRecords;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageVO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "VO;\r\n");
        content.append("import com.com.dongpinyun.base.utils.exception.ServiceException;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的业务层接口\r\n");
        content.append(" */ \r\n");
        content.append("public interface " + moduleName + "Service {\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 新增" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams 待新增的" + moduleCnName + "实例\r\n");
        content.append("     * @return 装填了自增型主键的" + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO insert(" + moduleName + "PO insertParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return 修改结果\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean update(" + moduleName + "PO setParams, " + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 修改结果\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean update(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回受影响的行数\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 受影响的行数\r\n");
        content.append("     */ \r\n");
        content.append("    Integer updateAndRtnRows(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return 删除结果\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean delete(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号数组\r\n");
        content.append("     * @return 删除结果数组\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean[] delete(" + sqlTypeToJavaType(this.colTypes[0]) + "[] " + this.attrs[0] + ") throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 删除结果\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean delete(" + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 删除结果数组\r\n");
        content.append("     */ \r\n");
        content.append("    Boolean[] delete(" + moduleName + "VO[] whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，统计" + moduleCnName + "的数量\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 统计结果\r\n");
        content.append("     */ \r\n");
        content.append("    int count(" + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据编号查询" + moduleCnName + "对象\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO get(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO get(" + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    " + moduleName + "PO get(" + moduleName + "VO whereParams, String[] selectFields) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> list(" + moduleName + "VO whereParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    List<" + moduleName + "PO> list(" + moduleName + "VO whereParams, String[] selectFields) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @return 封装了分页的" + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    PageRecords<" + moduleName + "PO> list(" + moduleName + "VO whereParams, PageVO pageParams) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return 封装了分页的" + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    PageRecords<" + moduleName + "PO> list(" + moduleName + "VO whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;\r\n");
        content.append("\r\n");
        content.append("}");

        outputPath = outputPath + "/" + servicePath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "Service.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成 ServiceImpl
     * 
     * @param outputPath 文件导出的路径
     * @param daoPath 持久层接口类的包路径
     * @param servicePath 业务层接口类的包路径
     * @param serviceImplPath 业务层实现类的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateServiceImpl(String outputPath, String daoPath, String servicePath, String serviceImplPath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + serviceImplPath + ";\r\n");
        content.append("\r\n");
        content.append("import java.util.List;\r\n");
        content.append("\r\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
        content.append("import org.springframework.stereotype.Service;\r\n");
        content.append("\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageRecords;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageVO;\r\n");
        content.append("import " + daoPath + "." + moduleName + "DAO;\r\n");
        content.append("import " + servicePath + "." + moduleName + "Service;\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "VO;\r\n");
        content.append("import com.com.dongpinyun.base.utils.CollectionUtils;\r\n");
        content.append("import com.com.dongpinyun.base.utils.exception.ServiceException;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的业务层实现类\r\n");
        content.append(" */ \r\n");
        content.append("@Service(\"" + initLower(moduleName) + "Service\")\r\n");
        content.append("public class " + moduleName + "ServiceImpl implements " + moduleName + "Service {\r\n");
        content.append("\r\n");
        content.append("    @Autowired\r\n");
        content.append("    private " + moduleName + "DAO " + initLower(moduleName) + "DAO;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 新增" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams 待新增的" + moduleCnName + "实例\r\n");
        content.append("     * @return 装填了自增型主键的" + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public " + moduleName + "PO insert(" + moduleName + "PO insertParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            " + initLower(moduleName) + "DAO.insert(insertParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("        return insertParams;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return 修改结果\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean update(" + moduleName + "PO setParams, " + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            " + moduleName + "VO whereParams = new " + moduleName + "VO();\r\n");
        content.append("            whereParams.setId(id);\r\n");
        content.append("            return this.update(setParams, whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 修改结果\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean update(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return this.updateAndRtnRows(setParams, whereParams) >= 1;\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回受影响的行数\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 受影响的行数\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Integer updateAndRtnRows(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.update(setParams, whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return 删除结果\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean delete(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            " + moduleName + "VO whereParams = new " + moduleName + "VO();\r\n");
        content.append("            whereParams.setId(id);\r\n");
        content.append("            return this.delete(whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号数组\r\n");
        content.append("     * @return 删除结果数组\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean[] delete(" + sqlTypeToJavaType(this.colTypes[0]) + "[] " + this.attrs[0] + ") throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            if (CollectionUtils.isNotEmpty(" + this.attrs[0] + ")) {\r\n");
        content.append("                int i = 0;\r\n");
        content.append("                int length = " + this.attrs[0] + ".length;\r\n");
        content.append("                " + moduleName + "VO[] whereParams = new " + moduleName + "VO[length];\r\n");
        content.append("                for (; i < length; i++) {\r\n");
        content.append("                    whereParams[i] = new " + moduleName + "VO();\r\n");
        content.append("                    whereParams[i].setId(" + this.attrs[0] + "[i]);\r\n");
        content.append("                }\r\n");
        content.append("                return this.delete(whereParams);\r\n");
        content.append("            }\r\n");
        content.append("            else {\r\n");
        content.append("                return null;\r\n");
        content.append("            }\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 删除结果\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean delete(" + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            int result = " + initLower(moduleName) + "DAO.delete(whereParams);\r\n");
        content.append("            return result >= 1;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 删除结果数组\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public Boolean[] delete(" + moduleName + "VO[] whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            Boolean[] results = null;\r\n");
        content.append("            if (CollectionUtils.isNotEmpty(whereParams)) {\r\n");
        content.append("                int i = 0;\r\n");
        content.append("                int length = whereParams.length;\r\n");
        content.append("                results = new Boolean[length];\r\n");
        content.append("                for (; i < length; i++) {\r\n");
        content.append("                    results[i] = this.delete(whereParams[i]);\r\n");
        content.append("                }\r\n");
        content.append("            }\r\n");
        content.append("            return results;\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，统计" + moduleCnName + "的数量\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return 统计结果\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public int count(" + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.count(whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据编号查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public " + moduleName + "PO get(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            " + moduleName + "VO whereParams = new " + moduleName + "VO();\r\n");
        content.append("            whereParams.setId(" + this.attrs[0] + ");\r\n");
        content.append("            return this.get(whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            throw ex;\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public " + moduleName + "PO get(" + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.get(whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public " + moduleName + "PO get(" + moduleName + "VO whereParams, String[] selectFields) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.select(whereParams, selectFields);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public List<" + moduleName + "PO> list(" + moduleName + "VO whereParams) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.list(whereParams);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return " + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public List<" + moduleName + "PO> list(" + moduleName + "VO whereParams, String[] selectFields) throws ServiceException {\r\n");
        content.append("        try {\r\n");
        content.append("            return " + initLower(moduleName) + "DAO.selects(whereParams, selectFields);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @return 封装了分页的" + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public PageRecords<" + moduleName + "PO> list(" + moduleName + "VO whereParams, PageVO pageParams) throws ServiceException {\r\n");
        content.append("        PageRecords<" + moduleName + "PO> pageData = new PageRecords<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            int total = " + initLower(moduleName) + "DAO.count(whereParams);\r\n");
        content.append("            if (total >= 1) {\r\n");
        content.append("                pageData.setRecords(" + initLower(moduleName) + "DAO.listPaging(whereParams, pageParams));\r\n");
        content.append("            }\r\n");
        content.append("            pageData.setTotal(total);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("        return pageData;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return 封装了分页的" + moduleCnName + "对象列表\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public PageRecords<" + moduleName + "PO> list(" + moduleName + "VO whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {\r\n");
        content.append("        PageRecords<" + moduleName + "PO> pageData = new PageRecords<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            int total = " + initLower(moduleName) + "DAO.count(whereParams);\r\n");
        content.append("            if (total >= 1) {\r\n");
        content.append("                pageData.setRecords(" + initLower(moduleName) + "DAO.selectsPaging(whereParams, pageParams, selectFields));\r\n");
        content.append("            }\r\n");
        content.append("            pageData.setTotal(total);\r\n");
        content.append("        }\r\n");
        content.append("        catch (Exception ex) {\r\n");
        content.append("            throw new ServiceException(ex);\r\n");
        content.append("        }\r\n");
        content.append("        return pageData;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("}");
        outputPath = outputPath + "/" + serviceImplPath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "ServiceImpl.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成Remote Service
     * 
     * @param outputPath 文件导出的路径
     * @param remoteServicePath 远程服务层的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateRemoteService(String outputPath, String remoteServicePath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + remoteServicePath + ";\r\n");
        content.append("\r\n");
        content.append("import java.util.List;\r\n");
        content.append("\r\n");
        content.append("import com.com.dongpinyun.base.domain.DTO;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageRecords;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageVO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "VO;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的远程服务层接口\r\n");
        content.append(" */ \r\n");
        content.append("public interface " + moduleName + "RemoteService {\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 新增" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams 待新增的" + moduleCnName + "实例\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<" + moduleName + "PO> insert(" + moduleName + "PO insertParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean> update(" + moduleName + "PO setParams, " + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ");\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean> update(" + moduleName + "PO setParams, " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回受影响的行数\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Integer> updateAndRtnRows(" + moduleName + "PO setParams, " + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean> delete(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ");\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号数组\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean[]> delete(" + sqlTypeToJavaType(this.colTypes[0]) + "[] " + this.attrs[0] + ");\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean> delete(" + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Boolean[]> delete(" + moduleName + "VO[] whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，统计" + moduleCnName + "的数量\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<Integer> count(" + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据编号查询" + moduleCnName + "对象\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<" + moduleName + "PO> get(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ");\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<" + moduleName + "PO> get(" + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<" + moduleName + "PO> get(" + moduleName + "VO whereParams, String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<List<" + moduleName + "PO>> list(" + moduleName + "VO whereParams);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<List<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<PageRecords<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, PageVO pageParams);\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    ResultModel<PageRecords<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, PageVO pageParams, String[] selectFields);\r\n");
        content.append("\r\n");
        content.append("}");

        outputPath = outputPath + "/" + remoteServicePath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "RemoteService.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成Remote ServiceImpl
     * 
     * @param outputPath 文件导出的路径
     * @param remoteServicePath 远程服务层的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateRemoteServiceImpl(String outputPath, String remoteServiceImplPath, String remoteServicePath, String servicePath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + remoteServiceImplPath + ";\r\n");
        content.append("\r\n");
        content.append("import java.util.List;\r\n");
        content.append("\r\n");
        content.append("import org.springframework.beans.factory.annotation.Autowired;\r\n");
        content.append("import org.springframework.stereotype.Service;\r\n");
        content.append("\r\n");
        content.append("import com.com.dongpinyun.base.constant.serviceConstant.Constant;\r\n");
        content.append("import com.com.dongpinyun.base.domain.DTO;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageRecords;\r\n");
        content.append("import com.com.dongpinyun.base.domain.PageVO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import " + domainPath + "." + moduleName + "VO;\r\n");
        content.append("import " + servicePath + "." + moduleName + "Service;\r\n");
        content.append("import " + remoteServicePath + "." + moduleName + "RemoteService;\r\n");
        content.append("import com.com.dongpinyun.base.utils.exception.ServiceException;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的远程服务层实现类\r\n");
        content.append(" */ \r\n");
        content.append("@Service(\"" + initLower(moduleName) + "RemoteService\")\r\n");
        content.append("public class " + moduleName + "RemoteServiceImpl implements " + moduleName + "RemoteService {\r\n");
        content.append("\r\n");
        content.append("    @Autowired\r\n");
        content.append("    private " + moduleName + "Service " + initLower(moduleName) + "Service;\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 新增" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams 待新增的" + moduleCnName + "实例\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<" + moduleName + "PO> insert(" + moduleName + "PO insertParams) {\r\n");
        content.append("        ResultModel<" + moduleName + "PO> dto = new ResultModel<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.insert(insertParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean> update(" + moduleName + "PO setParams, " + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") {\r\n");
        content.append("        ResultModel<Boolean> dto = new ResultModel<Boolean>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.update(setParams, " + this.attrs[0] + "));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回修改结果\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean> update(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<Boolean> dto = new ResultModel<Boolean>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.update(setParams, whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 修改" + moduleCnName + "，返回受影响的行数\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams 待修改的" + moduleCnName + "实例\r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Integer> updateAndRtnRows(" + moduleName + "PO setParams, " + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<Integer> dto = new ResultModel<Integer>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.updateAndRtnRows(setParams, whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean> delete(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") {\r\n");
        content.append("        ResultModel<Boolean> dto = new ResultModel<Boolean>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.delete(" + this.attrs[0] + "));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据ID数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号数组\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean[]> delete(" + sqlTypeToJavaType(this.colTypes[0]) + "[] " + this.attrs[0] + ") {\r\n");
        content.append("        ResultModel<Boolean[]> dto = new ResultModel<Boolean[]>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.delete(" + this.attrs[0] + "));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean> delete(" + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<Boolean> dto = new ResultModel<Boolean>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.delete(whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件数组，批量删除" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Boolean[]> delete(" + moduleName + "VO[] whereParams) {\r\n");
        content.append("        ResultModel<Boolean[]> dto = new ResultModel<Boolean[]>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.delete(whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据查询条件，统计" + moduleCnName + "的数量\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<Integer> count(" + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<Integer> dto = new ResultModel<Integer>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.count(whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据编号查询" + moduleCnName + "对象\r\n");
        content.append("     * \r\n");
        content.append("     * @param " + this.attrs[0] + " 编号\r\n");
        content.append("     * @return " + moduleCnName + "对象\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<" + moduleName + "PO> get(" + sqlTypeToJavaType(this.colTypes[0]) + " " + this.attrs[0] + ") {\r\n");
        content.append("        ResultModel<" + moduleName + "PO> dto = new ResultModel<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.get(" + this.attrs[0] + "));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<" + moduleName + "PO> get(" + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<" + moduleName + "PO> dto = new ResultModel<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.get(whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<" + moduleName + "PO> get(" + moduleName + "VO whereParams, String[] selectFields) {\r\n");
        content.append("        ResultModel<" + moduleName + "PO> dto = new ResultModel<" + moduleName + "PO>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.get(whereParams, selectFields));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<List<" + moduleName + "PO>> list(" + moduleName + "VO whereParams) {\r\n");
        content.append("        ResultModel<List<" + moduleName + "PO>> dto = new ResultModel<List<" + moduleName + "PO>>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.list(whereParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<List<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, String[] selectFields) {\r\n");
        content.append("        ResultModel<List<" + moduleName + "PO>> dto = new ResultModel<List<" + moduleName + "PO>>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.list(whereParams, selectFields));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<PageRecords<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, PageVO pageParams) {\r\n");
        content.append("        ResultModel<PageRecords<" + moduleName + "PO>> dto = new ResultModel<PageRecords<" + moduleName + "PO>>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.list(whereParams, pageParams));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("    /**\r\n");
        content.append("     * 根据条件分页查询" + moduleCnName + "列表\r\n");
        content.append("     * \r\n");
        content.append("     * @param whereParams 查询条件\r\n");
        content.append("     * @param pageParams 分页实例\r\n");
        content.append("     * @param selectFields 筛查出来的字段\r\n");
        content.append("     * @return DTO\r\n");
        content.append("     */ \r\n");
        content.append("    @Override\r\n");
        content.append("    public ResultModel<PageRecords<" + moduleName + "PO>> list(" + moduleName + "VO whereParams, PageVO pageParams, String[] selectFields) {\r\n");
        content.append("        ResultModel<PageRecords<" + moduleName + "PO>> dto = new ResultModel<PageRecords<" + moduleName + "PO>>();\r\n");
        content.append("        try {\r\n");
        content.append("            dto.setData(" + initLower(moduleName) + "Service.list(whereParams, pageParams, selectFields));\r\n");
        content.append("        }\r\n");
        content.append("        catch (ServiceException ex) {\r\n");
        content.append("            dto.setCode(ex.getCode());\r\n");
        content.append("            dto.setMessage(ex.getMessage());\r\n");
        content.append("        }\r\n");
        content.append("        return dto;\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("}");

        outputPath = outputPath + "/" + remoteServiceImplPath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "RemoteServiceImpl.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 生成Validation
     * 
     * @param outputPath 文件导出的路径
     * @param validationPath 入参校验类的包路径
     * @param domainPath 模型层的包路径
     * @param moduleName 模块名
     * @param moduleCnName 模块中文名
     */
    private void generateValidation(String outputPath, String validationPath, String domainPath, String moduleName, String moduleCnName) {
        StringBuffer content = new StringBuffer();
        content.append("package " + validationPath + ";\r\n");
        content.append("\r\n");
        content.append("import " + domainPath + "." + moduleName + "PO;\r\n");
        content.append("import com.com.dongpinyun.base.utils.exception.ServiceException;\r\n");
        content.append("\r\n");
        content.append("/**\r\n");
        content.append(" * " + moduleCnName + "的入参校验类\r\n");
        content.append(" */ \r\n");
        content.append("public class " + moduleName + "Validation {\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 校验“新增" + moduleCnName + "”的入参\r\n");
        content.append("     * \r\n");
        content.append("     * @param insertParams\r\n");
        content.append("     * @throws ServiceException\r\n");
        content.append("     */ \r\n");
        content.append("    public static void validateInsert(" + moduleName + "PO insertParams) throws ServiceException {\r\n");
        content.append("\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 校验“修改" + moduleCnName + "”的入参\r\n");
        content.append("     * \r\n");
        content.append("     * @param setParams\r\n");
        content.append("     * @param id\r\n");
        content.append("     * @throws ServiceException\r\n");
        content.append("     */ \r\n");
        content.append("    public static void validateUpdate(" + moduleName + "PO setParams, Integer id) throws ServiceException {\r\n");
        content.append("\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 校验“删除" + moduleCnName + "”的入参\r\n");
        content.append("     * \r\n");
        content.append("     * @param id\r\n");
        content.append("     * @throws ServiceException\r\n");
        content.append("     */ \r\n");
        content.append("    public static void validateDelete(Integer id) throws ServiceException {\r\n");
        content.append("        if (id == null) {\r\n");
        content.append("            throw new ServiceException(\"请输入" + moduleCnName + "ID\");\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("    /**\r\n");
        content.append("     * 校验“根据编号查询" + moduleCnName + "”的入参\r\n");
        content.append("     * \r\n");
        content.append("     * @param id\r\n");
        content.append("     * @throws ServiceException\r\n");
        content.append("     */ \r\n");
        content.append("    public static void validateGet(Integer id) throws ServiceException {\r\n");
        content.append("        if (id == null) {\r\n");
        content.append("            throw new ServiceException(\"请输入" + moduleCnName + "ID\");\r\n");
        content.append("        }\r\n");
        content.append("    }\r\n");
        content.append("\r\n");
        content.append("}");
        outputPath = outputPath + "/" + validationPath.replaceAll("\\.", "/");
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        outputPath = outputPath + "/" + moduleName + "Validation.java";
        try {
            FileUtils.writeStringToFile(new File(outputPath), content.toString(), "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /////////////////////////////////////////////// 和业务无关的辅助方法////////////////////////////////////////////////////////

    /**
     * 实现：subject_balance --> subjectBalance
     * @return 经过驼峰修饰的字符串
     */
    private String getCamelString(String s) {
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
    private String initCap(String str) {
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
    private String initLower(String str) {
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        else {
            return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    /**  
    *  根据数据表的字段输出成Java类的属性  
    *   
    * @return  
    */
    private void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < attrs.length; i++) {
            sb.append("    private " + sqlTypeToJavaType(colTypes[i]) + " " + attrs[i] + ";\r\n");
        }
    }

    private String sqlTypeToJavaType(String sqlType) {
        if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("tinytext") || sqlType.equalsIgnoreCase("mediumtext") || sqlType.equalsIgnoreCase("longtext")) {
            return "String";
        }
        else if (sqlType.equalsIgnoreCase("char")) {
            return "String";
        }
        else if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        }
        else if (sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        }
        else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        }
        else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "Integer";
        }
        else if (sqlType.equalsIgnoreCase("smallint")) {
            return "Integer";
        }
        else if (sqlType.equalsIgnoreCase("decimal")) {
            return "java.math.BigDecimal";
        }
        else if (sqlType.equalsIgnoreCase("double")) {
            return "Double";
        }
        else if (sqlType.equalsIgnoreCase("float")) {
            return "Float";
        }
        else if (sqlType.equalsIgnoreCase("numeric")) {
            return "Double";
        }
        else if (sqlType.equalsIgnoreCase("datetime")) {
            return "java.util.Date";
        }
        else if (sqlType.equalsIgnoreCase("date")) {
            return "java.util.Date";
        }
        else if (sqlType.equalsIgnoreCase("time")) {
            return "java.util.Date";
        }
        else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "java.sql.Timestamp";
        }
        else if (sqlType.equalsIgnoreCase("real")) {
            return "Double";
        }
        return null;
    }

    private String sqlTypeToMybatisJdbcType(String sqlType) {
        if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("tinytext") || sqlType.equalsIgnoreCase("mediumtext") || sqlType.equalsIgnoreCase("longtext")) {
            return "VARCHAR";
        }
        else if (sqlType.equalsIgnoreCase("char")) {
            return "CHAR";
        }
        else if (sqlType.equalsIgnoreCase("bit")) {
            return "BIT";
        }
        else if (sqlType.equalsIgnoreCase("int")) {
            return "INTEGER";
        }
        else if (sqlType.equalsIgnoreCase("bigint")) {
            return "BIGINT";
        }
        else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "TINYINT";
        }
        else if (sqlType.equalsIgnoreCase("smallint")) {
            return "SMALLINT";
        }
        else if (sqlType.equalsIgnoreCase("decimal")) {
            return "DECIMAL";
        }
        else if (sqlType.equalsIgnoreCase("double")) {
            return "DOUBLE";
        }
        else if (sqlType.equalsIgnoreCase("float")) {
            return "FLOAT";
        }
        else if (sqlType.equalsIgnoreCase("numeric")) {
            return "NUMERIC";
        }
        else if (sqlType.equalsIgnoreCase("datetime")) {
            return "TIMESTAMP";
        }
        else if (sqlType.equalsIgnoreCase("date")) {
            return "DATE";
        }
        else if (sqlType.equalsIgnoreCase("time")) {
            return "TIME";
        }
        else if (sqlType.equalsIgnoreCase("timestamp")) {
            return "TIMESTAMP";
        }
        else if (sqlType.equalsIgnoreCase("real")) {
            return "REAL";
        }
        return null;
    }

    /**  
    * 生成所有的方法  
    *   
    * @param sb  
    */
    private void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < attrs.length; i++) {
            sb.append("    public void set" + initCap(attrs[i]) + "(" + sqlTypeToJavaType(colTypes[i]) + " " + attrs[i] + ") {\r\n");
            sb.append("        this." + attrs[i] + " = " + attrs[i] + ";\r\n");
            sb.append("    }\r\n");
            sb.append("\r\n");
            sb.append("    public " + sqlTypeToJavaType(colTypes[i]) + " get" + initCap(attrs[i]) + "() {\r\n");
            sb.append("        return " + attrs[i] + ";\r\n");
            sb.append("    }\r\n");
            sb.append("\r\n");
            attrGetMethods[i] = "get" + initCap(attrs[i]) + "()";
        }
    }
}