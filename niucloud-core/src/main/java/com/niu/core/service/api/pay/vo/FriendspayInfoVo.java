package com.niu.core.service.api.pay.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.entity.member.Member;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
public class FriendspayInfoVo {
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
    private int posterId;
    private Member member;
    private boolean isSelf;
    private Object tradeInfo;
    private JSONObject config;
    private List<PayTypeVo> payTypeList;

    public Object getTradeInfo() {
        if (ObjectUtil.isNotEmpty(tradeInfo)) return tradeInfo;
        return new LinkedList<>();
    }
}
