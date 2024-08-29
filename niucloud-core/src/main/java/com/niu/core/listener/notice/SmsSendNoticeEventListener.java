package com.niu.core.listener.notice;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.component.context.event.DefaultEventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.enums.notice.NoticeTypeEnum;
import com.niu.core.event.notice.SendNoticeEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysNoticeLogMapper;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.niu.core.service.core.sms.ICoreSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@EventCallback("core")
@Component
public class SmsSendNoticeEventListener extends SendNoticeEventDefiner {

    @Autowired
    SysNoticeLogMapper sysNoticeLogMapper;

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    ICoreSmsService coreSmsService;

    public DefaultEventResult handleCallback(SendNoticeEvent event) {
        NoticeInfoVo notice = event.getNotice();
        NoticeDataVo noticeData = event.getNoticeData();

        if (notice.getIsSms().equals(1)) {
            SysNoticeLog model = new SysNoticeLog();

            model.setSiteId(event.getSiteId());
            model.setKey(event.getKey());
            model.setNoticeType(NoticeTypeEnum.SMS.getCode());
            model.setUid(noticeData.getUid());
            model.setMemberId(noticeData.getMemberId());
            model.setContent(notice.getSms().getStr("content", ""));
            model.setParams(JSONUtil.parseObj(noticeData.getVars()).toString());

            String mobile = ObjectUtil.defaultIfNull(noticeData.getMobile(), "");

            if (mobile.isEmpty() && noticeData.getMemberId() > 0) {
                Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("mobile,nickname").eq("site_id", event.getSiteId()).eq("member_id", noticeData.getMemberId()));
                if (member != null && !member.getMobile().isEmpty()) {
                    noticeData.setMobile(member.getMobile());
                    mobile = member.getMobile();
                    model.setNickname(member.getNickname());
                }
            }
            if (mobile.isEmpty()) {
                if (!notice.getAsync()) throw new CommonException("手机号为空");
                return null;
            }

            model.setReceiver(noticeData.getMobile());
            model.setCreateTime(System.currentTimeMillis() / 1000);

            try {
                coreSmsService.send(event.getSiteId(), notice, noticeData);
                sysNoticeLogMapper.insert(model);
            } catch (Exception e) {
                model.setResult(e.getMessage());
                sysNoticeLogMapper.insert(model);
                if (!notice.getAsync()) throw new CommonException(e.getMessage());
            }
        } else {
            if (!notice.getAsync()) throw new CommonException("当前消息未开启短信发送");
        }
        return DefaultEventResult.build();
    }

}
