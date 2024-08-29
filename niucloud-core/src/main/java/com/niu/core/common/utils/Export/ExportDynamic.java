package com.niu.core.common.utils.Export;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 导出Excel的数据实体类
 */
@Data
@Accessors(chain = true)
public class ExportDynamic {
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件全路径
     */
    private String filePath;
    /**
     * 表格标题
     */
    private String tableTitle;
    /**
     * 表头数据
     */
    List<ExportHeads> heads =new ArrayList<>();
    /**
     * 表数据
     */
    List<List<Object>> datas =new ArrayList<>();
}
