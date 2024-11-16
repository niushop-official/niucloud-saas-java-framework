package com.niu.core.service.core.generator.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 代码生成器模板数据
 */
@Data
public class CoreGenerateTemplateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型  Java，admin， uniapp等
     */
    private String type;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 模板路径
     */
    private String template;

    /**
     * 数据
     */
    private String data;

    /**
     * 插件名称（模块名称）
     */
    private String addon;



}