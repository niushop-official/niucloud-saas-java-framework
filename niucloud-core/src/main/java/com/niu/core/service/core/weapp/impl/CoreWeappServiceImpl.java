package com.niu.core.service.core.weapp.impl;

import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.ICoreWeappService;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.weapp.vo.WeappVersionVo;
import com.niu.core.service.core.wxoplatform.ICoreOplatformService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.open.bean.result.WxOpenVersioninfoResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreWeappServiceImpl implements ICoreWeappService {

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    @Resource
    ICoreOplatformService coreOplatformService;

    @Override
    public byte[] qrcodeBytes(Integer siteId, String page, Map<String, Object> data, Integer width) {
        try {
            List<String> scene = new LinkedList<>();
            for (String key : data.keySet()) {
                scene.add(key + "-" + data.get(key).toString());
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
                scene.add(key + "-" + data.get(key).toString());
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

    @Override
    public WeappVersionVo getWeappVersion(Integer siteId) {
        WeappConfigVo config = coreWeappConfigService.getWeappConfig(siteId);
        if (config.getIsAuthorization() != 1) return null;

        WxOpenVersioninfoResult version = coreOplatformService.weappVersion(siteId);
        if (version == null) return null;

        if (version.isSuccess()) {
            WeappVersionVo vo = new WeappVersionVo();
            vo.setReleaseVersion(version.getReleaseInfo().getReleaseVersion());
            vo.setReleaseTime(DateUtils.timestampToString(version.getReleaseInfo().getReleaseTime()));
            vo.setExpVersion(version.getExpInfo().getExpVersion());
            vo.setExpTime(DateUtils.timestampToString(version.getExpInfo().getExpTime()));
            return vo;
        }

        return null;
    }
}
