package com.niu.core.service.admin.member.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.member.SignStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * MemberSign列表视图
 */
@Data
public class MemberSignListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer signId;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer days;  // 连续签到天数
    private JSONObject dayAward;  // 日签奖励
    private JSONObject continueAward;  // 连签奖励
    private String continueTag;  // 连签奖励标识
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 签到时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime; // 签到周期开始时间
    private Integer isSign;  // 是否签到（0未签到 1已签到）
    private MemberBriefInfoVo member;
    private String isSignName;

    public String getIsSignName() {
        return SignStatusEnum.getNameByStatus(this.isSign);
    }
}
