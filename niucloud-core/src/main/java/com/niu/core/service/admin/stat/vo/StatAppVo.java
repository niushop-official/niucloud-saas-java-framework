package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatAppVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer appCount;
    private Integer appInstalledCount;
    private Integer appNoInstalledCount;
}
