package com.niu.core.service.api.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.component.context.SpringContext;
import com.niu.core.entity.wechat.WechatReply;
import com.niu.core.enums.channel.WechatReplyTypeEnum;
import com.niu.core.mapper.wechat.WechatReplyMapper;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.core.sys.ICoreScanService;
import com.niu.core.service.core.wechat.ICoreWechatReplyService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.builder.kefu.NewsBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Component
public class MessageHandleImpl implements WxMpMessageHandler {

    WxMpService wxMpService;

    WxMpXmlMessage wxMessage;

    private Integer siteId;

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    /**
     * @return
     */
    public ICoreScanService getCoreScanService(){
        ICoreScanService coreScanService = SpringContext.bean(ICoreScanService.class);
        return coreScanService;
    }

    public WechatReplyMapper getWechatReplyMapper() {
        WechatReplyMapper wechatReplyMapper = SpringContext.bean(WechatReplyMapper.class);
        return wechatReplyMapper;
    }

    public ICoreWechatReplyService getCoreWechatReplyService() {
        ICoreWechatReplyService coreWechatReplyService = SpringContext.bean(ICoreWechatReplyService.class);
        return coreWechatReplyService;
    }

    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        this.wxMpService = wxMpService;
        this.wxMessage = wxMessage;
        switch (wxMessage.getMsgType()) {
            case WxConsts.XmlMsgType.EVENT:
                return event(wxMessage);
            case WxConsts.XmlMsgType.TEXT:
                return text(wxMessage);
        }
        return null;
    }

    /**
     * 扫码事件
     * @param wxMessage
     * @return
     */
    private WxMpXmlOutMessage event(WxMpXmlMessage wxMessage) {
        switch (wxMessage.getEvent()) {
            case WxConsts.EventType.SUBSCRIBE:
                this.subscribeReply();
                if (!wxMessage.getEventKey().isEmpty()) {
                    this.scan(wxMessage);
                }
                break;
            case WxConsts.EventType.SCAN:
                return this.scan(wxMessage);
        }
        return null;
    }

    private WxMpXmlOutMessage text(WxMpXmlMessage wxMessage) {
        String keyword = wxMessage.getContent();

        QueryWrapper<WechatReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reply_type", WechatReplyTypeEnum.REPLY_KEYWORD.getType());
        queryWrapper.eq("site_id", siteId);
        queryWrapper.like("keyword", keyword);
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
        List<WechatReply> replyList = getWechatReplyMapper().selectList(queryWrapper);

        WechatReply reply = null;

        if (!replyList.isEmpty()) {
            for (WechatReply item: replyList) {
                if (item.getMatchingType().equals("full") && item.getKeyword().equals(keyword)) {
                    reply = item;
                    break;
                }
                if (item.getMatchingType().equals("like") && keyword.indexOf(item.getKeyword()) != -1) {
                    reply = item;
                    break;
                }
            }
        }
        if (reply != null) {
            JSONArray content = JSONUtil.parseArray(reply.getContent());
            if (reply.getReplyMethod().equals("all")) {
                for (Object item: content) {
                    if (item instanceof JSONObject) {
                        this.sendReply((JSONObject) item);
                    }
                }
            } else {
                int index = new Random().nextInt(content.size());
                Object item = content.getObj(index);
                if (item instanceof JSONObject) {
                    this.sendReply((JSONObject) item);
                }
            }
        } else {
            this.defaultReply();
        }
        return null;
    }

    /**
     * 扫码时间
     * @param wxMessage
     * @return
     */
    private WxMpXmlOutMessage scan(WxMpXmlMessage wxMessage) {
        String key = wxMessage.getEventKey().replace("qrscene_", "");
        getCoreScanService().actionByScan(siteId, key, new JSONObject().set("openid", wxMessage.getFromUser()));
        return WxMpXmlOutMessage.TEXT()
                .content("扫码成功")
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }

    /**
     * 默认回复
     * @return
     */
    private void defaultReply() {
        WechatReplyInfoVo info = getCoreWechatReplyService().getDefault(siteId);
        if (ObjectUtil.isNotNull(info.getContent())) {
            this.sendReply(JSONUtil.parseObj(info.getContent()));
        }
    }

    /**
     * 关注回复
     * @return
     */
    private void subscribeReply() {
        WechatReplyInfoVo info = getCoreWechatReplyService().getSubscribe(siteId);
        if (ObjectUtil.isNotNull(info.getContent())) {
            this.sendReply(JSONUtil.parseObj(info.getContent()));
        }
    }

    /**
     * 发送回复
     * @param content
     */
    private void sendReply(JSONObject content) {
        try {
            WxMpKefuMessage message = null;
            switch (ObjectUtil.defaultIfNull(content.getStr("msgtype"), "")) {
                case "text":
                    message = WxMpKefuMessage
                            .TEXT()
                            .toUser(this.wxMessage.getFromUser())
                            .content((String) content.getByPath("text.content"))
                            .build();
                    break;
                case "image":
                    message = WxMpKefuMessage
                            .IMAGE()
                            .toUser(this.wxMessage.getFromUser())
                            .mediaId((String) content.getByPath("image.media_id"))
                            .build();
                    break;
                case "video":
                    message = WxMpKefuMessage
                            .VIDEO()
                            .toUser(this.wxMessage.getFromUser())
                            .title("")
                            .mediaId((String) content.getByPath("video.media_id"))
                            .thumbMediaId("")
                            .description("")
                            .build();
                    break;
                case "miniprogrampage":
                    message = WxMpKefuMessage
                            .MINIPROGRAMPAGE()
                            .toUser(this.wxMessage.getFromUser())
                            .appId((String) content.getByPath("miniprogrampage.appid"))
                            .title((String) content.getByPath("miniprogrampage.title"))
                            .pagePath((String) content.getByPath("miniprogrampage.pagepath"))
                            .thumbMediaId((String) content.getByPath("miniprogrampage.thumb_media_id"))
                            .build();
                    break;
                case "mpnewsarticle":
                    NewsBuilder newsBuilder = WxMpKefuMessage
                            .NEWS()
                            .toUser(this.wxMessage.getFromUser());

                    for (Object item : content.getJSONArray("news_item")) {
                        if (item instanceof JSONObject) {
                            JSONObject Object = (JSONObject) item;
                            WxMpKefuMessage.WxArticle article = new WxMpKefuMessage.WxArticle();
                            article.setUrl(Object.getStr("url"));
                            article.setPicUrl(Object.getStr("picurl"));
                            article.setDescription(Object.getStr("description"));
                            article.setTitle(Object.getStr("title"));
                            newsBuilder.addArticle(article);
                        }
                    }
                    message = newsBuilder.build();
                    break;
            }
            if (message != null) this.wxMpService.getKefuService().sendKefuMessage(message);
        } catch (Exception e) {
            log.info("微信回复异常：" + e.getMessage());
        }
    }
}
