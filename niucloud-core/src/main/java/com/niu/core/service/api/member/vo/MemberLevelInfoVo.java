package com.niu.core.service.api.member.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

@Data
public class MemberLevelInfoVo {

    private Integer levelId;  // 会员等级
    private Integer siteId;  // 站点id
    private String levelName;  // 等级名称
    private Integer growth;  // 所需成长值
    private String remark;  // 备注
    private JSONObject levelBenefits;  // 等级权益
    private JSONObject levelGifts;  // 等级礼包
    private String levelBg;
    private String memberBg;
    private String levelIcon;
    private String levelTag;
    private JSONObject levelStyle;
}
