package com.sixing.education.device.controller;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.device.*;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.StringUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.device.utils.ExcelUtils;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import com.sixing.education.packet.controller.PacketController;
import com.sixing.education.packet.service.PacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Service;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 设备控制器
 *
 * @author sixing
 */
@RestController
@RequestMapping("/device")
@CrossOrigin
public class DeviceController {

    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private PacketService packetService;

    @GetMapping("/pages")
    public PageRecords<DeviceVO> pages(DeviceQuery param, PageVO pageParam) {
        try {
            return deviceService.pagesWithVO(param, pageParam);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new PageRecords<>();
        }
    }

    @PutMapping("/completeNum")
    public ResultModel<Void> updateCompleteNum(Integer completeNum, Integer type, Long id) {
        try {
            deviceService.update(id, type, completeNum);
            return ResultModel.ok();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultModel.fail(e.getMessage());
        }
    }

    @GetMapping("/export")
    public void export(@RequestParam Long packetId, HttpServletResponse response) throws Exception {
        if (packetId == null) {
            return;
        }
        PacketPO packet = packetService.get(packetId);
        if (packet == null) {
            return;
        }
        List<ExportDeviceVO> devices = deviceService.listExportDevices(packetId);
        if (CollectionUtils.isNotEmpty(devices)) {
            String filename = new String((packet.getName() + "_" + packet.getSupplierName() + ".xls").getBytes(), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream out = response.getOutputStream();
            ExcelUtils.export(out, devices);
            out.close();
        }
    }

    @PostMapping("/import")
    public ResultModel<Void> deviceImport(@RequestParam MultipartFile excelFile) {
        try {
            if (excelFile == null || excelFile.isEmpty()) {
                return ResultModel.fail("请选择文件");
            }
            String fileName = excelFile.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return ResultModel.fail("文件名不能为空");
            }
            String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!Constant.XLS.equalsIgnoreCase(fileEnd) && !Constant.XLSX.equalsIgnoreCase(fileEnd)) {
                return ResultModel.fail("只能上传Excel文件，扩展名为.xls或.xlsx");
            }
            ImportDeviceBaseProperties properties = this.disassembleFileName(fileName);
            List<ImportDeviceVO> devices = ExcelUtils.read(excelFile.getInputStream());
            ResultModel<Void> resultModel = this.check(devices, fileName);
            if (resultModel.getCode() == Constant.RESPONSE_ERROR_CODE) {
                return resultModel;
            }
            deviceService.importDevice(devices, properties.getPacketName(), properties.getCurrentYear(), properties.getSupplierName());
            return ResultModel.ok();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("导入设备excel报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    private ImportDeviceBaseProperties disassembleFileName(String fileName) throws ServiceException {
        try {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            if (StringUtils.isBlank(fileName)) {
                throw new ServiceException("文件名不能为空");
            }
            String packetName = fileName.substring(0, fileName.indexOf("-"));
            if (StringUtils.isBlank(packetName)) {
                throw new ServiceException("包名不能为空");
            }
            String supplierName = fileName.substring(fileName.indexOf("-") + 1, fileName.lastIndexOf("-"));
            if (StringUtils.isBlank(supplierName)) {
                throw new ServiceException("供应商不能为空");
            }
            String currentYearStr = fileName.substring(fileName.lastIndexOf("-") + 1);
            Integer currentYear;
            try {
                if (StringUtils.isBlank(currentYearStr) || (currentYear = Integer.parseInt(currentYearStr.replaceAll(Constant.YEAR_CN, ""))) < 1) {
                    throw new ServiceException("年份错误");
                }
            } catch (Exception e) {
                logger.error("年份转换错误", e);
                throw new ServiceException("年份错误");
            }
            ImportDeviceBaseProperties properties = new ImportDeviceBaseProperties();
            properties.setPacketName(packetName);
            properties.setSupplierName(supplierName);
            properties.setCurrentYear(currentYear);
            return properties;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("导入设备excel, 文件名称解析错误");
            throw new ServiceException("文件名称错误, 请检查");
        }
    }

    private ResultModel<Void> check(List<ImportDeviceVO> devices, String fileName) {
        if (CollectionUtils.isEmpty(devices)) {
            return ResultModel.fail("excel数据不能为空");
        }
        for (int i = 0; i < devices.size(); i++) {
            int row = i + 1;
            ImportDeviceVO device = devices.get(i);
            if (device == null) {
                return ResultModel.fail("第" + row + "行数据为空, 请检查");
            }
            if (StringUtils.isBlank(device.getName())) {
                return ResultModel.fail("第" + row + "行设备名称为空, 请检查");
            }
            if (device.getNum() == null || device.getNum() < 1) {
                return ResultModel.fail("第" + row + "行数量为空, 请检查");
            }
            if (device.getExcludingTaxPrice() == null) {
                return ResultModel.fail("第" + row + "行不含税单价为空, 请检查");
            }
            if (device.getTax() == null) {
                return ResultModel.fail("第" + row + "行税金为空, 请检查");
            }
            if (device.getIncludingTaxPrice() == null) {
                return ResultModel.fail("第" + row + "行含税单价为空, 请检查");
            }
            if (device.getTotalAmount() == null) {
                return ResultModel.fail("第" + row + "行含税合价为空, 请检查");
            }
            if (StringUtils.isBlank(device.getSchoolName())) {
                return ResultModel.fail("第" + row + "行学校为空, 请检查");
            }
            device.setName(device.getName().trim());
            device.setSchoolName(device.getSchoolName().trim());
        }
        return ResultModel.ok();
    }
}
