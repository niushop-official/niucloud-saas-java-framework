package com.niu.core.service.admin.pay.vo;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.member.Member;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FriendsPayInfoByTradeVo {
    private Integer id;
    private Integer siteId;
    private Integer mainId;
    private String outTradeNo;
    private String tradeType;
    private Integer tradeId;
    private String tradeNo;
    private String body;
    private BigDecimal money;
    private String voucher;
    private Integer status;
    private String json;
    private String createTime;
    private String payTime;
    private String cancelTime;
    private String type;
    private String mchId;
    private String mainType;
    private String channel;
    private String failReason;
    private Integer posterId;
    private MemberBriefInfoVo member;
    private String link;
    private String qrcode;
    private JSONObject config;
}
