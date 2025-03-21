package com.niu.core.controller.adminapi.wxoplatform;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import com.niu.core.service.admin.wxoplatform.IOplatformServerService;
import com.niu.core.service.admin.wxoplatform.param.OplatformServerParam;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/wxoplatform")
@SaIgnore
public class ServerController {

    @Resource
    IOplatformServerService oplatformServerService;

    /**
     * 第三方平台授权事件接收
     */
    @PostMapping(value = "/server", consumes = MediaType.TEXT_XML_VALUE)
    public String server(@RequestBody(required = false) String requestBody,
                         @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce,
                         @RequestParam("signature") String signature,
                         @RequestParam(name = "encrypt_type", required = false) String encType,
                         @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        OplatformServerParam param = new OplatformServerParam();
        param.setEncryptType(encType);
        param.setSignature(signature);
        param.setMsgSignature(msgSignature);
        param.setTimestamp(timestamp);
        param.setNonce(nonce);
        param.setRequestBody(requestBody);
        oplatformServerService.server(param);
        return "success";
    }

    /**
     * 第三方平台消息与事件接收
     */
    @PostMapping(value = "/message/{appid}", consumes = MediaType.TEXT_XML_VALUE)
    public String message(@RequestBody(required = false) String requestBody,
                          @PathVariable("appid") String appid,
                          @RequestParam("signature") String signature,
                          @RequestParam("timestamp") String timestamp,
                          @RequestParam("nonce") String nonce,
                          @RequestParam("openid") String openid,
                          @RequestParam("encrypt_type") String encType,
                          @RequestParam("msg_signature") String msgSignature) {
        OplatformServerParam param = new OplatformServerParam();
        param.setEncryptType(encType);
        param.setSignature(signature);
        param.setMsgSignature(msgSignature);
        param.setTimestamp(timestamp);
        param.setNonce(nonce);
        param.setRequestBody(requestBody);
        return oplatformServerService.message(appid, param);
    }
}
