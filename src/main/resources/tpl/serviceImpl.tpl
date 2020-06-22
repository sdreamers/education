package ${packageName}.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dongpinyun.domain.PageRecords;
import com.dongpinyun.domain.PageVO;
import ${packageName}.dao.${moduleName}DAO;
import ${packageName}.service.${moduleName}Service;
import ${packageName}.domain.${moduleName}VO;
import ${packageName}.domain.${moduleName}Query;
import ${packageName}.domain.${moduleName}PO;
import com.dongpinyun.utils.CollectionUtils;
import com.dongpinyun.utils.exception.ServiceException;

/**
* ${moduleCName}的业务实现类
* @author ${author}
* @date ${createDate}
*/
@Service("${_moduleName}BaseService")
public class ${moduleName}ServiceImpl implements ${moduleName}Service {

    @Autowired
    private ${moduleName}DAO ${_moduleName}DAO;

    /**
    * 新增${moduleCName}
    *
    * @param insertParams 待新增的${moduleCName}实例
    * @return 装填了自增型主键的${moduleCName}对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public ${moduleName}PO insert(${moduleName}PO insertParams) throws ServiceException {
        try {
            ${_moduleName}DAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的${moduleCName}实例
     * @param ${columnList[0].name} 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(${moduleName}PO setParams, ${columnList[0].type} ${columnList[0].name}) throws ServiceException {
        try {
            ${moduleName}Query whereParams = new ${moduleName}Query();
            whereParams.setId(${columnList[0].name});
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改${moduleCName}，返回修改结果
     *
     * @param setParams 待修改的${moduleCName}实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(${moduleName}PO setParams, ${moduleName}Query whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改${moduleCName}，返回受影响的行数
     *
     * @param setParams 待修改的${moduleCName}实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(${moduleName}PO setParams, ${moduleName}Query whereParams) throws ServiceException {
        try {
            return ${_moduleName}DAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除${moduleCName}
     *
     * @param ${columnList[0].name} 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(${columnList[0].type} ${columnList[0].name}) throws ServiceException {
        try {
            ${moduleName}Query whereParams = new ${moduleName}Query();
            whereParams.setId(${columnList[0].name});
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除${moduleCName}
     *
     * @param ${columnList[0].name}s 编号数组
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(${columnList[0].type}[] ${columnList[0].name}s) throws ServiceException {
        try {
            if (CollectionUtils.isNotEmpty(${columnList[0].name}s)) {
                int i = 0;
                int length = ${columnList[0].name}s.length;
                ${moduleName}Query[] whereParams = new ${moduleName}Query[length];
                for (; i < length; i++) {
                    whereParams[i] = new ${moduleName}Query();
                    whereParams[i].setId(${columnList[0].name}s[i]);
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
     * 根据查询条件，删除${moduleCName}
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(${moduleName}Query whereParams) throws ServiceException {
        try {
            int result = ${_moduleName}DAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除${moduleCName}
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(${moduleName}Query[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计${moduleCName}的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(${moduleName}Query whereParams) throws ServiceException {
        try {
            return ${_moduleName}DAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询${moduleCName}对象
     *
     * @param ${columnList[0].name} 编号
     * @return ${moduleCName}对象
     * @throws ServiceException 业务异常
     */
     @Override
     public ${moduleName}PO get(${columnList[0].type} ${columnList[0].name}) throws ServiceException {
        try {
            ${moduleName}Query whereParams = new ${moduleName}Query();
            whereParams.setId(${columnList[0].name});
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询${moduleCName}
     *
     * @param whereParams 查询条件
     * @return ${moduleCName}对象
     * @throws ServiceException 业务异常
     */
     @Override
     public ${moduleName}PO get(${moduleName}Query whereParams) throws ServiceException {
        try {
            return ${_moduleName}DAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询${moduleCName}
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return ${moduleCName}对象
     * @throws ServiceException 业务异常
     */
     @Override
     public ${moduleName}PO get(${moduleName}Query whereParams, String[] selectFields) throws ServiceException {
        try {
            return ${_moduleName}DAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @return ${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<${moduleName}PO> list(${moduleName}Query whereParams) throws ServiceException {
        try {
            return ${_moduleName}DAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return ${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<${moduleName}PO> list(${moduleName}Query whereParams, String[] selectFields) throws ServiceException {
        try {
            return ${_moduleName}DAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return ${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<${moduleName}PO> listPaging(${moduleName}Query whereParams, PageVO pageParams) throws ServiceException {
        try {
            return ${_moduleName}DAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<${moduleName}PO> pages(${moduleName}Query whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<${moduleName}PO> pageData = new PageRecords<>();
        try {
            int total = ${_moduleName}DAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(${_moduleName}DAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<${moduleName}PO> pages(${moduleName}Query whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<${moduleName}PO> pageData = new PageRecords<>();
        try {
            int total = ${_moduleName}DAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(${_moduleName}DAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }
}