package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.util.Map;

@Data
public class SysScheduleTemplateVo {

    private String key;

    private String name;

    private String desc;

    private Map<String,Object> time;

    private String clazz;

    private String function;

}
