package com.sixing.education.packet.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.sixing.base.domain.base.HartsResult;
import com.sixing.base.domain.base.HartsValue;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.device.DevicePO;
import com.sixing.base.domain.device.DeviceQuery;
import com.sixing.base.domain.packet.ExportPacketProgressVO;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.domain.packet.PacketVO;
import com.sixing.base.domain.packetschool.PacketSchoolPO;
import com.sixing.base.domain.packetschool.PacketSchoolQuery;
import com.sixing.base.domain.school.ExportSchoolProgressVO;
import com.sixing.base.domain.school.SchoolPO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.school.SchoolVO;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.utils.BeanUtils;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.packet.dao.PacketDAO;
import com.sixing.education.packet.service.PacketService;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import com.sixing.education.packetschool.service.PacketSchoolService;
import com.sixing.education.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 包的业务实现类
 *
 * @author
 * @date 2020年6月20日
 */
@Service("packetBaseService")
public class PacketServiceImpl implements PacketService {

    @Autowired
    private PacketDAO packetDAO;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;

    @Autowired
    private PacketSchoolService packetSchoolService;

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
     * @param id        编号
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
     * @param setParams   待修改的包实例
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
     * @param setParams   待修改的包实例
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
     * @param whereParams  查询条件
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
     * @param whereParams  查询条件
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
     * @param pageParams  分页实例
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
     * @param pageParams  分页实例
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
     * @param whereParams  查询条件
     * @param pageParams   分页实例
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

    @Override
    public PageRecords<PacketVO> progressPages(PacketQuery param, PageVO pageParam) throws ServiceException {
        PageRecords<PacketVO> result = new PageRecords<>();

        if (param.getSchoolId() != null) {
            PacketSchoolQuery whereParams = new PacketSchoolQuery();
            whereParams.setSchoolId(param.getSchoolId());
            List<PacketSchoolPO> packetSchools = packetSchoolService.list(whereParams);
            if (CollectionUtils.isEmpty(packetSchools)) {
                return result;
            }
            param.setPacketIds(packetSchools.stream().map(PacketSchoolPO::getPacketId).toArray(Long[]::new));
        }

        PageRecords<PacketPO> pageRecords = this.pages(param, pageParam);
        if (pageRecords.getTotal() < 1) {
            return result;
        }
        result.setTotal(pageRecords.getTotal());
        result.setRecords(BeanUtils.copyProperties(pageRecords.getRecords(), PacketVO.class));
        for (PacketVO record : result.getRecords()) {
            setPacketProgress(record, param.getSchoolId());
        }
        return result;
    }

