package com.sixing.education.supplier.controller;

import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.supplier.SupplierPO;
import com.sixing.base.domain.supplier.SupplierQuery;
import com.sixing.education.supplier.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 供应商控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/supplier")
public class SupplierController {

    private static Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/list")
    public ResultModel<List<SupplierPO>> list(SupplierQuery param) {
        try {
            return ResultModel.ok(supplierService.list(param));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultModel.fail("系统错误");
        }
    }
}
