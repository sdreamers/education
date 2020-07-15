package com.sixing.education.device.service.impl;

import com.google.common.collect.Lists;
import com.sixing.base.dict.device.DeviceStatusEnum;
import com.sixing.base.dict.device.DeviceTypeEnum;
import com.sixing.base.dict.device.InProgressStatusEnum;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.device.*;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.domain.packetschool.PacketSchoolPO;
import com.sixing.base.domain.packetschool.PacketSchoolQuery;
import com.sixing.base.domain.school.SchoolPO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.supplier.SupplierPO;
import com.sixing.base.domain.supplier.SupplierQuery;
import com.sixing.base.utils.BeanUtils;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.StringUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.device.dao.DeviceDAO;
import com.sixing.education.packet.service.PacketService;
import com.sixing.education.packetschool.service.PacketSchoolService;
import com.sixing.education.school.service.SchoolService;
import com.sixing.education.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备的业务实现类
 *
 * @author
 * @date 2020年6月20日
 */
@Service("deviceBaseService")
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceDAO deviceDAO;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private PacketService packetService;

    @Autowired
    private PacketSchoolService packetSchoolService;

    @Autowired
    private SupplierService supplierService;

    /**
     * 新增设备
     *
     * @param insertParams 待新增的设备实例
     * @return 装填了自增型主键的设备对象
     * @throws ServiceException 业务异常
     */
    @Override
    public DevicePO insert(DevicePO insertParams) throws ServiceException {
        try {
            deviceDAO.insert(insertParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return insertParams;
    }

    /**
     * 根据ID修改，返回修改结果
     *
     * @param setParams 待修改的设备实例
     * @param id        编号
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    @Override
    public Boolean update(DevicePO setParams, Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.update(setParams, whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 修改设备，返回修改结果
     *
     * @param setParams   待修改的设备实例
     * @param whereParams 查询条件
     * @return 修改结果
     * @throws ServiceException 业务异常
     */
    @Override
    public Boolean update(DevicePO setParams, DeviceQuery whereParams) throws ServiceException {
        try {
            return this.updateAndRtnRows(setParams, whereParams) >= 1;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 修改设备，返回受影响的行数
     *
     * @param setParams   待修改的设备实例
     * @param whereParams 查询条件
     * @return 受影响的行数
     * @throws ServiceException 业务异常
     */
    @Override
    public Integer updateAndRtnRows(DevicePO setParams, DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.update(setParams, whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据ID删除设备
     *
     * @param id 编号
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    @Override
    public Boolean delete(Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.delete(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据ID数组，批量删除设备
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
                DeviceQuery[] whereParams = new DeviceQuery[length];
                for (; i < length; i++) {
                    whereParams[i] = new DeviceQuery();
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
     * 根据查询条件，删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果
     * @throws ServiceException 业务异常
     */
    @Override
    public Boolean delete(DeviceQuery whereParams) throws ServiceException {
        try {
            int result = deviceDAO.delete(whereParams);
            return result >= 1;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据查询条件数组，批量删除设备
     *
     * @param whereParams 查询条件
     * @return 删除结果数组
     * @throws ServiceException 业务异常
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public Boolean[] delete(DeviceQuery[] whereParams) throws ServiceException {
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
     * 根据查询条件，统计设备的数量
     *
     * @param whereParams 查询条件
     * @return 统计结果
     * @throws ServiceException 业务异常
     */
    @Override
    public int count(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.count(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据编号查询设备对象
     *
     * @param id 编号
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    @Override
    public DevicePO get(Long id) throws ServiceException {
        try {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setId(id);
            return this.get(whereParams);
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件查询设备
     *
     * @param whereParams 查询条件
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    @Override
    public DevicePO get(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.get(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件查询设备
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象
     * @throws ServiceException 业务异常
     */
    @Override
    public DevicePO get(DeviceQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return deviceDAO.select(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    @Override
    public List<DevicePO> list(DeviceQuery whereParams) throws ServiceException {
        try {
            return deviceDAO.list(whereParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams  查询条件
     * @param selectFields 筛查出来的字段
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    @Override
    public List<DevicePO> list(DeviceQuery whereParams, String[] selectFields) throws ServiceException {
        try {
            return deviceDAO.selects(whereParams, selectFields);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 设备对象列表
     * @throws ServiceException 业务异常
     */
    @Override
    public List<DevicePO> listPaging(DeviceQuery whereParams, PageVO pageParams) throws ServiceException {
        try {
            return deviceDAO.listPaging(whereParams, pageParams);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams 查询条件
     * @param pageParams  分页实例
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
    @Override
    public PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams) throws ServiceException {
        PageRecords<DevicePO> pageData = new PageRecords<>();
        try {
            int total = deviceDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(deviceDAO.listPaging(whereParams, pageParams));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
    }

    /**
     * 根据条件分页查询设备列表
     *
     * @param whereParams  查询条件
     * @param pageParams   分页实例
     * @param selectFields 筛查出来的字段
     * @return 封装了分页的设备对象列表
     * @throws ServiceException 业务异常
     */
    @Override
    public PageRecords<DevicePO> pages(DeviceQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException {
        PageRecords<DevicePO> pageData = new PageRecords<>();
        try {
            int total = deviceDAO.count(whereParams);
            if (total >= 1) {
                pageData.setRecords(deviceDAO.selectsPaging(whereParams, pageParams, selectFields));
            }
            pageData.setTotal(total);
        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
        return pageData;
    }

    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public void importDevice(List<ImportDeviceVO> devices, String packetName, Integer currentYear, String supplierName) throws ServiceException {
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }
        // 供应商
        Long supplierId = this.saveSupplier(supplierName);
        // 包
        PacketPO packet = this.savePacket(packetName, supplierId, supplierName);
        // 学校
        List<SchoolPO> schools = this.saveSchool(devices.stream().map(ImportDeviceVO::getSchoolName).distinct().toArray(String[]::new));
        // 包-学校中间表
        this.savePacketSchool(packet, schools, currentYear);
        // 保存设备
        this.saveDevice(devices, packet, schools);
    }

    @Override
    public PageRecords<DeviceVO> pagesWithVO(DeviceQuery param, PageVO pageParam) throws ServiceException {
        PageRecords<DeviceVO> result = new PageRecords<>();
        PageRecords<DevicePO> pageRecords = this.pages(param, pageParam);
        if (pageRecords.getTotal() > 0) {
            result.setTotal(pageRecords.getTotal());
            result.setRecords(BeanUtils.copyProperties(pageRecords.getRecords(), DeviceVO.class));
            for (DeviceVO record : result.getRecords()) {
                record.setIncludingTaxPrice(record.getExcludingTaxPrice().add(record.getTax()));
                record.setTotalAmount(record.getIncludingTaxPrice().multiply(new BigDecimal(record.getNum().toString())));
            }
        }
        return result;
    }

    @Override
    public void update(Long id, Integer type, Integer status) throws ServiceException {
        DevicePO setParams = new DevicePO();
        switch (type) {
            case 1: setParams.setProduce(status); break;
            case 2: setParams.setArrival(status); break;
            case 3: setParams.setInstall(status); break;
            default: throw new ServiceException("参数异常");
        }
        this.update(setParams, id);
    }

    @Override
    public List<ExportDeviceVO> listExportDevices(Long packetId) throws ServiceException {
        DeviceQuery whereParams = new DeviceQuery();
        whereParams.setPacketId(packetId);
        List<DevicePO> list = this.list(whereParams);

        List<ExportDeviceVO> devices = BeanUtils.copyProperties(list, ExportDeviceVO.class);
        if (CollectionUtils.isNotEmpty(devices)) {
            devices.forEach(item -> item.setProduceStr(DeviceStatusEnum.findName(item.getProduce())));
            devices.forEach(item -> item.setArriveStr(DeviceStatusEnum.findName(item.getArrival())));
            devices.forEach(item -> item.setInstallStr(DeviceStatusEnum.findName(item.getInstall())));
        }
        return devices;
    }

    private void saveDevice(List<ImportDeviceVO> devices, PacketPO packet, List<SchoolPO> schools) throws ServiceException {
        Map<String, Long> schoolMap = new HashMap<>(schools.size());
        schools.forEach(item -> schoolMap.put(item.getName(), item.getId()));

        List<DevicePO> insertParams = BeanUtils.copyProperties(devices, DevicePO.class);
        for (DevicePO insertParam : insertParams) {
            insertParam.setPacketId(packet.getId());
            insertParam.setPacketName(packet.getName());
            insertParam.setSchoolId(schoolMap.get(insertParam.getSchoolName()));
            insertParam.setIncludingTaxPrice(insertParam.getExcludingTaxPrice().add(insertParam.getTax()));
            insertParam.setTotalAmount(insertParam.getIncludingTaxPrice().multiply(new BigDecimal(insertParam.getNum().toString())));
            this.insert(insertParam);
        }
    }

    private void savePacketSchool(PacketPO packet, List<SchoolPO> schools, Integer currentYear) throws ServiceException {
        if (packet == null || CollectionUtils.isEmpty(schools)) {
            throw new ServiceException("参数错误");
        }
        for (SchoolPO school : schools) {
            PacketSchoolQuery whereParams = new PacketSchoolQuery();
            whereParams.setSchoolId(school.getId());
            whereParams.setPacketId(packet.getId());
            int count = packetSchoolService.count(whereParams);
            if (count < 1) {
                PacketSchoolPO insertParams = new PacketSchoolPO();
                insertParams.setPacketId(packet.getId());
                insertParams.setPacketName(packet.getName());
                insertParams.setSchoolId(school.getId());
                insertParams.setSchoolName(school.getName());
                insertParams.setCurrentYear(currentYear.toString());
                packetSchoolService.insert(insertParams);
            }
        }
    }

    private Long saveSupplier(String supplierName) throws ServiceException {
        if (StringUtils.isBlank(supplierName)) {
            throw new ServiceException("供应商名称为空");
        }
        SupplierQuery whereParams = new SupplierQuery();
        whereParams.setName(supplierName);
        SupplierPO supplier = supplierService.get(whereParams);
        if (supplier == null) {
            SupplierPO insertParams = new SupplierPO();
            insertParams.setName(supplierName);
            supplierService.insert(insertParams);
            return insertParams.getId();
        }
        return supplier.getId();
    }

    private List<SchoolPO> saveSchool(String[] schoolNames) throws ServiceException {
        if (CollectionUtils.isEmpty(schoolNames)) {
            throw new ServiceException("学校不能为空");
        }
        List<SchoolPO> schools = Lists.newArrayList();

        SchoolQuery whereParams = new SchoolQuery();
        whereParams.setSchools(schoolNames);
        List<SchoolPO> existSchools = schoolService.list(whereParams, new String[]{"name", "id"});
        List<String> existSchoolNames = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(existSchools)) {
            existSchoolNames = existSchools.stream().map(SchoolPO::getName).collect(Collectors.toList());
            schools.addAll(existSchools);
        }
        for (String school : schoolNames) {
            if (!existSchoolNames.contains(school)) {
                SchoolPO insertParams = new SchoolPO();
                insertParams.setName(school);
                schoolService.insert(insertParams);
                schools.add(insertParams);
            }
        }
        return schools;
    }

    private PacketPO savePacket(String packetName, Long supplierId, String supplierName) throws ServiceException {
        if (StringUtils.isBlank(packetName)) {
            throw new ServiceException("包名不能为空");
        }
        PacketQuery whereParams = new PacketQuery();
        whereParams.setName(packetName);
        PacketPO packet = packetService.get(whereParams);
        if (packet == null) {
            PacketPO insertParams = new PacketPO();
            insertParams.setName(packetName);
            insertParams.setCreateTime(new Date());
            insertParams.setSupplierId(supplierId);
            insertParams.setSupplierName(supplierName);
            packetService.insert(insertParams);
            return insertParams;
        }
        return packet;
    }
}