package com.sixing.education.school.dao;

import java.util.List;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.school.SchoolPO;
import com.sixing.base.domain.school.SchoolQuery;
import org.apache.ibatis.annotations.Param;

/**
* 学校的持久层接口类
* @author 
* @date 2020年6月20日
*/
public interface SchoolDAO {

    /**
    * 新增学校
    * @param insertParams 待新增的学校实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") SchoolPO insertParams);

    /**
    * 修改学校
    * @param setParams 待修改的学校实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") SchoolPO setParams, @Param("whereParams") SchoolQuery whereParams);

    /**
    * 根据查询条件，删除学校
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") SchoolQuery whereParams);

    /**
    * 根据查询条件，统计学校的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") SchoolQuery whereParams);

    /**
    * 根据条件查询学校
    * @param whereParams 查询条件
    * @return 学校对象
    */
    SchoolPO get(@Param("whereParams") SchoolQuery whereParams);

    /**
    * 根据条件查询学校
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 学校对象
    */
    SchoolPO select(@Param("whereParams") SchoolQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询学校列表
    * @param whereParams 查询条件
    * @return 学校对象
    */
    List<SchoolPO> list(@Param("whereParams") SchoolQuery whereParams);

    /**
    * 根据条件查询学校列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 学校对象
    */
    List<SchoolPO> selects(@Param("whereParams") SchoolQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询学校列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 学校对象列表
    */
    List<SchoolPO> listPaging(@Param("whereParams") SchoolQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询学校列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 学校对象列表
    */
    List<SchoolPO> selectsPaging(@Param("whereParams") SchoolQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}