package com.sixing.education.user.service;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.domain.user.UserQuery;
import com.sixing.base.domain.user.UserVO;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
 * 系统用户的业务层接口
 *
 * @author
 * @date 2020年7月14日
 */
public interface UserService {

    /**
     * 新增系统用户
     *
     * @param insertParams 待新增的系统用户实例
     * @return 装填了自增型主键的系统用户对象
     * @throws ServiceException 业务异常
     */
    UserPO insert(UserPO insertParams) throws ServiceException;

    /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的系统用户实例
     * @param id        编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    Boolean update(UserPO setParams, Long id) throws ServiceException;

    /**
     * 修改系统用户，返回修改结果
     *
     * @param setParams   待修改的系统用户实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    Boolean update(UserPO setParams, UserQuery whereParams) throws ServiceException;

    /**
     * 修改系统用户，返回受影响的行数
     *
     * @param setParams   待修改的系统用户实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
    Integer updateAndRtnRows(UserPO setParams, UserQuery whereParams) throws ServiceException;

    /**
     * 根据ID删除系统用户
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    Boolean delete(Long id) throws ServiceException;

    /**
     * 根据ID数组，批量删除系统用户
     *
     * @param id 编号数组
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
     * 根据查询条件，删除系统用户
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    Boolean delete(UserQuery whereParams) throws ServiceException;

    /**
     * 根据查询条件数组，批量删除系统用户
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
    Boolean[] delete(UserQuery[] whereParams) throws ServiceException;

    /**
     * 根据查询条件，统计系统用户的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
    int count(UserQuery whereParams) throws ServiceException;

    /**
     * 根据编号查询系统用户对象
     *
     * @param id 编号
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
    UserPO get(Long id) throws ServiceException;

    /**
     * 根据条件查询系统用户
     *
     * @param whereParams 查询条件
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
    UserPO get(UserQuery whereParams) throws ServiceException;

    /**
     * 根据条件查询系统用户
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 系统用户对象
     * @throws ServiceException 业务异常
     */
    UserPO get(UserQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams 查询条件
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
    List<UserPO> list(UserQuery whereParams) throws ServiceException;

    /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
    List<UserPO> list(UserQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 系统用户对象列表
     * @throws ServiceException 业务异常
     */
    List<UserPO> listPaging(UserQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
     * 根据条件分页查询系统用户列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 封装了分页的系统用户对象列表
     * @throws ServiceException 业务异常
     */
    PageRecords<UserPO> pages(UserQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
     * 根据条件分页查询系统用户列表
     *
     * @param whereParams  查询条件
     * @param pageParams   分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的系统用户对象列表
     * @throws ServiceException 业务异常
     */
    PageRecords<UserPO> pages(UserQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;

    /**
     * 获取用户
     *
     * @param name 姓名
     * @return 用户
     * @throws ServiceException 业务异常
     */
    UserPO getByName(String name) throws ServiceException;

    /**
     * 获取用户
     *
     * @param userId 用户ID
     * @return 用户
     * @throws ServiceException 业务异常
     */
    UserVO getUser(Long userId) throws ServiceException;
}