package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 附件管理参数
 */
@Data
public class SysAttachmentSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String realName;

    private String attType;

    private Integer cateId;


}
