package com.sixing.education.school.controller;

import com.sixing.base.domain.base.HartsResult;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.school.SchoolVO;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.school.service.SchoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学校控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/school")
public class SchoolController {

    private static Logger logger = LoggerFactory.getLogger(SchoolController.class);

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/progressPages")
    public PageRecords<SchoolVO> progressPages(SchoolQuery param, PageVO pageParam) {
        try {
            return schoolService.progressPages(param, pageParam);
        } catch (Exception e) {
            return new PageRecords<>();
        }
    }

    @GetMapping("/overallProgress")
    public HartsResult overallProgress(SchoolQuery param) {
        try {
            return schoolService.overallProgress(param);
        } catch (ServiceException e) {
            logger.error("获取包任务整体进度报错", e);
            return HartsResult.error(e.getMessage());
        }
    }
}
