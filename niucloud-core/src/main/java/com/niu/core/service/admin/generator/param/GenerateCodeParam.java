package com.niu.core.service.admin.generator.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 代码生成创建
 */
@Data
public class GenerateCodeParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String generateType;

}
