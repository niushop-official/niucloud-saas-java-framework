package com.niu.core.service.core.diy.vo;

import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.BottomConfigValue;
import lombok.Data;

import java.io.Serializable;

/**
 * 底部导航视图
 */

@Data
public class StartUpPageConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type = "";

    private String name = "";

    private String mode = "";

    private String title = "";

    private String page = "";

    private String parent = "";

    private String action = "";
}