package com.niu.core.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.code.QRCodeUtils;
import com.niu.core.event.sys.GetQrcodeOfChannelDefiner;
import com.qcloud.cos.utils.Base64;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

@EventCallback("core")
@Component
public class WechatQrcodeListener extends GetQrcodeOfChannelDefiner {

    @Override
    public GetQrcodeOfChannelResult handleCallback(GetQrcodeOfChannelEvent event) {
        if (event.getChannel().equals("wechat") || event.getChannel().equals("h5") ||  event.getChannel().equals("app")) {
            try {
                String url = event.getUrl();
                if (ObjectUtil.isNotEmpty(event.getPage())) url += "/" + event.getPage();
                if (ObjectUtil.isNotEmpty(event.getData())) url = HttpUtil.urlWithForm(url, event.getData(), Charset.forName("UTF-8"), false);

                GetQrcodeOfChannelResult result = new GetQrcodeOfChannelResult();

                if (event.getOutfile()) {
                    String filePath = event.getFilePath();
                    result.setPath(event.getFilePath() + "/" + QRCodeUtils.encode(url, WebAppEnvs.get().webRootDownResource + filePath));
                } else {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    QRCodeUtils.encode(url, output);
                    result.setPath("data:image/png;base64," + Base64.encodeAsString(output.toByteArray()));
                }
                return result;
            } catch (Exception e) {
                throw new CommonException(e.getMessage());
            }
        }
        return null;
    }
}
