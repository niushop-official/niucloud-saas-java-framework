package com.niu.core.service.api.sys.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAreaLevelVo {

    // ID
    private Integer id;

    // 父级
    private Integer pid;

    //
    @JsonIgnore
    private Integer level;

    // 名称
    private String name;

    // 孩子节点
    private List<SysAreaLevelVo> child;

}
