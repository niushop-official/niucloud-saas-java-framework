package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 导出报参数
 */
@Data
public class SysExportSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主题关键字 */
    private String exportKey;

    /** 导出状态 */
    private Integer exportStatus;

    /** 创建时间 */
    private String[] createTime;

}
