package com.niu.core.listener.poster;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventCallback("shop")
@Component
public class GetPosterDataListener extends GetPosterDataEventDefiner {
    private PayMapper payMapper;
    private MemberMapper memberMapper;
    private ICoreSysConfigService coreSysConfigService;

    public GetPosterDataEventDefiner.GetPosterDataResult handleCallback(GetPosterDataEventDefiner.GetPosterDataEvent event) {
        if (event.getType().equals("friendspay")) {
            final Map<String, Object> param = event.getParam();
            GetPosterDataEventDefiner.GetPosterDataResult result = new GetPosterDataEventDefiner.GetPosterDataResult();
            Object tradeId = param.get("id");
            Object tradeType = param.get("type");
            String wapUrl = coreSysConfigService.getSceneDomain(event.getSiteId()).getWapUrl();
            if (ObjectUtil.equal(param.get("mode"), "preview")) {
                Map<String, Object> data = new HashMap<>(8, 1);
                data.put("nickname", "会员昵称的帮付");
                data.put("headimg", "static/resource/images/default_headimg.png");
                data.put("friendspay_message", "帮我付一下这笔订单吧，谢谢啦~");
                data.put("friendspay_money", "￥369.00");

                Map<String, Object> url = Map.of("url", wapUrl,
                        "page", "app/pages/friendspay/money",
                        "data", Map.of("id", tradeId, "type", param.get("type")));
                data.put("url", url);

                result.setData(data);
                return result;
            }

            QueryWrapper<Pay> wrapper = new QueryWrapper<>();
            wrapper.eq("site_id", event.getSiteId())
                    .eq("trade_id", tradeId)
                    .eq("trade_type", tradeType)
                    .ne("status", PayStatusEnum.STATUS_CANCLE.getCode());
            List<Pay> pays = payMapper.selectList(wrapper);
            if (ObjectUtil.isEmpty(pays)) {
                return result;
            }

            Map<String, Object> data = new HashMap<>(8, 1);
            data.put("friendspay_message", "帮我付一下这笔订单吧，谢谢啦~");
            data.put("friendspay_money", "￥369.00");

            // 生成二维码
            String url = QrcodeUtils.qrcodeToFile(
                    event.getSiteId(),
                    event.getChannel(),
                    coreSysConfigService.getSceneDomain(event.getSiteId()).getWapUrl(),
                    "addon/shop/pages/goods/detail",
                    Map.of("id", tradeId, "type", param.get("type")),
                    ""
            );
            data.put("url", url);

            String memberId = event.getParam().getOrDefault("member_id", 0).toString();
            if (ObjectUtil.isNotEmpty(memberId)) {
                Member member = memberMapper.selectById(memberId);
                if (ObjectUtil.isNotEmpty(member)) {
                    if (ObjectUtil.isEmpty(member.getHeadimg())) {
                        member.setHeadimg("static/resource/images/default_headimg.png");
                    }

                    data.put("nickname", member.getNickname().length() > 12 ? member.getNickname().substring(12) + "...的帮付" : member.getNickname() + "的帮付");
                    data.put("headimg", member.getHeadimg());
                }
            }

            result.setData(data);
            return result;
        }
        return null;
    }

    @Autowired
    public void setPayMapper(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setCoreSysConfigService(ICoreSysConfigService coreSysConfigService) {
        this.coreSysConfigService = coreSysConfigService;
    }

}
