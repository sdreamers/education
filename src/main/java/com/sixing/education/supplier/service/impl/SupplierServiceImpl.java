package com.sixing.education.supplier.service.impl;

import java.util.List;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.supplier.SupplierPO;
import com.sixing.base.domain.supplier.SupplierQuery;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.supplier.dao.SupplierDAO;
import com.sixing.education.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 供应商的业务实现类
* @author 
* @date 2020年6月22日
*/
@Service("supplierService")
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierDAO supplierDAO;

    /**
    * 新增供应商
    *
    * @param insertParams 待新增的供应商实例
    * @return 装填了自增型主键的供应商对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public SupplierPO insert(SupplierPO insertParams) throws ServiceException {
        try {
            supplierDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的供应商实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(SupplierPO setParams, Long id) throws ServiceException {
        try {
            SupplierQuery whereParams = new SupplierQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改供应商，返回修改结果
     *
     * @param setParams 待修改的供应商实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(SupplierPO setParams, SupplierQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改供应商，返回受影响的行数
     *
     * @param setParams 待修改的供应商实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(SupplierPO setParams, SupplierQuery whereParams) throws ServiceException {
        try {
            return supplierDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除供应商
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            SupplierQuery whereParams = new SupplierQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除供应商
     *
     * @param ids 编号数组
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(Long[] ids) throws ServiceException {
        try {
            if (CollectionUtils.isNotEmpty(ids)) {
                int i = 0;
                int length = ids.length;
                SupplierQuery[] whereParams = new SupplierQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new SupplierQuery();
                    whereParams[i].setId(ids[i]);
                }
                return this.delete(whereParams);
            } else {
                return null;
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件，删除供应商
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(SupplierQuery whereParams) throws ServiceException {
        try {
            int result = supplierDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除供应商
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(SupplierQuery[] whereParams) throws ServiceException {
        try {
            Boolean[] results = null;
            if (CollectionUtils.isNotEmpty(whereParams)) {
                int i = 0;
                int length = whereParams.length;
                results = new Boolean[length];
                for (; i < length; i++) {
                    results[i] = this.delete(whereParams[i]);
                }
            }
            return results;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件，统计供应商的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(SupplierQuery whereParams) throws ServiceException {
        try {
            return supplierDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询供应商对象
     *
     * @param id 编号
     * @return 供应商对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SupplierPO get(Long id) throws ServiceException {
        try {
            SupplierQuery whereParams = new SupplierQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询供应商
     *
     * @param whereParams 查询条件
     * @return 供应商对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SupplierPO get(SupplierQuery whereParams) throws ServiceException {
        try {
            return supplierDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询供应商
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 供应商对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SupplierPO get(SupplierQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return supplierDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询供应商列表
     *
     * @param whereParams 查询条件
     * @return 供应商对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SupplierPO> list(SupplierQuery whereParams) throws ServiceException {
        try {
            return supplierDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询供应商列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 供应商对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SupplierPO> list(SupplierQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return supplierDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询供应商列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 供应商对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SupplierPO> listPaging(SupplierQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return supplierDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询供应商列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的供应商对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<SupplierPO> pages(SupplierQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<SupplierPO> pageData = new PageRecords<>();
        try {
            int total = supplierDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(supplierDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询供应商列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的供应商对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<SupplierPO> pages(SupplierQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<SupplierPO> pageData = new PageRecords<>();
        try {
            int total = supplierDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(supplierDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}