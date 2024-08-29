package com.niu.core.service.admin.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * MemberLabel列表视图
 */
@Data
public class MemberLabelAllListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer labelId;  // 标签id
    private String labelName;  // 标签名称

}
