package com.niu.shop.service.admin.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.active.ActiveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopActive列表视图
 */
@Data
public class ShopDiscountMemberListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;  // 主键

    private String mobile;  // 手机号

    private String nickname;  // 会员昵称

    private String headimg;  // 会员头像

    private MemberBriefInfoVo member;

    private BigDecimal activeOrderMoney;

    private Long memberCount;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
}
