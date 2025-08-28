package com.niu.core.service.admin.weapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.weapp.IWeappConfigService;
import com.niu.core.service.admin.weapp.param.SetDomainParam;
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
import me.chanjar.weixin.open.api.WxOpenMaService;
import me.chanjar.weixin.open.bean.ma.privacy.GetPrivacySettingResult;
import me.chanjar.weixin.open.bean.ma.privacy.SetPrivacySetting;
import me.chanjar.weixin.open.bean.result.WxOpenMaDomainResult;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedList;


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
        weappConfigVo = staticInfo(weappConfigVo);

        if (weappConfigVo.getIsAuthorization().equals(1)) {
            try {
                WxOpenMaService wxOpenMaService = (WxOpenMaService) WechatUtils.miniapp(RequestUtils.siteId());
                WxOpenMaDomainResult domainResult = wxOpenMaService.getDomain();
                if (domainResult.getErrcode().equals("0")) {
                    WeappConfigVo.Domain domain = new WeappConfigVo.Domain();
                    domain.setRequestdomain(String.join(";", domainResult.getRequestDomain()));
                    domain.setWsrequestdomain(String.join(";", domainResult.getWsRequestDomain()));
                    domain.setUploaddomain(String.join(";", domainResult.getUploadDomain()));
                    domain.setDownloaddomain(String.join(";", domainResult.getDownloadDomain()));
                    weappConfigVo.setDomain(domain);
                }
            } catch (Exception e) {
            }
        }

        return weappConfigVo;
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

    /**
     * 设置取小程序服务域名
     */
    @Override
    public void setDomain(SetDomainParam param) {
        try {
            WxOpenMaService wxOpenMaService = (WxOpenMaService) WechatUtils.miniapp(RequestUtils.siteId());
            wxOpenMaService.modifyDomain(
                "set",
                ObjectUtil.isNotEmpty(param.getRequestdomain()) ? Arrays.asList(param.getRequestdomain().split(";")) : new LinkedList<>(),
                ObjectUtil.isNotEmpty(param.getWsrequestdomain())? Arrays.asList(param.getWsrequestdomain().split(";")) : new LinkedList<>(),
                ObjectUtil.isNotEmpty(param.getUploaddomain())? Arrays.asList(param.getUploaddomain().split(";")) : new LinkedList<>(),
                ObjectUtil.isNotEmpty(param.getDownloaddomain())? Arrays.asList(param.getDownloaddomain().split(";")) : new LinkedList<>(),
                ObjectUtil.isNotEmpty(param.getTcpdomain())? Arrays.asList(param.getTcpdomain().split(";")) : new LinkedList<>(),
                ObjectUtil.isNotEmpty(param.getUdpdomain())? Arrays.asList(param.getUdpdomain().split(";")) : new LinkedList<>()
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 获取隐私设置
     */
    @Override
    public GetPrivacySettingResult getPrivacySetting() {
        try {
            WxOpenMaService wxOpenMaService = (WxOpenMaService) WechatUtils.miniapp(RequestUtils.siteId());
            return wxOpenMaService.getPrivacyService().getPrivacySetting(2);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public void setPrivacySetting(JSONObject privacySetting) {
        try {
            WxOpenMaService wxOpenMaService = (WxOpenMaService) WechatUtils.miniapp(RequestUtils.siteId());
            SetPrivacySetting setPrivacySetting = JSONUtil.toBean(privacySetting, SetPrivacySetting.class);
            setPrivacySetting.setPrivacyVer(2);
            wxOpenMaService.getPrivacyService().setPrivacySetting(setPrivacySetting);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
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
            vo.setUploadIp(InetAddress.getByName("java.oss.niucloud.com").getHostAddress());
        } catch (Exception e) {
        }
        return vo;
    }
}