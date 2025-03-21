package com.niu.core.service.admin.diy.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自定义页面参数
*/
@Data
public class DiyPageInitParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id = 0;

    private String title = "";

    private String name = "";

    private String type = "";

}
