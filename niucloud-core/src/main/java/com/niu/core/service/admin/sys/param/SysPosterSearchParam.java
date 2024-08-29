package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 海报参数
 */
@Data
public class SysPosterSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String name;

    private String type;

    private String channel;

    private String value;

    private Integer status;

    private String addon;

    private Integer isDefault;

}
