package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Member视图
 */
@Data
public class MemberConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String prefix = "";  // 会员编码前缀
    private Integer length = 4;  // 会员编码长度
    private Integer formId;
}
