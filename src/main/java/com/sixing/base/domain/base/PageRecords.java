package com.sixing.base.domain.base;

import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * response data structure;
 * @author dengchicheng
 *
 * @param <T> Any customized domain class type.
 */
public class PageRecords<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9034417790644550777L;

    private List<T>           records;

    private int               total;

    private Object            dict;

    private List<Column>      columns;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Object getDict() {
        return dict;
    }

    public void setDict(Object dict) {
        this.dict = dict;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return JSONObject.fromObject(this).toString();
    }

}
