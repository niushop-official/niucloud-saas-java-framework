package com.niu.core.service.admin.wxoplatform.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.sys.ConfigKeyEnum;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.admin.wxoplatform.IOplatformService;
import com.niu.core.service.admin.wxoplatform.param.AuthorizationParam;
import com.niu.core.service.admin.wxoplatform.param.IOplatformAuthRecordParam;
import com.niu.core.service.admin.wxoplatform.vo.OplatformRecordVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.upload.ICoreFetchService;
import com.niu.core.service.core.weapp.ICoreWeappConfigService;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizationInfo;
import me.chanjar.weixin.open.bean.auth.WxOpenAuthorizerInfo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OplatformServiceImpl implements IOplatformService {

    private final ICoreWeappConfigService coreWeappConfigService;
    private final ICoreWechatConfigService coreWechatConfigService;
    private final SysConfigMapper sysConfigMapper;
    private final ICoreConfigService coreConfigService;
    private final ICoreFetchService coreFetchService;

    /**
     * 获取预授权链接
     * @return
     */
    @Override
    public String createPreAuthorizationUrl() {
        try {
            String url = RequestUtils.getDomain(true) + "/site/wxoplatform/callback";
            return WechatUtils.WxOpen().getWxOpenComponentService().getPreAuthUrl(url);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 授权
     * @param param
     */
    @Override
    public void authorization(AuthorizationParam param) {
        try {
            WxOpenQueryAuthResult queryAuth = WechatUtils.WxOpen().getWxOpenComponentService().getQueryAuth(param.getAuthCode());

            // 授权账号信息 授权信息
            WxOpenAuthorizerInfoResult result = WechatUtils.WxOpen().getWxOpenComponentService().getAuthorizerInfo(queryAuth.getAuthorizationInfo().getAuthorizerAppid());

            WxOpenAuthorizerInfo authorizerInfo = result.getAuthorizerInfo();
            WxOpenAuthorizationInfo authorization = result.getAuthorizationInfo();

            // 小程序
            if (ObjectUtil.isNotEmpty(authorizerInfo.getMiniProgramInfo())) {
                this.weappCheck(authorizerInfo, authorization);

                WeappConfigParam weappConfig = new WeappConfigParam();
                weappConfig.setAppId(authorization.getAuthorizerAppid());
                weappConfig.setWeappName(authorizerInfo.getNickName());
                weappConfig.setWeappOriginal(authorizerInfo.getUserName());
                weappConfig.setIsAuthorization(1);
                weappConfig.setQrCode(coreFetchService.image(authorizerInfo.getQrcodeUrl(), RequestUtils.siteId()));
                coreWeappConfigService.setWeappConfig(RequestUtils.siteId(), weappConfig);

                coreWeappConfigService.setWeappAuthorizationInfo(RequestUtils.siteId(), result);
            } else { // 公众号
                this.wechatCheck(authorizerInfo, authorization);

                WechatConfigParam wechatConfig = new WechatConfigParam();
                wechatConfig.setAppId(authorization.getAuthorizerAppid());
                wechatConfig.setWechatName(authorizerInfo.getNickName());
                wechatConfig.setWechatOriginal(authorizerInfo.getUserName());
                wechatConfig.setIsAuthorization(1);
                wechatConfig.setQrcode(coreFetchService.image(authorizerInfo.getQrcodeUrl(), RequestUtils.siteId()));
                coreWechatConfigService.setWechatConfig(RequestUtils.siteId(), wechatConfig);

                coreWechatConfigService.setWechatAuthorizationInfo(RequestUtils.siteId(), result);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 授权小程序校验
     * @param authorizerInfo
     * @param authorization
     */
    private void weappCheck(WxOpenAuthorizerInfo authorizerInfo, WxOpenAuthorizationInfo authorization) {
        SysConfig exist = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>()
                .like("value", authorization.getAuthorizerAppid())
                .eq("config_key", ConfigKeyEnum.WEAPP.getName())
                .ne("site_id", RequestUtils.siteId())
        );
        if (exist != null) throw new CommonException("该小程序已经授权给其他站点");

        if (!authorizerInfo.getServiceTypeInfo().equals(0) || authorizerInfo.getVerifyTypeInfo().equals(-1)) throw new CommonException("请使用已认证的小程序进行授权");

        Map<Integer, String> authority = new HashMap<>();
        authority.put(17, "获取小程序码");
        authority.put(19, "小程序客服管理");
        authority.put(25, "开放平台账号管理");
        authority.put(30, "小程序基本信息管理");
        authority.put(31, "小程序认证名称检测");
        authority.put(40, "小程序插件管理");
        authority.put(49, "小程序云开发管理");
        authority.put(52, "小程序直播管理");
        authority.put(65, "小程序广告管理");
        authority.put(70, "标准版交易组件商品管理");
        authority.put(71, "标准版交易组件订单物流与售后管理");
        authority.put(73, "标准版交易组件接入");
        authority.put(76, "小程序违规与交易投诉管理");
        authority.put(81, "试用小程序快速认证");
        authority.put(84, "标准版交易组件优惠券管理");
        authority.put(85, "自定义版交易组件管理");
        authority.put(88, "小程序链接管理");
        authority.put(116, "获取自定义版交易组件数据");
        authority.put(118, "硬件服务");
        authority.put(119, "小程序支付管理服务");
        authority.put(120, "小程序购物订单");
        authority.put(139, "小程序运费险");
        authority.put(142, "小程序发货管理服务");
        authority.put(157, "小程序虚拟支付管理权限");
        authority.put(161, "微信客服管理");

        for (Integer i : authority.keySet()) {
            if (!authorization.getFuncInfo().contains(i)) throw new CommonException("请将"+ authority.get(i) +"的权限授权给我们");
        }
    }

    /**
     * 公众号校验
     * @param authorizerInfo
     * @param authorization
     */
    private void wechatCheck(WxOpenAuthorizerInfo authorizerInfo, WxOpenAuthorizationInfo authorization) {
        SysConfig exist = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>()
                .like("value", authorization.getAuthorizerAppid())
                .eq("config_key", ConfigKeyEnum.WECHAT.getName())
                .ne("site_id", RequestUtils.siteId())
        );
        if (exist != null) throw new CommonException("该公众号已经授权给其他站点");

        if (!authorizerInfo.getServiceTypeInfo().equals(-2) || authorizerInfo.getVerifyTypeInfo().equals(-1)) throw new CommonException("请使用已认证的服务号进行授权");

        Map<Integer, String> authority = new HashMap<>();
        authority.put(1, "消息管理");
        authority.put(2, "用户管理");
        authority.put(3, "账号服务");
        authority.put(4, "网页服务");
        authority.put(6, "微信多客服");
        authority.put(7, "群发与通知");
        authority.put(8, "微信卡券");
        authority.put(9, "微信扫一扫");
        authority.put(11, "素材管理");
        authority.put(15, "菜单管理");
        authority.put(27, "快速注册小程序");
        authority.put(33, "小程序关联管理");
        authority.put(54, "服务号对话管理");
        authority.put(89, "服务号订阅通知");

        for (Integer i : authority.keySet()) {
            if (!authorization.getFuncInfo().contains(i)) throw new CommonException("请将"+ authority.get(i) +"的权限授权给我们");
        }
    }

    /**
     * 取消授权
     * @param appid
     */
    public void clearAuthorization(String appid) {
        String[] configKey = new String[]{ ConfigKeyEnum.WECHAT.getName(), ConfigKeyEnum.WEAPP.getName(), ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName(), ConfigKeyEnum.WECHAT_AUTHORIZATION_INFO.getName()};
        sysConfigMapper.delete(new QueryWrapper<SysConfig>().like("value", appid).in("config_key", configKey));
        coreConfigService.cacheClear();
    }

    @Override
    public PageResult<OplatformRecordVo> getAuthRecord(PageParam pageParam, IOplatformAuthRecordParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJLambdaWrapper<SysConfig> wrapper = new MPJLambdaWrapper<SysConfig>();
        wrapper.select(SysConfig::getCreateTime,SysConfig::getAddon,SysConfig::getStatus,SysConfig::getConfigKey, SysConfig::getValue, SysConfig::getSiteId, SysConfig::getUpdateTime);
        wrapper.leftJoin(Site.class, "siteinfo",Site::getSiteId, SysConfig::getSiteId).select("site_name");
        wrapper.in(SysConfig::getConfigKey, ConfigKeyEnum.WECHAT_AUTHORIZATION_INFO.getName(), ConfigKeyEnum.WEAPP_AUTHORIZATION_INFO.getName());
        wrapper.eq(SysConfig::getSiteId, RequestUtils.siteId());
        wrapper.orderByDesc(SysConfig::getUpdateTime);
        Page<SysConfig> pageObj = new Page<>(page, limit);
        IPage<SysConfig> iPage = sysConfigMapper.selectPage(pageObj, wrapper);
        List<OplatformRecordVo> listInfo = new LinkedList<>();
        for (SysConfig item : iPage.getRecords()) {
            OplatformRecordVo vo = new OplatformRecordVo();
            BeanUtils.copyProperties(item, vo);
            vo.setValue(JSONUtil.parseObj(item.getValue()));
            Site siteInfo = new Site();
            siteInfo.setSiteName(item.getSiteName());
            vo.setSite(siteInfo);
            listInfo.add(vo);
        }
        return  PageResult.build(page, limit, iPage.getTotal()).setData(listInfo);
    }
}
