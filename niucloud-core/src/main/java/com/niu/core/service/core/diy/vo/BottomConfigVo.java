package com.niu.core.service.core.diy.vo;

import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.BottomConfigValue;
import lombok.Data;

import java.io.Serializable;

/**
 * 底部导航视图
 */

@Data
public class BottomConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key = "";

    private Addon info;

    private BottomConfigValue value;

}