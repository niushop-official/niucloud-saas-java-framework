package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.code.QRCodeUtils;
import com.niu.core.event.sys.GetQrcodeOfChannelDefiner;
import com.niu.core.service.core.weapp.ICoreWeappService;
import com.qcloud.cos.utils.Base64;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;

@EventCallback("core")
@Component
public class WeappQrcodeListener extends GetQrcodeOfChannelDefiner {

    @Resource
    ICoreWeappService coreWeappService;

    @Override
    public GetQrcodeOfChannelResult handleCallback(GetQrcodeOfChannelEvent event) {
        if (event.getChannel().equals("weapp")) {
            try {
                GetQrcodeOfChannelResult result = new GetQrcodeOfChannelResult();

                if (event.getOutfile()) {
                    String filePath = event.getFilePath() + "/";
                    File qrcode = coreWeappService.qrcodeFile(event.getSiteId(), WebAppEnvs.get().webRootDownResource + filePath, event.getPage(), event.getData(), 430);
                    result.setPath(filePath + qrcode.getName());
                } else {
                    result.setPath("data:image/png;base64," + Base64.encodeAsString(coreWeappService.qrcodeBytes(event.getSiteId(), event.getPage(), event.getData(), 430)));
                }
                return result;
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
        return null;
    }
}
