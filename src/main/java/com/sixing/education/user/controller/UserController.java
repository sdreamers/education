package com.sixing.education.user.controller;

import com.qiniu.util.Md5;
import com.sixing.base.constant.Constant;
import com.sixing.base.constant.UserNatureConstant;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.domain.user.UserQuery;
import com.sixing.base.domain.user.UserVO;
import com.sixing.base.security.utils.TokenManager;
import com.sixing.base.utils.StringUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.device.controller.DeviceController;
import com.sixing.education.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/currentUser")
    public ResultModel<UserVO> currentUser() {
        try {
            Long userId = TokenManager.getUserId();
            return ResultModel.ok(userService.getUser(userId));
        } catch (Exception e) {
            logger.error("获取当前登录用户报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    @GetMapping("/pages")
    public PageRecords<UserVO> pages(UserQuery param, PageVO pageParam) {
        try {
            return userService.viewPages(param, pageParam);
        } catch (ServiceException e) {
            logger.error("获取用户分页报错", e);
            return new PageRecords<>();
        }
    }

    @PostMapping
    public ResultModel<Void> insert(UserPO user) {
        try {
            this.checkParam(user);
            user.setPassword(Constant.DEFAULT_PASSWORD_MD5);
            user.setStatus(1);
            userService.insert(user);
            return ResultModel.ok();
        } catch (ServiceException e) {
            logger.error("新增用户报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    @PutMapping
    public ResultModel<Void> update(UserPO user, Long id) {
        try {
            this.checkParam(user);
            if (id == null) {
                throw new IllegalArgumentException("id不能为空");
            }
            userService.update(user, id);
            return ResultModel.ok();
        } catch (ServiceException e) {
            logger.error("新增用户报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    @PutMapping("/status")
    public ResultModel<Void> updateStatus(Long id, Integer status) {
        try {
            if (id == null) {
                throw new ServiceException("用户id为空");
            }
            UserPO setParams = new UserPO();
            setParams.setStatus(status);
            userService.update(setParams, id);
            return ResultModel.ok();
        } catch (ServiceException e) {
            logger.error("禁用用户报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResultModel<Void> updatePassword(String oldPassword, String newPassword, String reNewPassword) {
        try {
            if (StringUtils.isBlank(oldPassword)) {
                throw new ServiceException("原密码不能为空");
            }
            if (StringUtils.isBlank(newPassword) || StringUtils.isBlank(reNewPassword)) {
                throw new ServiceException("新密码不能为空");
            }
            if (!newPassword.equals(reNewPassword)) {
                throw new ServiceException("两次输入的新密码不一致");
            }
            Long userId = TokenManager.getUserId();
            userService.updatePassword(oldPassword, newPassword, userId);
            return ResultModel.ok();
        } catch (ServiceException e) {
            logger.error("更新密码报错", e);
            return ResultModel.fail(e.getMessage());
        }
    }

    private void checkParam(UserPO user) {
        Assert.notNull(user, "新增参数错误");
        Assert.hasText(user.getName(), "请输入用户名");
        Assert.hasText(user.getAccount(), "请输入账号");
        Assert.hasText(user.getTelephone(), "请输入手机号码");
        Assert.notNull(user.getNature(), "请选择角色");
        if (UserNatureConstant.SUPPLIER.equals(user.getNature()) && user.getSupplierId() == null) {
            throw new IllegalArgumentException("请选择供应商");
        }
    }
}
