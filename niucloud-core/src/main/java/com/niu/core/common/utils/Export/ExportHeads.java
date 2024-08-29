package com.niu.core.common.utils.Export;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 导出Exceld的表头数据
 *
 */
@Data
public class ExportHeads {

    /** 名称 */
    private String label;

    /** 子集 */
    private List<ExportHeads> children=new ArrayList<>();
}
