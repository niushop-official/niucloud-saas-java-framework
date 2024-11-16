package com.niu.core.service.admin.dict.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据参数
 */
@Data
public class DictSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String key;

}
