package com.niu.core.service.admin.sys.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 海报参数
*/
@Data
public class SysPosterModifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer status;


}
