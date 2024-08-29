package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 附件管理参数
*/
@Data
public class SysAttachmentParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "realName参数缺失")
    private String realName;

    @NotNull(message = "path参数缺失")
    private String path;

    @NotNull(message = "dir参数缺失")
    private String dir;

    @NotNull(message = "attSize参数缺失")
    private String attSize;

    @NotNull(message = "attType参数缺失")
    private String attType;

    @NotNull(message = "storageType参数缺失")
    private String storageType;

    @NotNull(message = "cateId参数缺失")
    private Integer cateId;

    @NotNull(message = "url参数缺失")
    private String url;

}
