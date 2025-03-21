package com.niu.core.service.api.wechat.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.api.wechat.IServeService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Service
@Slf4j
public class ServeServiceImpl implements IServeService {

    /**
     * 公众号消息推送
     * @param request
     * @param response
     */
    public void service(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            String signature = request.getParameter("signature");
            String nonce = request.getParameter("nonce");
            String timestamp = request.getParameter("timestamp");

            // 消息签名不正确，说明不是公众平台发过来的消息
            WxMpService wxMpService = WechatUtils.mp(RequestUtils.siteId());
            if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
                response.getWriter().println("非法请求");
                return;
            }

            // 说明是一个仅仅用来验证的请求，回显echostr
            String echostr = request.getParameter("echostr");
            if (StringUtils.isNotBlank(echostr)) {
                response.getWriter().println(echostr);
                return;
            }

            String encryptType = StringUtils.isBlank(request.getParameter("encrypt_type")) ? "raw" : request.getParameter("encrypt_type");

            WxMpXmlMessage inMessage = null;

            if ("raw".equals(encryptType)) {
                // 明文传输的消息
                inMessage = WxMpXmlMessage.fromXml(request.getInputStream());
            } else if ("aes".equals(encryptType)) {
                // 是aes加密的消息
                String msgSignature = request.getParameter("msg_signature");
                inMessage = WxMpXmlMessage.fromEncryptedXml(request.getInputStream(), wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
            } else {
                response.getWriter().println("不可识别的加密类型");
                return;
            }

            WxMpXmlOutMessage outMessage = this.message(inMessage);
            if (outMessage != null) {
                if ("raw".equals(encryptType)) {
                    response.getWriter().write(outMessage.toXml());
                } else if ("aes".equals(encryptType)) {
                    response.getWriter().write(outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage()));
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WxMpXmlOutMessage message(WxMpXmlMessage inMessage) {
        try {
            WxMpMessageRouter router = new WxMpMessageRouter(WechatUtils.mp(RequestUtils.siteId()));
            MessageHandleImpl handler = new MessageHandleImpl();
            handler.setSiteId(RequestUtils.siteId());

            router
                .rule()
                .event(WxConsts.EventType.SUBSCRIBE)
                .handler(handler)
                .end()
                .rule()
                .event(WxConsts.EventType.SCAN)
                .handler(handler)
                .end()
                .rule()
                .msgType(WxConsts.MassMsgType.TEXT)
                .handler(handler)
                .end();

            return router.route(inMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
