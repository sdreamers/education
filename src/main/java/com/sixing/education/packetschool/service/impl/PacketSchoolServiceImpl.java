package com.sixing.education.packetschool.service.impl;

import java.util.List;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packetschool.PacketSchoolPO;
import com.sixing.base.domain.packetschool.PacketSchoolQuery;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.packetschool.dao.PacketSchoolDAO;
import com.sixing.education.packetschool.service.PacketSchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 包学校的业务实现类
* @author 
* @date 2020年6月20日
*/
@Service("packetSchoolBaseService")
public class PacketSchoolServiceImpl implements PacketSchoolService {

    @Autowired
    private PacketSchoolDAO packetSchoolDAO;

    /**
    * 新增包学校
    *
    * @param insertParams 待新增的包学校实例
    * @return 装填了自增型主键的包学校对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public PacketSchoolPO insert(PacketSchoolPO insertParams) throws ServiceException {
        try {
            packetSchoolDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的包学校实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(PacketSchoolPO setParams, Long id) throws ServiceException {
        try {
            PacketSchoolQuery whereParams = new PacketSchoolQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改包学校，返回修改结果
     *
     * @param setParams 待修改的包学校实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(PacketSchoolPO setParams, PacketSchoolQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改包学校，返回受影响的行数
     *
     * @param setParams 待修改的包学校实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(PacketSchoolPO setParams, PacketSchoolQuery whereParams) throws ServiceException {
        try {
            return packetSchoolDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除包学校
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            PacketSchoolQuery whereParams = new PacketSchoolQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除包学校
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
                PacketSchoolQuery[] whereParams = new PacketSchoolQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new PacketSchoolQuery();
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
     * 根据查询条件，删除包学校
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(PacketSchoolQuery whereParams) throws ServiceException {
        try {
            int result = packetSchoolDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除包学校
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(PacketSchoolQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计包学校的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(PacketSchoolQuery whereParams) throws ServiceException {
        try {
            return packetSchoolDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询包学校对象
     *
     * @param id 编号
     * @return 包学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketSchoolPO get(Long id) throws ServiceException {
        try {
            PacketSchoolQuery whereParams = new PacketSchoolQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包学校
     *
     * @param whereParams 查询条件
     * @return 包学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketSchoolPO get(PacketSchoolQuery whereParams) throws ServiceException {
        try {
            return packetSchoolDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包学校
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 包学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketSchoolPO get(PacketSchoolQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return packetSchoolDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包学校列表
     *
     * @param whereParams 查询条件
     * @return 包学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketSchoolPO> list(PacketSchoolQuery whereParams) throws ServiceException {
        try {
            return packetSchoolDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包学校列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 包学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketSchoolPO> list(PacketSchoolQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return packetSchoolDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 包学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketSchoolPO> listPaging(PacketSchoolQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return packetSchoolDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询包学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的包学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<PacketSchoolPO> pages(PacketSchoolQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<PacketSchoolPO> pageData = new PageRecords<>();
        try {
            int total = packetSchoolDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(packetSchoolDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询包学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的包学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<PacketSchoolPO> pages(PacketSchoolQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<PacketSchoolPO> pageData = new PageRecords<>();
        try {
            int total = packetSchoolDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(packetSchoolDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}