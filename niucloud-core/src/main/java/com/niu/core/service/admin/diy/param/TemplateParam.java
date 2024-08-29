package com.niu.core.service.admin.diy.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自定义页面参数
*/
@Data
public class TemplateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String[] key;

    private String action = "";

    private String mode = "";

    private String type = "";

    private String addon = "";
}
