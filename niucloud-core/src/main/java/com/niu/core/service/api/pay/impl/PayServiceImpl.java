package com.niu.core.service.api.pay.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.event.pay.PayTradeInfoDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.api.pay.IPayService;
import com.niu.core.service.api.pay.vo.FriendspayInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayChannelService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
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

    @Resource
    PayMapper payMapper;

    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 支付
     *
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

    public GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String scene) {
        return corePayService.getInfoByTrade(RequestUtils.siteId(), tradeType, tradeId, RequestUtils.channel(), scene);
    }

    @Override
    public FriendspayInfoVo friendspayInfo(String tradeType, Integer tradeId) {
        QueryWrapper<Pay> payQueryWrapper = new QueryWrapper<>();
        payQueryWrapper.eq("trade_type", tradeType)
                .eq("trade_id", tradeId);
        List<Pay> fromPayInfos = payMapper.selectList(payQueryWrapper);
        if (ObjectUtil.isEmpty(fromPayInfos) || fromPayInfos.stream().noneMatch(o -> o.getSiteId().equals(RequestUtils.siteId()))) {
            throw new RuntimeException("支付单据不存在");
        }

        GetInfoByTradeVo payInfo = getInfoByTrade(tradeType, tradeId, "friendspay");
        if (ObjectUtil.isEmpty(payInfo)) {
            return new FriendspayInfoVo();
        }

        FriendspayInfoVo vo = new FriendspayInfoVo();
        BeanUtil.copyProperties(payInfo, vo);
        vo.setConfig(payInfo.getConfig());
        vo.setPayTypeList(payInfo.getPayTypeList());
        vo.setCreateTime(DateUtils.timestampToString(payInfo.getCreateTime()));
        if (payInfo.getPayTime() != 0) {
            vo.setPayTime(DateUtils.timestampToString(payInfo.getPayTime()));
        }

        if (payInfo.getCancelTime() != 0) {
            vo.setCancelTime(DateUtils.timestampToString(payInfo.getCancelTime()));
        }

        PayTradeInfoDefiner.PayTradeInfoEvent event = new PayTradeInfoDefiner.PayTradeInfoEvent(tradeType, tradeId);
        List<PayTradeInfoDefiner.PayTradeInfoEventResult> trades = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (ObjectUtil.isNotEmpty(trades)) {
            vo.setTradeInfo(trades.get(0).getJsonObject());
        }

        vo.setSelf(payInfo.getMainId().equals(RequestUtils.memberId()));

        QueryWrapper<SysPoster> posterQueryWrapper = new QueryWrapper<>();
        posterQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("type", "friendspay")
                .eq("status", 1)
                .eq("is_default", 1);
        SysPoster sysPoster = sysPosterMapper.selectOne(posterQueryWrapper);
        if (ObjectUtil.isNotEmpty(sysPoster)) {
            vo.setPosterId(sysPoster.getId());
        }

        Member member = memberMapper.selectById(payInfo.getMainId());
        vo.setMember(member);
        return vo;
    }

}
