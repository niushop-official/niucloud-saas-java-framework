package com.niu.recharge.service.core.notice.driver;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.recharge.entity.RechargeOrder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RechargeSuccess extends BaseNotice {

    public MemberMapper memberMapper() {
        return SpringContext.bean(MemberMapper.class);
    }

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        RechargeOrder order = (RechargeOrder) vars.get("order");

        Member member = memberMapper().selectOne(new QueryWrapper<Member>().select("balance").eq("member_id", order.getMemberId()));

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("balance", member.getBalance());
        param.put("price", order.getOrderMoney());
        param.put("trade_no", order.getOrderNo());
        param.put("time", DateUtil.date(order.getPayTime() * 1000));
        param.put("wechat_page", "/addon/recharge/pages/recharge_record_detail?id=" + order.getOrderId());
        param.put("weapp_page", "addon/recharge/pages/recharge_record_detail?id=" + order.getOrderId());

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(order.getMemberId());
        vo.setVars(param);

        return vo;
    }
}
