package com.sixing.education.packet.controller;

import com.sixing.base.domain.base.HartsResult;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.ExportPacketProgressVO;
import com.sixing.base.domain.packet.PacketPO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.domain.packet.PacketVO;
import com.sixing.base.utils.CollectionUtils;
import com.sixing.base.utils.exception.ServiceException;
import com.sixing.education.packet.service.PacketService;
import com.sixing.education.packet.utils.ExcelUtils;
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
 * 包控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/packet")
public class PacketController {

    private static Logger logger = LoggerFactory.getLogger(PacketController.class);

    @Autowired
    private PacketService packetService;

    @GetMapping("/pages")
    public PageRecords<PacketPO> pages(PacketQuery param, PageVO pageParam) {
        try {
            return packetService.pages(param, pageParam);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new PageRecords<>();
        }
    }

    @GetMapping("/progressPages")
    public PageRecords<PacketVO> progressPages(PacketQuery param, PageVO pageParam) {
        try {
            return packetService.progressPages(param, pageParam);
        } catch (Exception e) {
            return new PageRecords<>();
        }
    }

    @GetMapping("/exportProgress")
    public void exportProgress(PacketQuery param, HttpServletResponse response) throws Exception {
        List<ExportPacketProgressVO> packets = packetService.listExportProgress(param);
        if (CollectionUtils.isNotEmpty(packets)) {
            String filename = new String("包进度.xls".getBytes(), StandardCharsets.ISO_8859_1);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream out = response.getOutputStream();
            ExcelUtils.export(out, packets);
            out.close();
        }
    }

    @GetMapping("/overallProgress")
    public HartsResult overallProgress(PacketQuery param) {
        try {
            return packetService.overallProgress(param);
        } catch (ServiceException e) {
            logger.error("获取包任务整体进度报错", e);
            return HartsResult.error(e.getMessage());
        }
    }

    @GetMapping("/export")
    public void export() {
        System.out.println(1);
    }
}
