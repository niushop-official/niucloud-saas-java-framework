package com.niu.core.common.utils.Export;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.entity.sys.SysExport;
import com.niu.core.mapper.sys.SysExportMapper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BusinessExcelUtil {

    /**
     * @param exportDynamic 参数实体类
     * @Description: 动态导出excel
     * @return: void
     */
    public static void exportBusinessExcel(Integer exportId, ExportDynamic exportDynamic) {
        // 文件名称不能为空
        if (!ObjectUtils.isEmpty(exportDynamic.getFileName())) {
            List<List<String>> head = new ArrayList<>();
            // 设置文件名称和文件路径
            String filePath = exportDynamic.getFilePath();
            FileTools.createDirs(filePath);

            String fileName = filePath + exportDynamic.getFileName();
            // 设置动态表头
            if (!CollectionUtils.isEmpty(exportDynamic.getHeads())) {
                // 处理查询条件以及表格的标题
                head.addAll(formatHead(head(exportDynamic.getHeads()), exportDynamic.getTableTitle()));
            }

            // 导出excel
            ThreadPoolManager.runTask(() -> {
                SysExportMapper mapper = SpringContext.bean(SysExportMapper.class);
                SysExport export = mapper.selectById(exportId);
                if (export == null) {
                    return;
                }

                export.setExportStatus(2);
                export.setFilePath(exportDynamic.getRelativePath() + exportDynamic.getFileName());
                try {
                    EasyExcel.write(fileName)
                            .head(head)
//                    .relativeHeadRowIndex(1) 表格离上方多少行
                            .registerWriteHandler(new SimpleColumnWidthStyleStrategy(ExcelConstant.DEFAULT_COLUMN_WIDTH))
                            .registerWriteHandler(setterTableHead())
                            .registerWriteHandler(new CustomCellWriteHandler())
                            .sheet(ExcelConstant.DEFAULT_SHEET_NAME)
                            .doWrite(exportDynamic.getDatas());
                } catch (Exception e) {
                    export.setExportStatus(-1);
                    export.setFailReason(e.getMessage());
                }

                if (export.getExportStatus() == 2) {
                    export.setFileSize(FileUtil.size(new File(fileName)) / 1024 + "");
                }

                mapper.updateById(export);
            });
        }
    }


    /**
     * 设置表格策略
     *
     * @return 表格策略
     */
    private static HorizontalCellStyleStrategy setterTableHead() {
        // 设置样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为颜色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 12);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 12);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }


    /**
     * @Description: 处理表格标题和查询条件
     * @Date: 2024/4/28 16:29
     * @Param heads: 表头数据
     * @Param title: 表头内容
     * @Param queryCriteria: 查询条件
     * @return: java.util.List<java.util.List < java.lang.String>>
     */
    private static List<List<String>> formatHead(List<List<String>> heads, String title) {
        if (ObjectUtil.hasEmpty(heads, title)) {
            return heads;
        }

        List<List<String>> datas = new ArrayList<>();
        if (!CollectionUtils.isEmpty(heads)) {
            for (int i = 0; i < heads.size(); i++) {
                heads.get(i).add(0, title);
            }
            datas.addAll(heads);
        }
        return datas;
    }

    /**
     * 处理表格表头数据
     *
     * @param heads 表头数据
     * @return 结果
     */
    private static List<List<String>> head(List<ExportHeads> heads) {
        List<List<String>> list = ListUtils.newArrayList();
        // 新增一级到数组
        for (ExportHeads head : heads) {
            List<String> datas = new ArrayList<>();
            datas.add(head.getLabel());
            // 判断子集
            recursion(list, datas, head.getChildren(), null);
        }
        // 先将一级新增倒数组
        return list;
    }

    /**
     * 递归处理数据
     *
     * @param list  需要新增的数组
     * @param heads 当前数据
     */
    private static void recursion(List<List<String>> lists, List<String> list, List<ExportHeads> children, ExportHeads heads) {
        if (!CollectionUtils.isEmpty(children)) {
            for (ExportHeads child : children) {
                List<String> listOne = new ArrayList<>();
                listOne.addAll(list);
                listOne.add(child.getLabel());
                recursion(lists, listOne, child.getChildren(), heads);
            }
        } else {
            lists.add(list);
        }
    }
}
