package com.sixing.education.packetschool.controller;

import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.packetschool.service.PacketSchoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 包学校关联控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/packetSchool")
public class PacketSchoolController {

    private static Logger logger = LoggerFactory.getLogger(PacketSchoolController.class);

    @Autowired
    private PacketSchoolService packetSchoolService;

    @GetMapping("/listYears")
    public ResultModel<List<String>> listYears() {
        try {
            return ResultModel.ok(packetSchoolService.listYears());
        } catch (ServiceException e) {
            logger.error("获取年份集合失败", e);
            return ResultModel.fail("获取年份失败");
        }
    }
}
