package com.sixing.education.user.controller;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.base.ResultModel;
import com.sixing.base.domain.user.UserPO;
import com.sixing.base.domain.user.UserQuery;
import com.sixing.base.domain.user.UserVO;
import com.sixing.base.security.utils.TokenManager;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.device.controller.DeviceController;
import com.sixing.education.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
