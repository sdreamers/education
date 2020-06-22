package ${packageName}.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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
* ${moduleCName}的持久对象
* @author ${author}
* @date ${createDate}
*/
@Getter
@Setter
public class ${moduleName}PO implements Serializable {
    private static final long serialVersionUID = 1L;

	public ${moduleName}PO() {
        super();
    }

	<#if columnList?exists >
	<#list columnList as column>
	<#if column??>
	private ${column.type} ${column.name};
	</#if>
	</#list>
	</#if>
}