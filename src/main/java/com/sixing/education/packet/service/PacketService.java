package com.sixing.education.packet.service;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
* 包的业务层接口
* @author 
* @date 2020年6月20日
*/
public interface PacketService {

    /**
    * 新增包
    *
    * @param insertParams 待新增的包实例
    * @return 装填了自增型主键的包对象
    * @throws ServiceException 业务异常
    */
    PacketPO insert(PacketPO insertParams) throws ServiceException;

    /**
    * 根据ID修改，返回修改结果
    *
    * @param setParams 待修改的包实例
    * @param id 编号
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(PacketPO setParams, Long id) throws ServiceException;

    /**
    * 修改包，返回修改结果
    *
    * @param setParams 待修改的包实例
    * @param whereParams 查询条件
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(PacketPO setParams, PacketQuery whereParams) throws ServiceException;

    /**
    * 修改包，返回受影响的行数
    *
    * @param setParams 待修改的包实例
    * @param whereParams 查询条件
    * @return 受影响的行数
    * @throws ServiceException 业务异常
    */
    Integer updateAndRtnRows(PacketPO setParams, PacketQuery whereParams) throws ServiceException;

    /**
    * 根据ID删除包
    *
    * @param id 编号
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(Long id) throws ServiceException;

    /**
    * 根据ID数组，批量删除包
    *
    * @param id 编号数组
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
    * 根据查询条件，删除包
    *
    * @param whereParams 查询条件
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(PacketQuery whereParams) throws ServiceException;

    /**
    * 根据查询条件数组，批量删除包
    *
    * @param whereParams 查询条件
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(PacketQuery[] whereParams) throws ServiceException;

    /**
    * 根据查询条件，统计包的数量
    *
    * @param whereParams 查询条件
    * @return 统计结果
    * @throws ServiceException 业务异常
    */
    int count(PacketQuery whereParams) throws ServiceException;

    /**
    * 根据编号查询包对象
    *
    * @param id 编号
    * @return 包对象
    * @throws ServiceException 业务异常
    */
    PacketPO get(Long id) throws ServiceException;

    /**
    * 根据条件查询包
    *
    * @param whereParams 查询条件
    * @return 包对象
    * @throws ServiceException 业务异常
    */
    PacketPO get(PacketQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询包
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 包对象
    * @throws ServiceException 业务异常
    */
    PacketPO get(PacketQuery whereParams, String[] selectFields) throws ServiceException;

    /**
    * 根据条件查询包列表
    *
    * @param whereParams 查询条件
    * @return 包对象列表
    * @throws ServiceException 业务异常
    */
    List<PacketPO> list(PacketQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询包列表
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 包对象列表
    * @throws ServiceException 业务异常
    */
    List<PacketPO> list(PacketQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询包列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 包对象列表
     * @throws ServiceException 业务异常
     */
     List<PacketPO> listPaging(PacketQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询包列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @return 封装了分页的包对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<PacketPO> pages(PacketQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询包列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @param selectFields 筛查出来的字段
    * @return 封装了分页的包对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<PacketPO> pages(PacketQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;
}