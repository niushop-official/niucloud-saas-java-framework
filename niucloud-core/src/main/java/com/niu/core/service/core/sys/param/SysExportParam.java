package com.niu.core.service.core.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 导出报参数
*/
@Data
public class SysExportParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "exportKey参数缺失")
    private String exportKey;

    @NotNull(message = "exportNum参数缺失")
    private Integer exportNum;

    @NotNull(message = "filePath参数缺失")
    private String filePath;

    @NotNull(message = "fileSize参数缺失")
    private String fileSize;

    @NotNull(message = "failReason参数缺失")
    private String failReason;

}
