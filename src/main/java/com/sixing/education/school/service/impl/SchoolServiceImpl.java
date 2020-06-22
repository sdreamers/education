package com.sixing.education.school.service.impl;

import java.util.List;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.school.SchoolPO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.school.dao.SchoolDAO;
import com.sixing.education.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 学校的业务实现类
* @author 
* @date 2020年6月20日
*/
@Service("schoolBaseService")
public class SchoolServiceImpl implements SchoolService {

    @Autowired
    private SchoolDAO schoolDAO;

    /**
    * 新增学校
    *
    * @param insertParams 待新增的学校实例
    * @return 装填了自增型主键的学校对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public SchoolPO insert(SchoolPO insertParams) throws ServiceException {
        try {
            schoolDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的学校实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(SchoolPO setParams, Long id) throws ServiceException {
        try {
            SchoolQuery whereParams = new SchoolQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改学校，返回修改结果
     *
     * @param setParams 待修改的学校实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(SchoolPO setParams, SchoolQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改学校，返回受影响的行数
     *
     * @param setParams 待修改的学校实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(SchoolPO setParams, SchoolQuery whereParams) throws ServiceException {
        try {
            return schoolDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除学校
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            SchoolQuery whereParams = new SchoolQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除学校
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
                SchoolQuery[] whereParams = new SchoolQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new SchoolQuery();
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
     * 根据查询条件，删除学校
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(SchoolQuery whereParams) throws ServiceException {
        try {
            int result = schoolDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除学校
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(SchoolQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计学校的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(SchoolQuery whereParams) throws ServiceException {
        try {
            return schoolDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询学校对象
     *
     * @param id 编号
     * @return 学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SchoolPO get(Long id) throws ServiceException {
        try {
            SchoolQuery whereParams = new SchoolQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询学校
     *
     * @param whereParams 查询条件
     * @return 学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SchoolPO get(SchoolQuery whereParams) throws ServiceException {
        try {
            return schoolDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询学校
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 学校对象
     * @throws ServiceException 业务异常
     */
     @Override
     public SchoolPO get(SchoolQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return schoolDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询学校列表
     *
     * @param whereParams 查询条件
     * @return 学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SchoolPO> list(SchoolQuery whereParams) throws ServiceException {
        try {
            return schoolDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询学校列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SchoolPO> list(SchoolQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return schoolDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<SchoolPO> listPaging(SchoolQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return schoolDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<SchoolPO> pages(SchoolQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<SchoolPO> pageData = new PageRecords<>();
        try {
            int total = schoolDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(schoolDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的学校对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<SchoolPO> pages(SchoolQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<SchoolPO> pageData = new PageRecords<>();
        try {
            int total = schoolDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(schoolDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}