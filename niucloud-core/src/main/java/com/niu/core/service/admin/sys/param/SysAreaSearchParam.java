package com.niu.core.service.admin.sys.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 地址参数
 */
@Data
public class SysAreaSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pid;

    private String name;

    private String shortname;

    private String longitude;

    private String latitude;

    private Integer level;

    private Integer sort;

    private Integer status;

}
