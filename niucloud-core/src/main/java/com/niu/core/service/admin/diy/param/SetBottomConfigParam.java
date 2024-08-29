package com.niu.core.service.admin.diy.param;

import com.niu.core.entity.diy.BottomConfigValue;
import lombok.Data;

import java.io.Serializable;

/**
 * 自定义页面参数
*/
@Data
public class SetBottomConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key = "app";

    private BottomConfigValue value;
}
