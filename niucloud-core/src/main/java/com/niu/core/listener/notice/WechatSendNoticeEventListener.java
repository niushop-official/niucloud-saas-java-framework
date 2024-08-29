package com.niu.core.listener.notice;

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
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@EventCallback("core")
@Component
public class WechatSendNoticeEventListener extends SendNoticeEventDefiner {

    @Resource
    SysNoticeLogMapper sysNoticeLogMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    public DefaultEventResult handleCallback(SendNoticeEvent event) {
        NoticeInfoVo notice = event.getNotice();
        NoticeDataVo noticeData = event.getNoticeData();

        if (notice.getIsWechat().equals(1)) {
            SysNoticeLog model = new SysNoticeLog();

            model.setSiteId(event.getSiteId());
            model.setKey(event.getKey());
            model.setNoticeType(NoticeTypeEnum.WECHAT.getCode());
            model.setUid(noticeData.getUid());
            model.setMemberId(noticeData.getMemberId());
            model.setContent(notice.getWechat().getStr("content", ""));
            model.setParams(JSONUtil.parseObj(noticeData.getVars()).toString());

            if (noticeData.getOpenid().isEmpty() && noticeData.getMemberId() > 0) {
                Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("wx_openid,nickname").eq("site_id", event.getSiteId()).eq("member_id", noticeData.getMemberId()));
                if (member != null && !member.getWxOpenid().isEmpty()) {
                    noticeData.setOpenid(member.getWxOpenid());
                    model.setNickname(member.getNickname());
                }
            }
            if (!noticeData.getOpenid().isEmpty()) {
                model.setReceiver(noticeData.getOpenid());
                model.setCreateTime(System.currentTimeMillis() / 1000);

                try {
                    List<WxMpTemplateData> data = new LinkedList<>();

                    JSONArray content = notice.getWechat().getJSONArray("content");

                    for (int i = 0; i < content.size(); i++) {
                        JSONArray contentItem = content.getJSONArray(i);
                        WxMpTemplateData item = new WxMpTemplateData();
                        String varKey = contentItem.getStr(1).replace("{", "").replace("}", "");
                        item.setName(contentItem.getStr(2));
                        item.setValue(ObjectUtil.defaultIfNull(noticeData.getVars().get(varKey), "").toString());
                        data.add(item);
                    }

                    WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
                    templateMessage.setTemplateId(notice.getWechatTemplateId());
                    templateMessage.setToUser(noticeData.getOpenid());
                    templateMessage.setData(data);
                    if (ObjectUtil.isNotEmpty(noticeData.getVars().get("wechat_page"))) {
                        templateMessage.setUrl(coreSysConfigService.getSceneDomain(event.getSiteId()).getWapUrl() + (String) noticeData.getVars().get("wechat_page"));
                    }

                    WeappConfigVo config = coreWeappConfigService.getWeappConfig(event.getSiteId());
                    if (!config.getAppId().isEmpty()) {
                        WxMpTemplateMessage.MiniProgram miniProgram = new WxMpTemplateMessage.MiniProgram();
                        miniProgram.setAppid(config.getAppId());
                        miniProgram.setPagePath((String) ObjectUtil.defaultIfNull(noticeData.getVars().get("weapp_page"), ""));
                        templateMessage.setMiniProgram(miniProgram);
                    }

                    WechatUtils.mp(event.getSiteId()).getTemplateMsgService().sendTemplateMsg(templateMessage);
                    sysNoticeLogMapper.insert(model);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.setResult(e.getMessage());
                    sysNoticeLogMapper.insert(model);
                    if (!notice.getAsync()) throw new CommonException(e.getMessage());
                }
            }
        }
        return DefaultEventResult.build();
    }

}
