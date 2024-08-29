package com.niu.core.service.api.member.vo;

import lombok.Data;

@Data
public class MemberSignRecordVo {


    private Integer signId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 连续签到天数
     */
    private Integer days;

    /**
     * 日签奖励
     */
    private String dayAward;

    /**
     * 连签奖励
     */
    private String continueAward;

    /**
     * 连签奖励标识
     */
    private String continueTag;

    /**
     * 签到时间
     */
    private Long createTime;

    /**
     * 是否签到（0未签到 1已签到）
     */
    private Integer isSign;

    /**
     * @return
     */
    public String getIsSignName() {
        if (isSign == 1) {
            return "已签到";
        }
        return "未签到";
    }

}
