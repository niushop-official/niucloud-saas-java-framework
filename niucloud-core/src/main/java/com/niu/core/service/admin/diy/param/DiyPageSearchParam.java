package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义页面参数
 */
@Data
public class DiyPageSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;


    private String title;

    private String type;

    private String mode;

    private String addonName;

}
