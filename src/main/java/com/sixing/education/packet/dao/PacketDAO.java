package com.sixing.education.packet.dao;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 包的持久层接口类
* @author 
* @date 2020年6月20日
*/
public interface PacketDAO {

    /**
    * 新增包
    * @param insertParams 待新增的包实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") PacketPO insertParams);

    /**
    * 修改包
    * @param setParams 待修改的包实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") PacketPO setParams, @Param("whereParams") PacketQuery whereParams);

    /**
    * 根据查询条件，删除包
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") PacketQuery whereParams);

    /**
    * 根据查询条件，统计包的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") PacketQuery whereParams);

    /**
    * 根据条件查询包
    * @param whereParams 查询条件
    * @return 包对象
    */
    PacketPO get(@Param("whereParams") PacketQuery whereParams);

    /**
    * 根据条件查询包
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 包对象
    */
    PacketPO select(@Param("whereParams") PacketQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询包列表
    * @param whereParams 查询条件
    * @return 包对象
    */
    List<PacketPO> list(@Param("whereParams") PacketQuery whereParams);

    /**
    * 根据条件查询包列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 包对象
    */
    List<PacketPO> selects(@Param("whereParams") PacketQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询包列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 包对象列表
    */
    List<PacketPO> listPaging(@Param("whereParams") PacketQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询包列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 包对象列表
    */
    List<PacketPO> selectsPaging(@Param("whereParams") PacketQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}