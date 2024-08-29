package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysAttachment列表视图
 */
@Data
public class SysAttachmentListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer attId;  // 
    private String realName;  // 原始文件名
    private String path;  // 完整地址
    private String attType;  // 附件类型image,video
    private String url;  // 网络地址
    private String thumb;
}
