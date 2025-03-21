package com.niu.core.controller.adminapi.channel;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.channel.ICorePcService;
import com.niu.core.service.core.channel.param.SetPcParam;
import com.niu.core.service.core.channel.vo.PcConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * pc端
 */
@RestController
@RequestMapping("/adminapi/channel/pc")
@SaCheckLogin
public class PcController {

    @Resource
    ICorePcService corePcService;

    /**
     * 获取h5端配置
     * @return Result<H5ConfigVo>
     */
    @GetMapping("/config")
    public Result<PcConfigVo> getPc() {
        Integer siteId= RequestUtils.siteId();
        return Result.success(corePcService.getPc(siteId));
    }

    /**
     * 设置h5端配置
     * @return Result
     */
    @PutMapping("/config")
    public Result<Object> setH5(@Validated @RequestBody SetPcParam param) {
        corePcService.setPc(param);
        return Result.success();
    }
}
