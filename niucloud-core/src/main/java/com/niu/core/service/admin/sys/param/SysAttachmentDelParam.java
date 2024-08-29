package com.niu.core.service.admin.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 附件管理参数
*/
@Data
public class SysAttachmentDelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "att_ids参数缺失")
    private Integer[] attIds;

}
