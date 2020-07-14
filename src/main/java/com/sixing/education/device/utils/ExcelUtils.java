package com.sixing.education.device.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.sixing.base.domain.device.ImportDeviceVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
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

        EasyExcel.read(inputStream, ImportDeviceVO.class, new ExcelParseListener(devices)).doReadAll();
        //EasyExcelFactory.read(inputStream, new ExcelParseListener(devices)).read;
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
