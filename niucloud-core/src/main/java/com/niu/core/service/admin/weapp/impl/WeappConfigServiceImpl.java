package com.niu.core.service.admin.weapp.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.weapp.IWeappConfigService;
import com.niu.core.service.admin.weapp.vo.WeappStaticInfoVo;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;


/**
 * 公众号消息回调实现
 */
@Service
public class WeappConfigServiceImpl implements IWeappConfigService {

    @Resource
    ICoreWeappConfigService coreWeappConfigService;

    @Resource
    ISysConfigService sysConfigService;

    /**
     * 获取公众号配置
     */
    @Override
    public WeappConfigVo getWeappConfig() {
        WeappConfigVo weappConfigVo = coreWeappConfigService.getWeappConfig(RequestUtils.siteId());
        return staticInfo(weappConfigVo);
    }

    /**
     * 设置小程序配置
     *
     * @param weappConfigParam
     */
    @Override
    public void setWeappConfig(WeappConfigParam weappConfigParam) {
        coreWeappConfigService.setWeappConfig(RequestUtils.siteId(), weappConfigParam);
    }

    private WeappConfigVo staticInfo(WeappConfigVo vo) {
        SceneDomainVo sysUrl = sysConfigService.getUrl();
        HttpServletRequest request = RequestUtils.handler();

        vo.setServeUrl(request.getScheme() + "://" + request.getServerName() + "/api/weapp/serve/" + RequestUtils.siteId());
        vo.setRequestUrl(request.getServerName());
        vo.setSocketUrl("wss://" + request.getHeader("host"));
        vo.setDownloadUrl(request.getServerName());
        vo.setUploadUrl(request.getServerName());

        try {
            vo.setUploadIp(InetAddress.getByName("oss.niucloud.com").getHostAddress());
        } catch (Exception e) {
        }
        return vo;
    }
}