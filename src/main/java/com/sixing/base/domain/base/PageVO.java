package com.sixing.base.domain.base;

import java.io.Serializable;

public class PageVO implements Serializable {

    private static final long serialVersionUID = 5265842077474477983L;
    // 当前页码，默认1
    private Integer page = 1;
    // 从第几行开始查询
    private Integer offset;
    // 查询几行，默认20行
    private Integer limit = 20;

    public PageVO() {
    	super();
    }
    
    public PageVO(Integer page, Integer limit) {
		super();
		this.page = page;
		this.limit = limit;
		this.setPage(page);
	}

	public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        page = (page == null ? 1 : page);
        if (page >= 1) {
            // 当page不为空，触发设置分页参数offset
            this.setOffset((page - 1) * getLimit());
        }
        this.page = page;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer i) {
        this.offset = i;
    }

    public void setOffset() {
        if (this.page != null && this.limit != null) {
            this.setOffset((this.page - 1) * this.limit);
        }
    }

    public Integer getLimit() {
        if (limit == null) {
            limit = 20;
        }
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
        this.setOffset((page - 1) * limit);
    }

    @Override
    public String toString(){
    	return net.sf.json.JSONObject.fromObject(this).toString();
    }

}
