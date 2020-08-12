package com.sixing.education.school.controller;

import com.sixing.base.domain.base.HartsResult;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.school.ExportSchoolProgressVO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.school.SchoolVO;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.school.service.SchoolService;
import com.sixing.education.school.utils.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @GetMapping("/exportSchoolProgress")
    public void exportSchoolProgress(SchoolQuery param, HttpServletResponse response) throws Exception {
        List<ExportSchoolProgressVO> schools = schoolService.listExportProgress(param);
        if (CollectionUtils.isNotEmpty(schools)) {
            String filename = new String("学校进度.xls".getBytes(), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream out = response.getOutputStream();
            ExcelUtils.export(out, schools);
            out.close();
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
