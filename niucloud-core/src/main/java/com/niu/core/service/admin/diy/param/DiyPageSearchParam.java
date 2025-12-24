package com.niu.core.service.admin.diy.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义页面参数
 */
@Data
public class DiyPageSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private List<String> type;

    private String mode;

    private String addonName = "";

}
