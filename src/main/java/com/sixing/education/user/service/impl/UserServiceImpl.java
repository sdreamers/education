package com.sixing.education.user.service.impl;

import java.util.List;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.domain.user.UserQuery;
import com.sixing.base.domain.user.UserVO;
import com.sixing.base.utils.BeanCopierUtil;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.user.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* 系统用户的业务实现类
* @author 
* @date 2020年7月14日
*/
@Service("userService")
public class UserServiceImpl implements com.sixing.education.user.service.UserService {

    @Autowired
    private UserDAO userDAO;

    /**
    * 新增系统用户
    *
    * @param insertParams 待新增的系统用户实例
    * @return 装填了自增型主键的系统用户对象
    * @throws ServiceException 业务异常 
    */
    @Override
    public UserPO insert(UserPO insertParams) throws ServiceException {
        try {
            userDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

     /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的系统用户实例
     * @param id 编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(UserPO setParams, Long id) throws ServiceException {
        try {
            UserQuery whereParams = new UserQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改系统用户，返回修改结果
     *
     * @param setParams 待修改的系统用户实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean update(UserPO setParams, UserQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 修改系统用户，返回受影响的行数
     *
     * @param setParams 待修改的系统用户实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
     @Override
     public Integer updateAndRtnRows(UserPO setParams, UserQuery whereParams) throws ServiceException {
        try {
            return userDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID删除系统用户
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(Long id) throws ServiceException {
        try {
            UserQuery whereParams = new UserQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据ID数组，批量删除系统用户
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
                UserQuery[] whereParams = new UserQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new UserQuery();
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
     * 根据查询条件，删除系统用户
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
     @Override
     public Boolean delete(UserQuery whereParams) throws ServiceException {
        try {
            int result = userDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据查询条件数组，批量删除系统用户
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
     @Override
     @Transactional(rollbackFor = ServiceException.class)
     public Boolean[] delete(UserQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计系统用户的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
     @Override
     public int count(UserQuery whereParams) throws ServiceException {
        try {
            return userDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据编号查询系统用户对象
     *
     * @param id 编号
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
     @Override
     public UserPO get(Long id) throws ServiceException {
        try {
            UserQuery whereParams = new UserQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询系统用户
     *
     * @param whereParams 查询条件
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
     @Override
     public UserPO get(UserQuery whereParams) throws ServiceException {
        try {
            return userDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询系统用户
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
     @Override
     public UserPO get(UserQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return userDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams 查询条件
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<UserPO> list(UserQuery whereParams) throws ServiceException {
        try {
            return userDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param selectFields 筛查出来的字段
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<UserPO> list(UserQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return userDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public List<UserPO> listPaging(UserQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return userDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
     }

     /**
     * 根据条件分页查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 封装了分页的系统用户对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<UserPO> pages(UserQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<UserPO> pageData = new PageRecords<>();
        try {
            int total = userDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(userDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

     /**
     * 根据条件分页查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的系统用户对象列表
     * @throws ServiceException 业务异常
     */
     @Override
     public PageRecords<UserPO> pages(UserQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<UserPO> pageData = new PageRecords<>();
        try {
            int total = userDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(userDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
     }

    @Override
    public UserPO getByName(String name) throws ServiceException{
        UserQuery whereParams = new UserQuery();
        whereParams.setAccount(name);
        return this.get(whereParams);
    }

    @Override
    public UserVO getUser(Long userId) throws ServiceException {
        UserPO user = this.get(userId);
        if (user == null) {
            return null;
        }
        UserVO userVO = BeanCopierUtil.copy(user, UserVO.class);
        return userVO;
    }
}