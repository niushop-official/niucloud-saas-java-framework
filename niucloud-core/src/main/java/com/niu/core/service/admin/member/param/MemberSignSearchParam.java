package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员签到参数
 */
@Data
public class MemberSignSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String keywords;

    private String[] createTime;
}
