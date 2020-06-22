package com.sixing.education.device.dao;

import java.util.List;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.device.DevicePO;
import com.sixing.base.domain.device.DeviceQuery;
import org.apache.ibatis.annotations.Param;

/**
* 设备的持久层接口类
* @author 
* @date 2020年6月20日
*/
public interface DeviceDAO {

    /**
    * 新增设备
    * @param insertParams 待新增的设备实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") DevicePO insertParams);

    /**
    * 修改设备
    * @param setParams 待修改的设备实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") DevicePO setParams, @Param("whereParams") DeviceQuery whereParams);

    /**
    * 根据查询条件，删除设备
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") DeviceQuery whereParams);

    /**
    * 根据查询条件，统计设备的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") DeviceQuery whereParams);

    /**
    * 根据条件查询设备
    * @param whereParams 查询条件
    * @return 设备对象
    */
    DevicePO get(@Param("whereParams") DeviceQuery whereParams);

    /**
    * 根据条件查询设备
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 设备对象
    */
    DevicePO select(@Param("whereParams") DeviceQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询设备列表
    * @param whereParams 查询条件
    * @return 设备对象
    */
    List<DevicePO> list(@Param("whereParams") DeviceQuery whereParams);

    /**
    * 根据条件查询设备列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 设备对象
    */
    List<DevicePO> selects(@Param("whereParams") DeviceQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询设备列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 设备对象列表
    */
    List<DevicePO> listPaging(@Param("whereParams") DeviceQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询设备列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 设备对象列表
    */
    List<DevicePO> selectsPaging(@Param("whereParams") DeviceQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}