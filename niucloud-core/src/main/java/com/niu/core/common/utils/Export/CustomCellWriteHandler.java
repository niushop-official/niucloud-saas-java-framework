package com.niu.core.common.utils.Export;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.util.BooleanUtils;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.RowWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.RowWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

@Slf4j
public class CustomCellWriteHandler extends HorizontalCellStyleStrategy implements CellWriteHandler, RowWriteHandler {

    private static Integer INDEX;


    @Override
    public void afterRowDispose(RowWriteHandlerContext context) {
        WriteSheetHolder writeSheetHolder = context.getWriteSheetHolder();
        // 拿取数据判断查询条件有多少
        List<String> list = writeSheetHolder.getHead().get(0);
        // 拿取查询的数据的长度
        INDEX = (int) list.stream().filter(item -> item.contains(ExcelConstant.QUERY_PLACEHOLDER)).count();
    }

    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Cell cell = context.getCell();
        Row row = context.getRow();
        // 这里可以对cell进行任何操作
        log.info("第{}行，第{}列写入完成。", cell.getRowIndex(), cell.getColumnIndex());
        if (BooleanUtils.isTrue(context.getHead())) {
            setHeadCellStyle(cell, null, cell.getRowIndex());
            //设置表格第一行的高度
            if (cell.getRowIndex() == 0 && cell.getColumnIndex() == 0) {
                row.setHeightInPoints(ExcelConstant.TABLE_TITLE_HEIGHT);
            }
        } else {
            // 处理内容格式
            setContentCellStyle(cell, null, cell.getRowIndex());
        }
        context.getFirstCellData().setWriteCellStyle(null);
    }

    @Override
    protected void setHeadCellStyle(Cell headCell, Head head, Integer relativeRowIndex) {
        Workbook workbook = headCell.getSheet().getWorkbook();
        // 创建表头样式
        CellStyle headCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        // 大标题18 小标题12
        if (headCell.getRowIndex() == 0) {
            font.setFontHeightInPoints(ExcelConstant.MAX_HEIGHT_SIZE); // 设置字体大小
            headCellStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
        } else {
            font.setFontHeightInPoints(ExcelConstant.MIN_HEIGHT_SIZE); // 设置字体大小
            if (headCell.getRowIndex()>0 && headCell.getRowIndex() <= INDEX) {
                // 只有查询条件列一居左 列二居右
                if (headCell.getColumnIndex() == 0) {
                    headCellStyle.setAlignment(HorizontalAlignment.LEFT); // 居左
                } else {
                    headCellStyle.setAlignment(HorizontalAlignment.RIGHT); // 居右
                }
            } else if (headCell.getRowIndex() > INDEX) {
                headCellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
                // 设置边框
                headCellStyle.setBorderTop(BorderStyle.THIN);
                headCellStyle.setBorderLeft(BorderStyle.THIN);
                headCellStyle.setBorderBottom(BorderStyle.THIN);
                headCellStyle.setBorderRight(BorderStyle.THIN);
            }
        }
        font.setFontName(ExcelConstant.FONT_NAME); // 设置字体
        font.setBold(false); // 加粗
        headCellStyle.setFont(font);
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        // 应用样式
        headCell.setCellStyle(headCellStyle);
    }

    @Override
    protected void setContentCellStyle(Cell headCell, Head head, Integer relativeRowIndex) {
        Workbook workbook = headCell.getSheet().getWorkbook();
        // 创建表头样式
        CellStyle headCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();

        // 设置边框
        headCellStyle.setBorderTop(BorderStyle.THIN);
        headCellStyle.setBorderLeft(BorderStyle.THIN);
        headCellStyle.setBorderBottom(BorderStyle.THIN);
        headCellStyle.setBorderRight(BorderStyle.THIN);
        DataFormat dataFormat = workbook.createDataFormat();
        headCellStyle.setDataFormat(dataFormat.getFormat(ExcelConstant.NUMBER_FORMAT));
        font.setFontName(ExcelConstant.FONT_NAME); // 设置字体
        font.setFontHeightInPoints(ExcelConstant.CONTENT_SIZE); // 设置字体大小
        headCellStyle.setFont(font);
        headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
        // 应用样式
        headCell.setCellStyle(headCellStyle);
    }
}
