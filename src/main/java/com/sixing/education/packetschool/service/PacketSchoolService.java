package com.sixing.education.packetschool.service;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packetschool.PacketSchoolPO;
import com.sixing.base.domain.packetschool.PacketSchoolQuery;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
* 包学校的业务层接口
* @author 
* @date 2020年6月20日
*/
public interface PacketSchoolService {

    /**
    * 新增包学校
    *
    * @param insertParams 待新增的包学校实例
    * @return 装填了自增型主键的包学校对象
    * @throws ServiceException 业务异常
    */
    PacketSchoolPO insert(PacketSchoolPO insertParams) throws ServiceException;

    /**
    * 根据ID修改，返回修改结果
    *
    * @param setParams 待修改的包学校实例
    * @param id 编号
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(PacketSchoolPO setParams, Long id) throws ServiceException;

    /**
    * 修改包学校，返回修改结果
    *
    * @param setParams 待修改的包学校实例
    * @param whereParams 查询条件
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(PacketSchoolPO setParams, PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 修改包学校，返回受影响的行数
    *
    * @param setParams 待修改的包学校实例
    * @param whereParams 查询条件
    * @return 受影响的行数
    * @throws ServiceException 业务异常
    */
    Integer updateAndRtnRows(PacketSchoolPO setParams, PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 根据ID删除包学校
    *
    * @param id 编号
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(Long id) throws ServiceException;

    /**
    * 根据ID数组，批量删除包学校
    *
    * @param id 编号数组
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
    * 根据查询条件，删除包学校
    *
    * @param whereParams 查询条件
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 根据查询条件数组，批量删除包学校
    *
    * @param whereParams 查询条件
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(PacketSchoolQuery[] whereParams) throws ServiceException;

    /**
    * 根据查询条件，统计包学校的数量
    *
    * @param whereParams 查询条件
    * @return 统计结果
    * @throws ServiceException 业务异常
    */
    int count(PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 根据编号查询包学校对象
    *
    * @param id 编号
    * @return 包学校对象
    * @throws ServiceException 业务异常
    */
    PacketSchoolPO get(Long id) throws ServiceException;

    /**
    * 根据条件查询包学校
    *
    * @param whereParams 查询条件
    * @return 包学校对象
    * @throws ServiceException 业务异常
    */
    PacketSchoolPO get(PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询包学校
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 包学校对象
    * @throws ServiceException 业务异常
    */
    PacketSchoolPO get(PacketSchoolQuery whereParams, String[] selectFields) throws ServiceException;

    /**
    * 根据条件查询包学校列表
    *
    * @param whereParams 查询条件
    * @return 包学校对象列表
    * @throws ServiceException 业务异常
    */
    List<PacketSchoolPO> list(PacketSchoolQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询包学校列表
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 包学校对象列表
    * @throws ServiceException 业务异常
    */
    List<PacketSchoolPO> list(PacketSchoolQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询包学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 包学校对象列表
     * @throws ServiceException 业务异常
     */
     List<PacketSchoolPO> listPaging(PacketSchoolQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询包学校列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @return 封装了分页的包学校对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<PacketSchoolPO> pages(PacketSchoolQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询包学校列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @param selectFields 筛查出来的字段
    * @return 封装了分页的包学校对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<PacketSchoolPO> pages(PacketSchoolQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;
}