package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 运行环境类
 */
@Data
public class StatSystemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String environment;
    private String os;
    private String phpV;
    private StatVersionVo version;
}
