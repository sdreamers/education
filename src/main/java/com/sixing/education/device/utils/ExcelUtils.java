package com.sixing.education.device.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.sixing.base.domain.device.ImportDeviceVO;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * excel工具类
 *
 * @author sixing
 * */
public class ExcelUtils {


    public static List<ImportDeviceVO> read(InputStream inputStream) {
        List<ImportDeviceVO> devices = new ArrayList<>();
        EasyExcelFactory.read(inputStream, new ExcelParseListener(devices));
        return devices;
    }

    static class ExcelParseListener extends AnalysisEventListener<ImportDeviceVO> {

        private List<ImportDeviceVO> devices;
        public ExcelParseListener(List<ImportDeviceVO> devices) {
            this.devices = devices;
        }

        @Override
        public void invoke(ImportDeviceVO device, AnalysisContext analysisContext) {
            ReadRowHolder holder = analysisContext.readRowHolder();
            Integer row = holder.getRowIndex();
            if (row != 0) {
                this.devices.add(device);
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {

        }
    }
}
