package com.niu.core.service.api.login.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.login.IAuthService;
import com.niu.core.service.api.member.IMemberService;
import com.niu.core.service.api.member.param.MemberInfoParam;
import com.niu.core.service.api.member.vo.MemberInfoVo;
import com.niu.core.service.core.channel.ICoreH5Service;
import com.niu.core.service.core.channel.ICorePcService;
import com.niu.core.service.core.channel.vo.H5ConfigVo;
import com.niu.core.service.core.channel.vo.PcConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthServiceImpl implements IAuthService {

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    IMemberService memberService;

    @Resource
    ICoreH5Service coreH5Service;

    @Resource
    ICorePcService corePcService;
    /**
     * 登录 检测站点的合法性
     *
     */
    @Override
    public void checkSite() {
        Integer siteId= RequestUtils.apiSiteId();
        SiteInfoVo siteInfoVo=coreSiteService.getSiteCache(siteId);
        if(ObjectUtil.isNull(siteInfoVo) || ObjectUtil.isEmpty(siteInfoVo)){
            throw new AuthException("SITE_NOT_EXIST", 403);
        }
        String rule=RequestUtils.getReqeustURI();
        if(!rule.equals("site")){
            if(siteInfoVo.getStatus()== SiteStatusEnum.CLOSE.getCode() || siteInfoVo.getExpireTime()< DateUtils.currTime()){
                throw new AuthException("SITE_CLOSE_NOT_ALLOW", 403);
            }
        }
        RequestUtils.setSiteId(siteId);
    }

    /**
     * 校验会员和站点
     */
    @Override
    public void checkSiteAuth() {
        if(ObjectUtil.isNotNull(RequestUtils.memberId()) && RequestUtils.memberId()>0){
            MemberInfoParam memberInfoParam=new MemberInfoParam();
            memberInfoParam.setMemberId(RequestUtils.memberId());
            memberInfoParam.setSiteId(RequestUtils.siteId());
            MemberInfoVo memberInfoVo=memberService.info(memberInfoParam);
            if(ObjectUtil.isNull(memberInfoVo) || ObjectUtil.isEmpty(memberInfoVo)){
                throw new AuthException("MEMBER_NOT_EXIST", 401);
            }
        }
    }

    /**
     * 校验渠道
     */
    @Override
    public void checkChannel() {
        String channel=RequestUtils.channel();
        Integer siteId=RequestUtils.siteId();
        if (channel.equals(ChannelEnum.H5.getCode())){
            H5ConfigVo h5ConfigVo=coreH5Service.getH5(siteId);
            Integer isOpen=h5ConfigVo.getIsOpen();
            if(isOpen==0){
                throw new AuthException("SITE_CLOSE_NOT_ALLOW", 402);
            }
        }else if (channel.equals(ChannelEnum.PC.getCode())){
            PcConfigVo pcConfigVo=corePcService.getPc(siteId);
            Integer isOpen=pcConfigVo.getIsOpen();
            if(isOpen==0){
                throw new AuthException("SITE_CLOSE_NOT_ALLOW", 402);
            }
        }
    }
}
