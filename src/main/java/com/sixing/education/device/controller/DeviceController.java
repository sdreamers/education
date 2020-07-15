package com.sixing.education.device.controller;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.device.*;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.StringUtils;
import com.sixing.education.device.utils.ExcelUtils;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import com.sixing.education.packet.controller.PacketController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 设备控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/device")
@CrossOrigin
public class DeviceController {

    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @GetMapping("/pages")
    public PageRecords<DeviceVO> pages(DeviceQuery param, PageVO pageParam) {
        try {
            return deviceService.pagesWithVO(param, pageParam);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new PageRecords<>();
        }
    }

    @PutMapping("/status")
    public ResultModel<Void> updateStatus(Integer status, Long id) {
        try {
            deviceService.update(id, status);
            return ResultModel.ok();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultModel.fail("系统异常");
        }
    }

    @GetMapping("/export")
    public void export(@RequestParam Long packetId, HttpServletResponse response) {
        ServletOutputStream outputStream = response.getOutputStream();
        List<ExportDeviceVO> devices = deviceService.listExportDeivces(packetId);
        ExcelUtils.export(response.getOutputStream(), devices);
    }

    @PostMapping("/import")
    public ResultModel<Void> deviceImport(@RequestParam MultipartFile excelFile, String packet, Integer currentYear, Integer type, String supplierName) {
        try {
            if (excelFile == null || excelFile.isEmpty()) {
                return ResultModel.fail("请选择文件");
            }
            String fileName = excelFile.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return ResultModel.fail("文件名不能为空");
            }
            String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!Constant.EXCEL_END.equalsIgnoreCase(fileEnd)) {
                return ResultModel.fail("只能上传Excel文件，扩展名为.xls");
            }
            List<ImportDeviceVO> devices = ExcelUtils.read(excelFile.getInputStream());
            ResultModel<Void> resultModel = this.check(devices, packet, currentYear, supplierName);
            if (resultModel.getCode() == Constant.RESPONSE_ERROR_CODE) {
                return resultModel;
            }
            packet = packet.trim();
            deviceService.importDevice(devices, packet, currentYear, type, supplierName);
            return ResultModel.ok();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("导入设备excel报错", e);
            return ResultModel.fail("系统异常, 请联系管理员");
        }
    }

    private ResultModel<Void> check(List<ImportDeviceVO> devices, String packet, Integer currentYear, String supplierName) {
        if (CollectionUtils.isEmpty(devices)) {
            return ResultModel.fail("excel数据不能为空");
        }
        if (StringUtils.isBlank(packet)) {
            return ResultModel.fail("包名不能为空");
        }
        if (currentYear == null || currentYear < 1) {
            return ResultModel.fail("请输入正确的年份");
        }
        if (StringUtils.isBlank(supplierName)) {
            return ResultModel.fail("请输入供应商");
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
            if (StringUtils.isBlank(device.getSchoolName())) {
                return ResultModel.fail("第" + row + "行学校为空, 请检查");
            }
            device.setName(device.getName().trim());
            device.setSchoolName(device.getSchoolName().trim());
        }
        return ResultModel.ok();
    }
}
