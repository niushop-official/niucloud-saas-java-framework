package com.niu.core.service.admin.wechat.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.wechat.WechatReply;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.mapper.wechat.WechatReplyMapper;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.IWechatReplyService;
import com.niu.core.service.admin.wechat.param.WechatReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplySearchParam;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 公众号消息回调实现
 */
@Service
public class WechatConfigServiceImpl implements IWechatConfigService {

    @Resource
    ICoreWechatConfigService coreWechatConfigService;

    @Resource
    ISysConfigService sysConfigService;

    /**
     * 获取公众号配置
     */
    @Override
    public WechatConfigVo getWechatConfig() {
        return coreWechatConfigService.getWechatConfig(RequestUtils.siteId());
    }

    /**
     * 设置公众号配置
     *
     * @param wechatConfigParam
     */
    @Override
    public void setWechatConfig(WechatConfigParam wechatConfigParam) {
        coreWechatConfigService.setWechatConfig(RequestUtils.siteId(), wechatConfigParam);
    }

    @Override
    public WechatStaticInfoVo staticInfo() {
        SceneDomainVo sysUrl = sysConfigService.getUrl();
        HttpServletRequest request = RequestUtils.handler();

        WechatStaticInfoVo vo = new WechatStaticInfoVo();
        vo.setServeUrl(request.getScheme() + "://" + request.getServerName() + "/api/wechat/serve/" + RequestUtils.siteId());
        vo.setBusinessDomain(sysUrl.getWapDomain());
        vo.setJsSecureDomain(sysUrl.getWapDomain());
        vo.setWebAuthDomain(sysUrl.getWapDomain());
        vo.setEncryptionType(WechatEncryptionTypeEnum.getMap());

        return vo;
    }
}