package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * SysAttachmentCategory列表视图
 */
@Data
public class SysAttachmentCategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /** 文件管理类型（image,video） */
    private String type;

    /** 分类名称 */
    private String name;


}
