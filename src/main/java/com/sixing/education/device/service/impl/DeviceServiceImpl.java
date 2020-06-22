package com.sixing.education.device.service.impl;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.device.DevicePO;
import com.sixing.base.domain.device.DeviceQuery;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.device.dao.DeviceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 设备的业务实现类
* @author 
* @date 2020年6月20日
*/
@Service("deviceBaseService")
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDAO deviceDAO;

    /**
    * 新增设备
    *
    * @param insertParams 待新增的设备实例
    * @return 装填了自增型主键的设备对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public DevicePO insert(DevicePO insertParams) throws ServiceException {
        try {
            deviceDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的设备实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(DevicePO setParams, Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改设备，返回修改结果
     *
     * @param setParams 待修改的设备实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(DevicePO setParams, DeviceQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改设备，返回受影响的行数
     *
     * @param setParams 待修改的设备实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(DevicePO setParams, DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除设备
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除设备
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
                DeviceQuery[] whereParams = new DeviceQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new DeviceQuery();
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
     * 根据查询条件，删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(DeviceQuery whereParams) throws ServiceException {
        try {
            int result = deviceDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(DeviceQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计设备的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询设备对象
     *
     * @param id 编号
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
     @Override
     public DevicePO get(Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询设备
     *
     * @param whereParams 查询条件
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
     @Override
     public DevicePO get(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询设备
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
     @Override
     public DevicePO get(DeviceQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return deviceDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<DevicePO> list(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<DevicePO> list(DeviceQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return deviceDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<DevicePO> listPaging(DeviceQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return deviceDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<DevicePO> pageData = new PageRecords<>();
        try {
            int total = deviceDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(deviceDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<DevicePO> pageData = new PageRecords<>();
        try {
            int total = deviceDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(deviceDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}