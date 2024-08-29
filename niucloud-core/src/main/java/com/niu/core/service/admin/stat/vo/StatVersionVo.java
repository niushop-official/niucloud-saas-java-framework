package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 版本号
 */
@Data
public class StatVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String version;
}
