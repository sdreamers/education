package com.sixing.education.packet.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sixing.base.domain.packet.ExportPacketProgressVO;
import com.sixing.base.domain.school.ExportSchoolProgressVO;

import java.io.OutputStream;
import java.util.List;

/**
 * excel工具类
 *
 * @author sixing
 * */
public class ExcelUtils {

    public static void export(OutputStream outputStream, List<ExportPacketProgressVO> devices) {
        ExcelWriter excelWriter = EasyExcel.write(outputStream, ExportPacketProgressVO.class).build();
        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet sheet = EasyExcel.writerSheet("包进度").build();
        excelWriter.write(devices, sheet);
        excelWriter.finish();
    }
}
