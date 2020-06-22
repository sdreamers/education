package com.sixing.education.packet.service.impl;

import java.util.List;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.packet.dao.PacketDAO;
import com.sixing.education.packet.service.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 包的业务实现类
* @author 
* @date 2020年6月20日
*/
@Service("packetBaseService")
public class PacketServiceImpl implements PacketService {

    @Autowired
    private PacketDAO packetDAO;

    /**
    * 新增包
    *
    * @param insertParams 待新增的包实例
    * @return 装填了自增型主键的包对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public PacketPO insert(PacketPO insertParams) throws ServiceException {
        try {
            packetDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的包实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(PacketPO setParams, Long id) throws ServiceException {
        try {
            PacketQuery whereParams = new PacketQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改包，返回修改结果
     *
     * @param setParams 待修改的包实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(PacketPO setParams, PacketQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改包，返回受影响的行数
     *
     * @param setParams 待修改的包实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(PacketPO setParams, PacketQuery whereParams) throws ServiceException {
        try {
            return packetDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除包
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            PacketQuery whereParams = new PacketQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除包
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
                PacketQuery[] whereParams = new PacketQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new PacketQuery();
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
     * 根据查询条件，删除包
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(PacketQuery whereParams) throws ServiceException {
        try {
            int result = packetDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除包
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(PacketQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计包的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(PacketQuery whereParams) throws ServiceException {
        try {
            return packetDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询包对象
     *
     * @param id 编号
     * @return 包对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketPO get(Long id) throws ServiceException {
        try {
            PacketQuery whereParams = new PacketQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包
     *
     * @param whereParams 查询条件
     * @return 包对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketPO get(PacketQuery whereParams) throws ServiceException {
        try {
            return packetDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 包对象
     * @throws ServiceException 业务异常
     */
     @Override
     public PacketPO get(PacketQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return packetDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包列表
     *
     * @param whereParams 查询条件
     * @return 包对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketPO> list(PacketQuery whereParams) throws ServiceException {
        try {
            return packetDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 包对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketPO> list(PacketQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return packetDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询包列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 包对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<PacketPO> listPaging(PacketQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return packetDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询包列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的包对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<PacketPO> pages(PacketQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<PacketPO> pageData = new PageRecords<>();
        try {
            int total = packetDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(packetDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询包列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的包对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<PacketPO> pages(PacketQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<PacketPO> pageData = new PageRecords<>();
        try {
            int total = packetDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(packetDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}