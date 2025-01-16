package com.niu.core.service.core.weapp.impl;

import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.core.weapp.ICoreWeappService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreWeappServiceImpl implements ICoreWeappService {

    @Override
    public byte[] qrcodeBytes(Integer siteId, String page, Map<String, Object> data, Integer width) {
        try {
            List<String> scene = new LinkedList<>();
            for (String key : data.keySet()) {
                scene.add(data.get(key) + "-" + data.get(key).toString());
            }

            return WechatUtils.miniapp(siteId).getQrcodeService().createWxaCodeUnlimitBytes(
                    String.join("&", scene),
                    page,
                    false,
                    "release",
                    width,
                    false,
                    null,
                    false
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public File qrcodeFile(Integer siteId, String filePath, String page, Map<String, Object> data, Integer width) {
        try {
            List<String> scene = new LinkedList<>();
            for (String key : data.keySet()) {
                scene.add(data.get(key) + "-" + data.get(key).toString());
            }

            return WechatUtils.miniapp(siteId).getQrcodeService().createWxaCodeUnlimit(
                    String.join("&", scene),
                    page,
                    filePath,
                    false,
                    "release",
                    width,
                    false,
                    null,
                    false
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
