package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysAttachmentCategory视图
 */
@Data
public class SysAttachmentCategoryInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer pid;  // 父级ID
    private String type;  // 文件管理类型（image,video）
    private String name;  // 分类名称
    private String enname;  // 分类目录
    private Integer sort;  // 排序

}
