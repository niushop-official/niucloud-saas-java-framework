package com.niu.core.service.core.diy.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 底部导航视图
 */

@Data
public class StartUpPageConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String name = "";

    private String mode;

    private String title;

    private String page;

    private String parent;

    private String action;
}