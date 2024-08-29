package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 附件分类参数
*/
@Data
public class SysAttachmentCategoryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer pid;

    @NotNull(message = "type参数缺失")
    @NotBlank(message = "type参数不能为空")
    private String type;

    @NotNull(message = "name参数缺失")
    @NotBlank(message = "name参数不能为空")
    private String name;

    private String enname;

    private Integer sort;

}
