package com.sixing.education.user.dao;

import java.util.List;

import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.domain.user.UserQuery;
import org.apache.ibatis.annotations.Param;

/**
* 系统用户的持久层接口类
* @author 
* @date 2020年7月14日
*/
public interface UserDAO {

    /**
    * 新增系统用户
    * @param insertParams 待新增的系统用户实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") UserPO insertParams);

    /**
    * 修改系统用户
    * @param setParams 待修改的系统用户实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") UserPO setParams, @Param("whereParams") UserQuery whereParams);

    /**
    * 根据查询条件，删除系统用户
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") UserQuery whereParams);

    /**
    * 根据查询条件，统计系统用户的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") UserQuery whereParams);

    /**
    * 根据条件查询系统用户
    * @param whereParams 查询条件
    * @return 系统用户对象
    */
    UserPO get(@Param("whereParams") UserQuery whereParams);

    /**
    * 根据条件查询系统用户
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 系统用户对象
    */
    UserPO select(@Param("whereParams") UserQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询系统用户列表
    * @param whereParams 查询条件
    * @return 系统用户对象
    */
    List<UserPO> list(@Param("whereParams") UserQuery whereParams);

    /**
    * 根据条件查询系统用户列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return 系统用户对象
    */
    List<UserPO> selects(@Param("whereParams") UserQuery whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询系统用户列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return 系统用户对象列表
    */
    List<UserPO> listPaging(@Param("whereParams") UserQuery whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询系统用户列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return 系统用户对象列表
    */
    List<UserPO> selectsPaging(@Param("whereParams") UserQuery whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}