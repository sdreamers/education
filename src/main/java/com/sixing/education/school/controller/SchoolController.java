package com.sixing.education.school.controller;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.domain.packet.PacketVO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.school.SchoolVO;
import com.sixing.education.school.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学校控制器
 *
 * @author hz19104557
 * */
@RestController
@RequestMapping("/school")
public class SchoolController {

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

}
