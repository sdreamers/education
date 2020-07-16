package com.sixing.base.domain.school;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
* 学校的持久对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class SchoolPO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SchoolPO() {
        super();
    }

	private Long id;
	private String name;
    private Date createTime;
    private Long creatorId;
    private String creatorName;
}