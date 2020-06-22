package com.sixing.education.packet.controller;

import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketQuery;
import com.sixing.base.domain.packet.PacketVO;
import com.sixing.education.packet.service.PacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 包控制器
 *
 * @author sixing
 * */
@RestController
@RequestMapping("/packet")
public class PacketController {

    @Autowired
    private PacketService packetService;

    @RequestMapping("/pages")
    public PageRecords<PacketVO> pages(PacketQuery param, PageVO pageParam) {
        try {
            return packetService.listPacketPaging(param, pageParam);
        } catch (Exception e) {
            return new PageRecords<>();
        }
    }
}
