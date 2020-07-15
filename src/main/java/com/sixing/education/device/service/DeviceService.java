package com.dongpinyun.productmodule.shop.service;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.device.*;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
 * 设备的业务层接口
 *
 * @author
 * @date 2020年6月20日
 */
public interface DeviceService {

    /**
     * 新增设备
     *
     * @param insertParams 待新增的设备实例
     * @return 装填了自增型主键的设备对象
     * @throws ServiceException 业务异常
     */
    DevicePO insert(DevicePO insertParams) throws ServiceException;

    /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的设备实例
     * @param id        编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    Boolean update(DevicePO setParams, Long id) throws ServiceException;

    /**
     * 修改设备，返回修改结果
     *
     * @param setParams   待修改的设备实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    Boolean update(DevicePO setParams, DeviceQuery whereParams) throws ServiceException;

    /**
     * 修改设备，返回受影响的行数
     *
     * @param setParams   待修改的设备实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
    Integer updateAndRtnRows(DevicePO setParams, DeviceQuery whereParams) throws ServiceException;

    /**
     * 根据ID删除设备
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    Boolean delete(Long id) throws ServiceException;

    /**
     * 根据ID数组，批量删除设备
     *
     * @param id 编号数组
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
     * 根据查询条件，删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    Boolean delete(DeviceQuery whereParams) throws ServiceException;

    /**
     * 根据查询条件数组，批量删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
    Boolean[] delete(DeviceQuery[] whereParams) throws ServiceException;

    /**
     * 根据查询条件，统计设备的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
    int count(DeviceQuery whereParams) throws ServiceException;

    /**
     * 根据编号查询设备对象
     *
     * @param id 编号
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    DevicePO get(Long id) throws ServiceException;

    /**
     * 根据条件查询设备
     *
     * @param whereParams 查询条件
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    DevicePO get(DeviceQuery whereParams) throws ServiceException;

    /**
     * 根据条件查询设备
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    DevicePO get(DeviceQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    List<DevicePO> list(DeviceQuery whereParams) throws ServiceException;

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    List<DevicePO> list(DeviceQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    List<DevicePO> listPaging(DeviceQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
    PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams  查询条件
     * @param pageParams   分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
    PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;

    /**
     * 导入excel
     *
     * @param devices     解析生成的设备
     * @param packet      包
     * @param currentYear excel对应的年份
     * @param type        类型
     * @throws ServiceException
     */
    void importDevice(List<ImportDeviceVO> devices, String packet, Integer currentYear, String supplierName) throws ServiceException;

    /**
     * 分页查询
     *
     * @param param     查询条件
     * @param pageParam 分页参数
     * @return 结果
     * @throws ServiceException
     */
    PageRecords<DeviceVO> pagesWithVO(DeviceQuery param, PageVO pageParam) throws ServiceException;

    /**
     * 更新节点
     *
     * @param id 设备ID
     * @param status 状态
     * @throws ServiceException
     * */
    void update(Long id, Integer type, Integer status) throws ServiceException;

    /**
     * 获取设备导出实体
     *
     * @param packetId 包ID
     * @return 设备
     * */
    List<ExportDeviceVO> listExportDevices(Long packetId) throws ServiceException;
}