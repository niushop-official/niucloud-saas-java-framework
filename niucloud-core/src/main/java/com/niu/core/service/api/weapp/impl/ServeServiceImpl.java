package com.niu.core.service.api.weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.message.WxMaXmlOutMessage;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.api.weapp.IServeService;
import com.niu.core.service.api.wechat.impl.MessageHandleImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Service;

@Service
public class ServeServiceImpl implements IServeService {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            String signature = request.getParameter("signature");
            String nonce = request.getParameter("nonce");
            String timestamp = request.getParameter("timestamp");

            WxMaService wxMaService = WechatUtils.miniapp(RequestUtils.siteId());
            if (!wxMaService.checkSignature(timestamp, nonce, signature)) {
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

            WxMaMessage inMessage = null;

            if ("raw".equals(encryptType)) {
                // 明文传输的消息
                inMessage = inMessage.fromXml(request.getInputStream());
            } else if ("aes".equals(encryptType)) {
                // 是aes加密的消息
                String msgSignature = request.getParameter("msg_signature");
                inMessage = WxMaMessage.fromEncryptedXml(request.getInputStream(), wxMaService.getWxMaConfig(), timestamp, nonce, msgSignature);
            } else {
                response.getWriter().println("不可识别的加密类型");
                return;
            }

            WxMaXmlOutMessage outMessage = this.message(inMessage);
            if (outMessage != null) {
                if ("raw".equals(encryptType)) {
                    response.getWriter().write(outMessage.toXml());
                } else if ("aes".equals(encryptType)) {
                    response.getWriter().write(outMessage.toEncryptedXml(wxMaService.getWxMaConfig()));
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WxMaXmlOutMessage message(WxMaMessage inMessage) {
        return null;
    }
}
