package com.niu.core.service.admin.generator.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class GeneratePreviewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private String content;

    private String fileDir;

}
