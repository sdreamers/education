package com.sixing.base.utils;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.OutputStream;
import java.util.List;

/**
 * 利用开源组件easyexcel动态导出EXCEL文档
 *
 */
public class ExportExcelUtils {

	public static void exportExcel(OutputStream out, Class<? extends BaseRowModel> clazz, List<? extends BaseRowModel> data, String sheetName){
		ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
		Sheet sheet1 = new Sheet(1, 0, clazz);
		sheet1.setSheetName(sheetName);
		writer.write(data, sheet1);
		writer.finish();
	}

}