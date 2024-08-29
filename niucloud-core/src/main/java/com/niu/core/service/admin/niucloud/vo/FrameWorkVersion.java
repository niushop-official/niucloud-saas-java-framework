package com.niu.core.service.admin.niucloud.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 框架最新版本
 */
@Data
public class FrameWorkVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 最后更新时间 */
    private String lastUpdateTime = "";
    /** 最新版本 */
    private String lastVersion = "";
}
