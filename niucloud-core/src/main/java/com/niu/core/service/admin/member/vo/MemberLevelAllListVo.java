package com.niu.core.service.admin.member.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * MemberLevel列表视图
 */
@Data
public class MemberLevelAllListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer levelId;  // 会员等级
    private String levelName;  // 等级名称
    private Integer growth; // 所需成长值
    private Integer siteId; // 站点id
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String levelBenefits; // 等级权益
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String levelGifts; //等级礼包
}
