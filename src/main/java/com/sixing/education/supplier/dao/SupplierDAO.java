package com.sixing.education.supplier.dao;

import java.util.List;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.supplier.SupplierPO;
import com.sixing.base.domain.supplier.SupplierQuery;
import org.apache.ibatis.annotations.Param;

/**
* 供应商的持久层接口类
* @author 
* @date 2020年6月22日
*/
public interface SupplierDAO {

    /**
    * 新增供应商
    * @param insertParams 待新增的供应商实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") SupplierPO insertParams);

    /**
    * 修改供应商
    * @param setParams 待修改的供应商实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") SupplierPO setParams, @Param("whereParams") SupplierQuery whereParams);

    /**
    * 根据查询条件，删除供应商
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") SupplierQuery whereParams);

    /**
    * 根据查询条件，统计供应商的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") SupplierQuery whereParams);

    /**
    * 根据条件查询供应商
    * @param whereParams 查询条件
    * @return 供应商对象
    */
    SupplierPO get(@Param("whereParams") SupplierQuery whereParams);

    /**
    * 根据条件查询供应商
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 供应商对象
    */
    SupplierPO select(@Param("whereParams") SupplierQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询供应商列表
    * @param whereParams 查询条件
    * @return 供应商对象
    */
    List<SupplierPO> list(@Param("whereParams") SupplierQuery whereParams);

    /**
    * 根据条件查询供应商列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 供应商对象
    */
    List<SupplierPO> selects(@Param("whereParams") SupplierQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询供应商列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 供应商对象列表
    */
    List<SupplierPO> listPaging(@Param("whereParams") SupplierQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询供应商列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 供应商对象列表
    */
    List<SupplierPO> selectsPaging(@Param("whereParams") SupplierQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}