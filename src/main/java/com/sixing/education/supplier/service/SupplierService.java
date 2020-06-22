package com.sixing.education.supplier.service;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.supplier.SupplierPO;
import com.sixing.base.domain.supplier.SupplierQuery;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
* 供应商的业务层接口
* @author 
* @date 2020年6月22日
*/
public interface SupplierService {

    /**
    * 新增供应商
    *
    * @param insertParams 待新增的供应商实例
    * @return 装填了自增型主键的供应商对象
    * @throws ServiceException 业务异常
    */
    SupplierPO insert(SupplierPO insertParams) throws ServiceException;

    /**
    * 根据ID修改，返回修改结果
    *
    * @param setParams 待修改的供应商实例
    * @param id 编号
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(SupplierPO setParams, Long id) throws ServiceException;

    /**
    * 修改供应商，返回修改结果
    *
    * @param setParams 待修改的供应商实例
    * @param whereParams 查询条件
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(SupplierPO setParams, SupplierQuery whereParams) throws ServiceException;

    /**
    * 修改供应商，返回受影响的行数
    *
    * @param setParams 待修改的供应商实例
    * @param whereParams 查询条件
    * @return 受影响的行数
    * @throws ServiceException 业务异常
    */
    Integer updateAndRtnRows(SupplierPO setParams, SupplierQuery whereParams) throws ServiceException;

    /**
    * 根据ID删除供应商
    *
    * @param id 编号
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(Long id) throws ServiceException;

    /**
    * 根据ID数组，批量删除供应商
    *
    * @param id 编号数组
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
    * 根据查询条件，删除供应商
    *
    * @param whereParams 查询条件
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(SupplierQuery whereParams) throws ServiceException;

    /**
    * 根据查询条件数组，批量删除供应商
    *
    * @param whereParams 查询条件
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(SupplierQuery[] whereParams) throws ServiceException;

    /**
    * 根据查询条件，统计供应商的数量
    *
    * @param whereParams 查询条件
    * @return 统计结果
    * @throws ServiceException 业务异常
    */
    int count(SupplierQuery whereParams) throws ServiceException;

    /**
    * 根据编号查询供应商对象
    *
    * @param id 编号
    * @return 供应商对象
    * @throws ServiceException 业务异常
    */
    SupplierPO get(Long id) throws ServiceException;

    /**
    * 根据条件查询供应商
    *
    * @param whereParams 查询条件
    * @return 供应商对象
    * @throws ServiceException 业务异常
    */
    SupplierPO get(SupplierQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询供应商
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 供应商对象
    * @throws ServiceException 业务异常
    */
    SupplierPO get(SupplierQuery whereParams, String[] selectFields) throws ServiceException;

    /**
    * 根据条件查询供应商列表
    *
    * @param whereParams 查询条件
    * @return 供应商对象列表
    * @throws ServiceException 业务异常
    */
    List<SupplierPO> list(SupplierQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询供应商列表
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 供应商对象列表
    * @throws ServiceException 业务异常
    */
    List<SupplierPO> list(SupplierQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询供应商列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 供应商对象列表
     * @throws ServiceException 业务异常
     */
     List<SupplierPO> listPaging(SupplierQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询供应商列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @return 封装了分页的供应商对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<SupplierPO> pages(SupplierQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询供应商列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @param selectFields 筛查出来的字段
    * @return 封装了分页的供应商对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<SupplierPO> pages(SupplierQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;
}