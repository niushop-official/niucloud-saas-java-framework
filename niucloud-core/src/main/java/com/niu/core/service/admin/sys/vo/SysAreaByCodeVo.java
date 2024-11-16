package com.niu.core.service.admin.sys.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.AgreementEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * SysArea视图
 */
@Data
public class SysAreaByCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer id;

    private Integer pid;

    private Integer level;

    private String name;
}
