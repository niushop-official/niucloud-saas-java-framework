package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 地址参数
*/
@Data
public class SysAreaParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "pid参数缺失")
    private Integer pid;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "shortname参数缺失")
    private String shortname;

    @NotNull(message = "longitude参数缺失")
    private String longitude;

    @NotNull(message = "latitude参数缺失")
    private String latitude;

    @NotNull(message = "level参数缺失")
    private Integer level;

    @NotNull(message = "sort参数缺失")
    private Integer sort;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
