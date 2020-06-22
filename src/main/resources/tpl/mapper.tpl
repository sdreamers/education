<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.dao.${moduleName}BaseDAO">

    <resultMap id="baseResultPO" type="${packageName}.domain.${moduleName}PO">
        <#if columnList?exists >
        <#list columnList as column>
        <#if column_index == 0>
        <id column="${column.colName}" property="${column.name}" jdbcType="${column.colType}" />
        <#else>
        <result column="${column.colName}" property="${column.name}" jdbcType="${column.colType}" />
        </#if>
        </#list>
        </#if>
    </resultMap>

    <sql id="selectAll">
        <choose>
            <when test="selectFields != null">
                <trim suffixOverrides=",">
                    <foreach collection="selectFields" item="selectField">
                        ${r'${selectField}'},
                    </foreach>
                </trim>
            </when>
            <otherwise>*</otherwise>
        </choose>
    </sql>

    <sql id="setAll">
        <set>
            <#if columnList?exists >
            <#list columnList as column>
            <#if column_index != 0>
            <if test="setParams.${column.name} != null">`${column.colName}`=${r'#{setParams.'}${column.name}},</if>
            </#if>
            </#list>
            </#if>
        </set>
    </sql>

    <sql id="whereAll">
        <where>
            <#if columnList?exists >
            <#list columnList as column>
            <if test="whereParams.${column.name} != null">AND `${column.colName}`=${r'#{whereParams'}.${column.name}}</if>
            </#list>
            <#list columnList as column>
            <#if column.type == 'Timestamp' || column.type == 'Date'>
            <if test="whereParams.${column.name}Start != null and whereParams.${column.name}Start != ''">
                AND `${column.colName}` &gt;= ${r'#{whereParams'}.${column.name}Start}
            </if>
            <if test="whereParams.${column.name}End != null and whereParams.${column.name}End != ''">
                AND `${column.colName}` &lt;= ${r'#{whereParams'}.${column.name}End}
            </if>
            </#if>
            </#list>
            </#if>
        </where>
    </sql>
    <sql id="sortAll">
        <if test="whereParams.sortBy != null and whereParams.sortBy != '' and whereParams.direction != null and whereParams.direction != ''">
            order by ${r'${whereParams.sortBy}'} ${r'${whereParams.direction}'}, ${columnList[0].colName}
        </if>
    </sql>
    <sql id="pagingAll">
        <if test="pageParams != null and pageParams.offset != null and pageParams.limit != null">
            limit ${r'#{pageParams.offset}'}, ${r'#{pageParams.limit}'}
        </if>
    </sql>

    <insert id="insert" useGeneratedKeys="true" keyProperty="insertParams.${columnList[0].name}">
        insert into ${dbTable}
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list columnList as column>
            <if test="insertParams.${column.name} != null"> `${column.colName}`,</if>
        </#list>
        </trim>
        <trim prefix="values(" suffix=")" suffixOverrides=",">
        <#list columnList as column>
            <if test="insertParams.${column.name} != null">${r'#{insertParams'}.${column.name}},</if>
        </#list>
        </trim>
    </insert>

    <update id="update">
        update ${dbTable}
        <include refid="setAll" />
        <include refid="whereAll" />
    </update>

    <delete id="delete">
        delete from ${dbTable}
        <include refid="whereAll" />
    </delete>

    <select id="count" resultType="java.lang.Integer">
        select count(${columnList[0].colName}) from ${dbTable}
        <include refid="whereAll" />
    </select>

    <select id="get" resultMap="baseResultPO">
        select * from ${dbTable}
        <include refid="whereAll" />
    </select>

    <select id="select" resultMap="baseResultPO">
        select
        <include refid="selectAll" />
        from ${dbTable}
        <include refid="whereAll" />
    </select>

    <select id="list" resultMap="baseResultPO">
        select * from ${dbTable}
        <include refid="whereAll" />
        <include refid="sortAll" />
    </select>

    <select id="selects" resultMap="baseResultPO">
        select
        <include refid="selectAll" />
        from ${dbTable}
        <include refid="whereAll" />
        <include refid="sortAll" />
    </select>

    <select id="listPaging" resultMap="baseResultPO">
        select * from ${dbTable}
        <include refid="whereAll" />
        <include refid="sortAll" />
        <include refid="pagingAll" />
    </select>

    <select id="selectsPaging" resultMap="baseResultPO">
        select
        <include refid="selectAll" />
        from ${dbTable}
        <include refid="whereAll" />
        <include refid="sortAll" />
        <include refid="pagingAll" />
    </select>

</mapper>