package com.sixing.education.device.controller;

import com.sixing.base.constant.Constant;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.device.ImportDeviceVO;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.StringUtils;
import com.sixing.education.device.utils.ExcelUtils;
import com.dongpinyun.productmodule.shop.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/import")
    public ResultModel<Void> deviceImport(MultipartFile file, @RequestParam String packet, @RequestParam Integer currentYear, @RequestParam Integer type) {
        try {
            if (file == null || file.isEmpty()) {
                return ResultModel.fail("请选择文件");
            }
            String fileName = file.getOriginalFilename();
            if (StringUtils.isBlank(fileName)) {
                return ResultModel.fail("文件名不能为空");
            }
            String fileEnd = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            if (!Constant.EXCEL_END.equalsIgnoreCase(fileEnd)) {
               return ResultModel.fail("只能上传Excel文件，扩展名为.xls");
            }
            List<ImportDeviceVO> devices = ExcelUtils.read(file.getInputStream());
            ResultModel<Void> resultModel = this.check(devices, packet, currentYear);
            if (resultModel.getCode() == Constant.RESPONSE_ERROR_CODE) {
                return resultModel;
            }
            packet = packet.trim();
            deviceService.importDevice(devices, packet, currentYear, type);
            return ResultModel.ok();
        } catch (Exception e) {
            return ResultModel.fail("系统异常, 请联系管理员");
        }
    }

    private ResultModel<Void> check(List<ImportDeviceVO> devices, String packet, Integer currentYear) {
        if (CollectionUtils.isEmpty(devices)) {
            return ResultModel.fail("excel数据不能为空");
        }
        if (StringUtils.isBlank(packet)) {
            return ResultModel.fail("包名不能为空");
        }
        if (currentYear == null || currentYear < 1) {
            return ResultModel.fail("请输入正确的年份");
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
            if (StringUtils.isBlank(device.getSchool())) {
                return ResultModel.fail("第" + row + "行学校为空, 请检查");
            }
            device.setName(device.getName().trim());
            device.setSchool(device.getSchool().trim());
        }
        long supplierCount = devices.stream().map(ImportDeviceVO::getSupplier).map(String::trim).distinct().count();
        if (supplierCount > 1) {
            return ResultModel.fail("excel包含多个供应商, 请检查");
        }
        return ResultModel.ok();
    }
}
