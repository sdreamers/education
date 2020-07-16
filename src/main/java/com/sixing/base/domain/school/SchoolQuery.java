package com.sixing.base.domain.school;

import com.sixing.base.domain.base.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
* 学校VO 值对象
* @author 
* @date 2020年6月20日
*/
@Getter
@Setter
public class SchoolQuery extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public SchoolQuery() {
	    super();
	}

	private Long id;
	private String name;
	private Date createTime;
	private Long creatorId;
	private String creatorName;
	private String[] schools;
}