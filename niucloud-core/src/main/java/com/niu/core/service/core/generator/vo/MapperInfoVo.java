package com.niu.core.service.core.generator.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MapperInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mapper;

    private String mapperName;

    private String tableName;

    private String entityName;

    private String entity;
}
