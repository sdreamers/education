package com.sixing.education.device.controller;

import com.sixing.base.domain.base.ResultModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @PostMapping("/import")
    public ResultModel<Void> deviceImport(MultipartFile file) {
        if (file == null) {
            return ResultModel.fail("请选择文件");
        }
        
    }
}
