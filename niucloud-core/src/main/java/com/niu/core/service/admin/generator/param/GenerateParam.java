package com.niu.core.service.admin.generator.param;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 代码生成创建
 */
@Data
public class GenerateParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tableName;

}
