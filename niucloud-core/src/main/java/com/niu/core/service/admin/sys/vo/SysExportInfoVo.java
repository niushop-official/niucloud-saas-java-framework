package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysExport视图
 */
@Data
public class SysExportInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点ID
    private String exportKey;  // 主题关键字
    private Integer exportNum;  // 导出数据数量
    private String filePath;  // 文件存储路径
    private String fileSize;  // 文件大小
    private Integer exportStatus;  // 导出状态
    private String failReason;  // 失败原因

}
