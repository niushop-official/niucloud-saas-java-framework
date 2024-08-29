package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员标签参数
 */
@Data
public class MemberLabelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelName;

}
