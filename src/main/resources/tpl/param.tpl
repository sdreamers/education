package ${packageName}.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import com.sixing.base.domain.base.BaseVO;
<#if columnList?exists >
<#list columnList as column>
<#if column.type == 'Date'>
import java.util.Date;
<#break>
</#if>
</#list>
<#list columnList as column>
<#if column.type == 'Timestamp'>
import java.sql.Timestamp;
<#break>
</#if>
</#list>
<#list columnList as column>
<#if column.type == 'BigDecimal'>
import java.math.BigDecimal;
<#break>
</#if>
</#list>
</#if>

/**
* ${moduleCName}VO 值对象
* @author ${author}
* @date ${createDate}
*/
@Getter
@Setter
public class ${moduleName}Query extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

	public ${moduleName}Query() {
	    super();
	}

	<#if columnList?exists >
	<#list columnList as column>
	<#if column??>
	private ${column.type} ${column.name};
	<#if column.type == 'Timestamp' || column.type == 'Date'>
	private String ${column.name}Start;
    private String ${column.name}End;
	</#if>
	</#if>
	</#list>
	</#if>
}