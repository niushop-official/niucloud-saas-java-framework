package com.niu.core.controller.api.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.sys.InitWapEvent;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.api.channel.IAppService;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysInitVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class SysConfigController {

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ILoginService loginService;

    @Resource
    private IDiyService diyService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private IMemberLevelService memberLevelService;

    @Resource
    private IDiyThemeService diyThemeService;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private IAppService appService;

    /**
     * 版权信息
     *
     * @return
     */
    @GetMapping("/copyright")
    public Result<?> copyright() {
        return Result.success(sysConfigService.copyright());
    }

    /**
     * 站点信息
     *
     * @param param
     * @return
     */
    @GetMapping("/site")
    public Result<?> site(SysConfigSiteParam param) {
        return Result.success(sysConfigService.site(param));
    }

    /**
     * 场景域名
     *
     * @param param
     * @return
     */
    @GetMapping("/scene_domain")
    public Result<?> sceneDomain(SysConfigSceneDomainParam param) {
        return Result.success(sysConfigService.sceneDomain(param));
    }

    /**
     * 获取地图设置
     *
     * @return
     */
    @GetMapping("/map")
    public Result<?> map(SysConfigMapParam param) {
        return Result.success(sysConfigService.map(param));
    }

    /**
     * 获取初始化数据信息
     *
     * @param url
     * @return
     */
    @GetMapping("/init")
    public Result init(@RequestParam(name = "url", defaultValue = "") String url, @RequestParam(value = "openid", defaultValue = "") String openid) {
        SysInitVo vo = new SysInitVo();

        vo.setTabbarList(diyService.tabbarList(new DiyTabbarListParam()));
        vo.setMapConfig(sysConfigService.map(new SysConfigMapParam()));
        vo.setLoginConfig(loginService.getLoginConfig(url));

        SysInitVo.SiteInfoVo siteInfoVo = new SysInitVo.SiteInfoVo();
        BeanUtil.copyProperties(coreSiteService.getSiteCache(RequestUtils.siteId()), siteInfoVo);
        siteInfoVo.setWapUrl(sysConfigService.sceneDomain(new SysConfigSceneDomainParam()).getWapUrl());

        vo.setSiteInfo(siteInfoVo);
        vo.setMemberLevel(memberLevelService.list(new MemberLevelParam()));
        vo.setThemeList(diyThemeService.getDiyTheme());
        vo.setAppConfig(appService.getAppConfig());
        vo.setCopyright(sysConfigService.copyright());

        if (ObjectUtil.isEmpty(vo.getSiteInfo().getSiteId())) {
            InitWapEvent event = new InitWapEvent();
            event.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        Map<String, Integer> checkRes = sysConfigService.getMemberMobileExist(openid, RequestUtils.channel());
        vo.setMemberExist(checkRes.getOrDefault("member_exist", 0));
        vo.setMemberMobileExist(checkRes.getOrDefault("member_mobile_exist", 0));

        return Result.success(vo);
    }

    /**
     * 获取公众号用户是否绑定手机
     */
    @GetMapping("/member_mobile_exist")
    public Result<?> getMemberMobileExist(@RequestParam(value = "openid", defaultValue = "") String openid, @RequestHeader(value = "Channel", defaultValue = "h5") String channel) {
        return Result.success(sysConfigService.getMemberMobileExist(openid, channel));
    }

}
