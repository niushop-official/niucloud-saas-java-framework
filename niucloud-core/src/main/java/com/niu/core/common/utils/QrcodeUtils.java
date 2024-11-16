package com.niu.core.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.upload.exception.UploadException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.code.QRCodeUtils;
import com.niu.core.event.sys.GetQrcodeOfChannelDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class QrcodeUtils {

    /**
     * 创建二维码并生成文件
     * @return
     */
    public static String qrcodeToFile(Integer siteId, String channel, String url, String page, Map<String, Object> data, String dir) {
        if (ObjectUtil.isEmpty(dir)) dir = "upload/qrcode/" + siteId;

        String savePath = WebAppEnvs.get().webRootDownResource + dir;

        File fileExist = new File(savePath);
        if (!fileExist.exists()) {
            if (!fileExist.mkdirs()) {
                throw new UploadException("创建二维码存放目录失败");
            }
        }

        String filePath = dir + "/" + DigestUtil.md5Hex(url + String.valueOf(System.currentTimeMillis())) + ".png";
        return createQrcode(url, page, data, siteId, channel, true, filePath);
    }

    /**
     * 创建二维码
     * @return
     */
    public static String qrcode(Integer siteId, String channel, String url, String page, Map<String, Object> data) {
        return createQrcode(url, page, data, siteId, channel, false, "");
    }

    /**
     * 创建二维码
     *
     * @param url
     * @param page
     * @param data
     * @param siteId
     * @param channel
     * @param isOutfile
     * @param filePath
     * @return
     */
    public static String createQrcode(String url, String page, Map<String, Object> data, Integer siteId, String channel, Boolean isOutfile, String filePath) {
        GetQrcodeOfChannelDefiner.GetQrcodeOfChannelEvent evnet = new GetQrcodeOfChannelDefiner.GetQrcodeOfChannelEvent();
        evnet.setName("GetQrcodeOfChannelEvent");
        evnet.setSiteId(siteId);
        evnet.setUrl(url);
        evnet.setPage(page);
        evnet.setData(data);
        evnet.setChannel(channel);
        evnet.setOutfile(isOutfile);
        evnet.setFilePath(filePath);

        try {
            List<GetQrcodeOfChannelDefiner.GetQrcodeOfChannelResult> list = EventAndSubscribeOfPublisher.publishAndCallback(evnet);
            if (list.size() > 0) {
                return list.get(0).getPath();
            }
            return "";
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
