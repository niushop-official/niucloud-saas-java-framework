package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义主题色设置
*/
@Data
public class DiyThemeTitleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String addon = "";

    private String title = "";

}
