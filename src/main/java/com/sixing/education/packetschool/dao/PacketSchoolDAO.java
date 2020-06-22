package com.sixing.education.packetschool.dao;

import java.util.List;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packetschool.PacketSchoolPO;
import com.sixing.base.domain.packetschool.PacketSchoolQuery;
import org.apache.ibatis.annotations.Param;

/**
* 包学校的持久层接口类
* @author 
* @date 2020年6月20日
*/
public interface PacketSchoolDAO {

    /**
    * 新增包学校
    * @param insertParams 待新增的包学校实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") PacketSchoolPO insertParams);

    /**
    * 修改包学校
    * @param setParams 待修改的包学校实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") PacketSchoolPO setParams, @Param("whereParams") PacketSchoolQuery whereParams);

    /**
    * 根据查询条件，删除包学校
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") PacketSchoolQuery whereParams);

    /**
    * 根据查询条件，统计包学校的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") PacketSchoolQuery whereParams);

    /**
    * 根据条件查询包学校
    * @param whereParams 查询条件
    * @return 包学校对象
    */
    PacketSchoolPO get(@Param("whereParams") PacketSchoolQuery whereParams);

    /**
    * 根据条件查询包学校
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 包学校对象
    */
    PacketSchoolPO select(@Param("whereParams") PacketSchoolQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询包学校列表
    * @param whereParams 查询条件
    * @return 包学校对象
    */
    List<PacketSchoolPO> list(@Param("whereParams") PacketSchoolQuery whereParams);

    /**
    * 根据条件查询包学校列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 包学校对象
    */
    List<PacketSchoolPO> selects(@Param("whereParams") PacketSchoolQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询包学校列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 包学校对象列表
    */
    List<PacketSchoolPO> listPaging(@Param("whereParams") PacketSchoolQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询包学校列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 包学校对象列表
    */
    List<PacketSchoolPO> selectsPaging(@Param("whereParams") PacketSchoolQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}