package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.sys.param.SysCopyRightParam;
import com.niu.core.service.admin.sys.vo.SysCopyRightVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("adminapi/sys/web")
@SaCheckLogin
public class SysWebConfigController {

    @Resource
    ISysConfigService sysConfigService;

    @Resource
    ISiteService siteService;

    /**
     * 获取站点信息
     * @return
     */
    @GetMapping("website")
    public Result<SiteInfoVo> getWebSite(){
        return Result.success(siteService.info(RequestUtils.siteId()));
    }
    /**
     * 获取版权信息
     * @return
     */
    @GetMapping("/copyright")
    public Result<SysCopyRightVo> getCopyRight(){
        return Result.success(sysConfigService.getCopyRight());
    }

    /**
     * 获取开发者key
     * @return
     */
    @GetMapping("layout")
    public Result<JSONObject> getLayOut(){
        return Result.success(sysConfigService.getLayout());
    }

    /**
     * 获取服务器重启结果
     * @return
     */
    @GetMapping("/restart")
    public Result isRestart() {
        return Result.success();
    }
}
