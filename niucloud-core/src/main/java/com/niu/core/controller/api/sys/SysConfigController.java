package com.niu.core.controller.api.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.SysConfigCopyrightParam;
import com.niu.core.service.api.sys.param.SysConfigMapParam;
import com.niu.core.service.api.sys.param.SysConfigSceneDomainParam;
import com.niu.core.service.api.sys.param.SysConfigSiteParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
@Slf4j
public class SysConfigController {

    @Resource
    private ISysConfigService sysConfigService;

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

}
