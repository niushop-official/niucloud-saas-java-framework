package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 附件分类参数
 */
@Data
public class SysAttachmentCategorySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String name;


}
