package com.sixing.base.utils.codegenerator;

public class Column {
    /**
     * 属性名称，首字母小写
     * */
    private String name;
    /**
     * 属性名称，首字母大写
     * */
    private String firstCapName;
    /**
     * 属性类型
     * */
    private String type;
    /**
     * 数据库属性名称
     * */
    private String colName;
    /**
     * 数据库属性类型
     * */
    private String colType;
    private int size;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstCapName() {
        return this.firstCapName;
    }

    public void setFirstCapName(String firstCapName) {
        this.firstCapName = firstCapName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public int getSize() {
        return size;
    }
}