    private void setPacketProgress(PacketVO record, Long schoolId) throws ServiceException {
        DeviceQuery whereParams = new DeviceQuery();
        whereParams.setPacketId(record.getId());
        whereParams.setSchoolId(schoolId);
        List<DevicePO> devices = deviceService.list(whereParams);
        if (CollectionUtils.isNotEmpty(devices)) {
            int totalNum = devices.size();
            BigDecimal totalAmount = devices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            List<DevicePO> unStartDevices = devices.stream().filter(item -> item.getNum() - item.getProduceNum() > 0).collect(Collectors.toList());
            List<DevicePO> produceDevices = devices.stream().filter(item -> item.getNum() - item.getProduceNum() <= 0).collect(Collectors.toList());
            List<DevicePO> arrivalDevices = devices.stream().filter(item -> item.getNum() - item.getArrivalNum() <= 0).collect(Collectors.toList());
            List<DevicePO> installDevices = devices.stream().filter(item -> item.getNum() - item.getInstallNum() <= 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(unStartDevices)) {
                int completeNum = unStartDevices.size();
                BigDecimal completeAmount = unStartDevices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                record.setUnStartDeviceNumProgress(new BigDecimal(String.valueOf(completeNum)).divide(new BigDecimal(String.valueOf(totalNum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                record.setUnStartDeviceAmountProgress(completeAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            } else {
                record.setUnStartDeviceNumProgress(BigDecimal.ZERO);
                record.setUnStartDeviceAmountProgress(BigDecimal.ZERO);
            }

            if (CollectionUtils.isNotEmpty(produceDevices)) {
                int completeNum = produceDevices.size();
                BigDecimal completeAmount = produceDevices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                record.setProduceDeviceNumProgress(new BigDecimal(String.valueOf(completeNum)).divide(new BigDecimal(String.valueOf(totalNum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                record.setProduceDeviceAmountProgress(completeAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            } else {
                record.setProduceDeviceNumProgress(BigDecimal.ZERO);
                record.setProduceDeviceAmountProgress(BigDecimal.ZERO);
            }

            if (CollectionUtils.isNotEmpty(arrivalDevices)) {
                int completeNum = arrivalDevices.size();
                BigDecimal completeAmount = arrivalDevices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                record.setArrivalDeviceNumProgress(new BigDecimal(String.valueOf(completeNum)).divide(new BigDecimal(String.valueOf(totalNum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                record.setArrivalDeviceAmountProgress(completeAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            } else {
                record.setArrivalDeviceNumProgress(BigDecimal.ZERO);
                record.setArrivalDeviceAmountProgress(BigDecimal.ZERO);
            }

            if (CollectionUtils.isNotEmpty(installDevices)) {
                int completeNum = installDevices.size();
                BigDecimal completeAmount = installDevices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                record.setInstallDeviceNumProgress(new BigDecimal(String.valueOf(completeNum)).divide(new BigDecimal(String.valueOf(totalNum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
                record.setInstallDeviceAmountProgress(completeAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
            } else {
                record.setInstallDeviceNumProgress(BigDecimal.ZERO);
                record.setInstallDeviceAmountProgress(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public HartsResult overallProgress(PacketQuery param) throws ServiceException {

        List<PacketPO> list = this.list(param);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        HartsResult hartsResult = new HartsResult();

        List<PacketVO> packets = BeanUtils.copyProperties(list, PacketVO.class);
        hartsResult.setX(packets.stream().map(PacketVO::getName).collect(Collectors.toList()));
        List<BigDecimal> numData = new ArrayList<>();
        List<BigDecimal> amountData = new ArrayList<>();
        for (PacketVO record : packets) {
            DeviceQuery whereParams = new DeviceQuery();
            whereParams.setPacketId(record.getId());
            List<DevicePO> devices = deviceService.list(whereParams);
            if (CollectionUtils.isNotEmpty(devices)) {
                int totalNum = devices.size();
                BigDecimal totalAmount = devices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                List<DevicePO> installDevices = devices.stream().filter(item -> item.getNum() - item.getInstallNum() <= 0).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(installDevices)) {
                    int completeNum = installDevices.size();
                    BigDecimal completeAmount = installDevices.stream().map(DevicePO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    numData.add(new BigDecimal(String.valueOf(completeNum)).divide(new BigDecimal(String.valueOf(totalNum)), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                    amountData.add(completeAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)));
                }
            }
        }
        HartsValue numHartsValue = new HartsValue(1, numData);
        HartsValue amountHartsValue = new HartsValue(2, amountData);
        hartsResult.setData(Lists.newArrayList(numHartsValue, amountHartsValue));
        return hartsResult;
    }

    @Override
    public List<ExportPacketProgressVO> listExportProgress(PacketQuery param) throws ServiceException {
        final List<PacketPO> packets = this.list(param);
        if (CollectionUtils.isEmpty(packets)) {
            return null;
        }
        final List<PacketVO> packetVOS = BeanUtils.copyProperties(packets, PacketVO.class);
        List<ExportPacketProgressVO> result = new ArrayList<>();
        for (PacketVO packet : packetVOS) {
            this.setPacketProgress(packet, null);
            ExportPacketProgressVO progress = new ExportPacketProgressVO();
            progress.setName(packet.getName());
            progress.setUnStartDeviceAmountProgressStr(packet.getUnStartDeviceAmountProgress().toString() + "%");
            progress.setUnStartDeviceNumProgressStr(packet.getUnStartDeviceNumProgress().toString() + "%");
            progress.setProduceDeviceAmountProgressStr(packet.getProduceDeviceAmountProgress().toString() + "%");
            progress.setProduceDeviceNumProgressStr(packet.getProduceDeviceNumProgress().toString() + "%");
            progress.setArrivalDeviceAmountProgressStr(packet.getArrivalDeviceAmountProgress().toString() + "%");
            progress.setArrivalDeviceNumProgressStr(packet.getArrivalDeviceNumProgress().toString() + "%");
            progress.setInstallDeviceAmountProgressStr(packet.getInstallDeviceAmountProgress().toString() + "%");
            progress.setInstallDeviceNumProgressStr(packet.getInstallDeviceNumProgress().toString() + "%");
            result.add(progress);
        }
        return result;
    }
}