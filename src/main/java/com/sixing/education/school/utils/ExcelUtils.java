package com.sixing.education.school.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sixing.base.domain.device.ExportDeviceVO;
import com.sixing.base.domain.device.ImportDeviceVO;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * excel工具类
 *
 * @author sixing
 * */
public class ExcelUtils {

    public static void export(OutputStream outputStream, List<ExportDeviceVO> devices) {
        ExcelWriter excelWriter = EasyExcel.write(outputStream).head().build();
        // 这里注意 如果同一个sheet只要创建一次
        WriteSheet sheet = EasyExcel.writerSheet("设备明细").build();
        excelWriter.write(devices, sheet);
        excelWriter.finish();
    }

    private static List<List<String>> head() {

    }
}
