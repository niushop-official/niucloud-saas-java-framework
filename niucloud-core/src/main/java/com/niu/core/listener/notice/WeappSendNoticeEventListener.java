package com.niu.core.listener.notice;

import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.component.context.event.DefaultEventResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.enums.notice.NoticeTypeEnum;
import com.niu.core.event.notice.SendNoticeEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysNoticeLogMapper;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@EventCallback("core")
@Component
public class WeappSendNoticeEventListener extends SendNoticeEventDefiner {

    @Resource
    SysNoticeLogMapper sysNoticeLogMapper;

    @Resource
    MemberMapper memberMapper;

    public DefaultEventResult handleCallback(SendNoticeEvent event) {
        NoticeInfoVo notice = event.getNotice();
        NoticeDataVo noticeData = event.getNoticeData();

        if (notice.getIsWeapp().equals(1)) {
            SysNoticeLog model = new SysNoticeLog();

            model.setSiteId(event.getSiteId());
            model.setKey(event.getKey());
            model.setNoticeType(NoticeTypeEnum.WEAPP.getCode());
            model.setUid(noticeData.getUid());
            model.setMemberId(noticeData.getMemberId());
            model.setContent(notice.getWeapp().getStr("content", ""));
            model.setParams(JSONUtil.parseObj(noticeData.getVars()).toString());

            if (noticeData.getOpenid().isEmpty() && noticeData.getMemberId() > 0) {
                Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("weapp_openid,nickname").eq("site_id", event.getSiteId()).eq("member_id", noticeData.getMemberId()));
                if (member != null && !member.getWeappOpenid().isEmpty()) {
                    noticeData.setOpenid(member.getWeappOpenid());
                    model.setNickname(member.getNickname());
                }
            }
            if (!noticeData.getOpenid().isEmpty()) {
                model.setReceiver(noticeData.getOpenid());
                model.setCreateTime(System.currentTimeMillis() / 1000);

                try {
                    List<WxMaSubscribeMessage.MsgData> data = new LinkedList<>();

                    JSONArray content = notice.getWeapp().getJSONArray("content");

                    for (int i = 0; i < content.size(); i++) {
                        JSONArray contentItem = content.getJSONArray(i);
                        WxMaSubscribeMessage.MsgData item = new WxMaSubscribeMessage.MsgData();
                        String varKey = contentItem.getStr(1).replace("{", "").replace("}", "");
                        item.setName(contentItem.getStr(2));
                        item.setValue((String) ObjectUtil.defaultIfNull(noticeData.getVars().get(varKey), ""));
                        data.add(item);
                    }

                    WxMaSubscribeMessage subscribeMessage = new WxMaSubscribeMessage();
                    subscribeMessage.setTemplateId(notice.getWeappTemplateId());
                    subscribeMessage.setToUser(noticeData.getOpenid());
                    subscribeMessage.setPage((String) ObjectUtil.defaultIfNull(noticeData.getVars().get("weapp_page"), ""));
                    subscribeMessage.setData(data);
                    subscribeMessage.setMiniprogramState("formal");
                    subscribeMessage.setLang("zh_CN");

                    WechatUtils.miniapp(event.getSiteId()).getSubscribeService().sendSubscribeMsg(subscribeMessage);
                    sysNoticeLogMapper.insert(model);
                } catch (Exception e) {
                    model.setResult(e.getMessage());
                    sysNoticeLogMapper.insert(model);
                    if (!notice.getAsync()) throw new CommonException(e.getMessage());
                }
            }
        }
        return DefaultEventResult.build();
    }

}
