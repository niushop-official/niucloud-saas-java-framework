package com.niu.core.service.api.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.pay.IPayService;
import com.niu.core.service.api.pay.vo.PayInfoVo;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
@Slf4j
public class PayServiceImpl implements IPayService {

    @Resource
    ICorePayService corePayService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICorePayChannelService corePayChannelService;

    /**
     * 支付
     * @param param
     * @return
     */
    public Object pay(PayParam param) {
        param.setSiteId(RequestUtils.siteId());
        param.setChannel(RequestUtils.channel());
        param.setBuyerId(RequestUtils.memberId());

        if (RequestUtils.channel().equals(ChannelEnum.WECHAT.getCode()) && param.getOpenid().isEmpty()) {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("wx_openid").eq("site_id", param.getSiteId()).eq("member_id", RequestUtils.memberId()));
            param.setOpenid(ObjectUtil.defaultIfNull(member.getWxOpenid(), ""));
        }
        if (RequestUtils.channel().equals(ChannelEnum.WEAPP.getCode()) && param.getOpenid().isEmpty()) {
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("weapp_openid").eq("site_id", param.getSiteId()).eq("member_id", RequestUtils.memberId()));
            param.setOpenid(ObjectUtil.defaultIfNull(member.getWeappOpenid(), ""));
        }

        return corePayService.pay(param);
    }

    /**
     * 支付异步回调
     *
     * @param param
     * @param request
     * @param response
     * @return
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return corePayService.asyncNotify(param, request, response);
    }

    public PayInfoVo getInfoByTrade(String tradeType, Integer tradeId) {
        Pay pay = null;
        pay = corePayService.findPayInfoByTrade(RequestUtils.siteId(), tradeType, tradeId);

        if (pay == null) {
            pay = corePayService.createByTrade(RequestUtils.siteId(), tradeType, tradeId);
        }

        PayInfoVo vo = new PayInfoVo();
        BeanUtil.copyProperties(pay, vo);

        List<PayTypeVo> payTypeList = corePayChannelService.getAllowPayTypeByChannel(RequestUtils.siteId(), RequestUtils.channel(), tradeType);
        vo.setPayTypeList(payTypeList);

        return vo;
    }
}
