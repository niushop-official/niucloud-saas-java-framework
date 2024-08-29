package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysExport列表视图
 */
@Data
public class SysExportListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 主题关键字 */
    private String exportKey;

    /** 导出数据量 */
    private Integer exportNum;

    /** 文件存储路径 */
    private String filePath;

    /** 文件大小 */
    private String fileSize;

    /** 导出状态 */
    private Integer exportStatus;

    /** 失败原因 */
    private String failReason;

    /** 导出时间 */
    private String createTime;

    /** 状态字段名称 */
    private String exportStatusName;

    /** 关键字字段名称 */
    private String exportKeyName;

}
