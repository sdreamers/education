package com.sixing.base.utils.codegenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 代码生成者
 * <p>
 * 描述：根据数据表生成基础代码
 */
@ImportResource("classpath:generatecode.properties")
public class CodeGenerator {

    private static Configuration mFreeMakerConfiguration = null;

    /**
     * 作者
     */
    private String mAuthor;
    /**
     * 基础包名
     */
    private String mPackageNameBase;
    /**
     * 设置代码导出的目录
     */
    public String mBaseDir;
    /**
     * 数据表名
     * */
    private String cDBTableName;
    /**
     * 模块名
     * */
    private String cModuleName;
    /**
     * 中文模块名
     * */
    private String cModuleCName;

    private String mDriver;
    private String mUrl;
    private String mUser;
    private String mPassword;

    private String mPoFileName;
    private String mParamFileName;
    private String mVoFileName;
    private String mDaoFileName;
    private String mMapperFileName;
    private String mServiceFileName;
    private String mServiceImplFileName;
    private String mValidatorFileName;

    List<Column> mColumnList = null;

    public CodeGenerator() {
        super();
    }

    public void init() {
        mFreeMakerConfiguration = new Configuration(Configuration.getVersion());
        ClassPathResource classPathResource = new ClassPathResource("tpl");
        try {
            File dir = classPathResource.getFile();
            mFreeMakerConfiguration.setDirectoryForTemplateLoading(dir);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        mFreeMakerConfiguration.setDefaultEncoding("utf-8");

        // 初始化属性
        this.initConfig();
        // 初始化各个代码的存储路径
        this.initPath();
        // 初始化JDBC
        this.initJdbc();
    }

    public void initConfig() {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = CodeGenerator.class.getClassLoader().getResourceAsStream("generatecode.properties");

        try {
            properties.load(new InputStreamReader(in, "UTF-8"));
            //获取key对应的value值
            mAuthor = properties.getProperty("author", "dongpinyun");
            mBaseDir = properties.getProperty("baseDir", "f:\\generator");
            mPackageNameBase = properties.getProperty("packageNameBase");
            cDBTableName = properties.getProperty("tableName");
            cModuleName = CodeGeneratorUtil.getModuleName(cDBTableName);
            cModuleCName = properties.getProperty("moduleCName");
            mDriver = properties.getProperty("driver");
            mUrl = properties.getProperty("url");
            mUser = properties.getProperty("user");
            mPassword = properties.getProperty("password");

        } catch (Exception ex) {

        }
    }

    /**
     * 初始化各个代码的存储路径
     */
    private void initPath() {
        String beforeString = mBaseDir + "\\src\\";
        for (String dir : mPackageNameBase.split("\\.")) {
            beforeString += dir + "\\";
        }

        mPoFileName = beforeString + "domain\\" + cModuleName + "PO.java";

        mVoFileName = beforeString + "domain\\" + cModuleName + "VO.java";

        mParamFileName = beforeString + "domain\\" + cModuleName + "Query.java";

        mDaoFileName = beforeString + "dao\\" + cModuleName + "DAO.java";

        mMapperFileName = beforeString + "mapper\\" + cModuleName + "Mapper.xml";

        mServiceFileName = beforeString + "service\\" + cModuleName + "Service.java";

        mServiceImplFileName = beforeString + "service\\impl\\" + cModuleName + "ServiceImpl.java";

        mValidatorFileName = beforeString + "validation\\" + cModuleName + "Validation.java";
    }

    private void initJdbc() {
        try {
            Class.forName(this.mDriver);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        Connection mConn = null;
        try {
            mConn = DriverManager.getConnection(this.mUrl, this.mUser, this.mPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (mConn != null) {
                try {
                    mConn.close();
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }

        PreparedStatement preparedStatement = null;
        String strSql = "select * from " + this.cDBTableName;

        try {
            preparedStatement = mConn.prepareStatement(strSql);
            ResultSetMetaData result = preparedStatement.getMetaData();

            // 字段数
            int size = result.getColumnCount();
            this.mColumnList = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Column col = new Column();
                String colType = result.getColumnTypeName(i + 1).replace("UNSIGNED", "").trim();
                String colName = result.getColumnName(i + 1);

                col.setColName(colName);
                col.setColType(CodeGeneratorUtil.sqlTypeToMybatisJdbcType(colType));
                col.setName(CodeGeneratorUtil.getCamelString(colName));
                col.setFirstCapName(CodeGeneratorUtil.firstCap(colName));
                col.setType(CodeGeneratorUtil.sqlTypeToJavaType(colType));
                col.setSize(result.getColumnDisplaySize(i + 1));
                mColumnList.add(col);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                mConn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void generateMain() {
        generateFile("po.tpl", mPoFileName, getBasicDataModel());
        generateFile("vo.tpl", mVoFileName, getBasicDataModel());
        generateFile("param.tpl", mParamFileName, getBasicDataModel());
        generateFile("dao.tpl", mDaoFileName, getBasicDataModel());
        generateFile("mapper.tpl", mMapperFileName, getBasicDataModel());
        generateFile("service.tpl", mServiceFileName, getBasicDataModel());
        generateFile("serviceImpl.tpl", mServiceImplFileName, getBasicDataModel());
        generateFile("validator.tpl", mValidatorFileName, getBasicDataModel());
    }

    private void generateFile(String templateFile, String outFile, Map dataModel) {
        Template template = null;

        Writer out = null;

        try {
            template = mFreeMakerConfiguration.getTemplate(templateFile);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            File file = new File(outFile);

            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();

            out = new FileWriter(file);

            template.process(dataModel, out);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

    private Map getBasicDataModel() {
        Map<String, Object> dataModel = new HashMap<>(8);
        dataModel.put("moduleName", CodeGeneratorUtil.firstCap(cModuleName));
        dataModel.put("moduleCName", cModuleCName);
        dataModel.put("_moduleName", CodeGeneratorUtil.initLower(cModuleName));
        dataModel.put("author", mAuthor);
        dataModel.put("columnList", mColumnList);
        dataModel.put("packageName", mPackageNameBase);
        dataModel.put("createDate", CodeGeneratorUtil.getCurrentDate());
        dataModel.put("dbTable", cDBTableName);
        return dataModel;
    }

    public static void main(String[] args) {
        CodeGenerator generator = new CodeGenerator();
        generator.init();
        generator.generateMain();

        System.out.println("操作完成，进入" + generator.mBaseDir + "目录获取代码");
    }

}