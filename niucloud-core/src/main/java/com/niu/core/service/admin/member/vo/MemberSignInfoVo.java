package com.niu.core.service.admin.member.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * MemberSign视图
 */
@Data
public class MemberSignInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer signId;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer days;  // 连续签到天数
    private String dayAward;  // 日签奖励
    private String continueAward;  // 连签奖励
    private String continueTag;  // 连签奖励标识
    private String startTime; // 签到周期开始时间
    private Integer isSign;  // 是否签到（0未签到 1已签到）

}
