package com.niu.core.controller.api.sys;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.sys.InitWapEvent;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import com.niu.core.service.api.sys.vo.SysInitVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

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

    /**
     * 版权信息
     *
     * @param param
     * @return
     */
    @GetMapping("/copyright")
    public Result<?> copyright(SysConfigCopyrightParam param) {
        return Result.success(sysConfigService.copyright(param));
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
    public Result init(@RequestParam(name = "url", defaultValue = "") String url) {
        var vo = new SysInitVo();

        vo.setTabbarList(diyService.tabbarList(new DiyTabbarListParam()));
        vo.setMapConfig(sysConfigService.map(new SysConfigMapParam()));
        vo.setLoginConfig(loginService.getLoginConfig());
        vo.setSiteInfo(coreSiteService.getSiteCache(RequestUtils.siteId()));
        vo.setMemberLevel(memberLevelService.list(new MemberLevelParam()));

        if (ObjectUtil.isEmpty(vo.getSiteInfo().getSiteId())) {
            InitWapEvent event = new InitWapEvent();
            event.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        return Result.success(vo);
    }
}
