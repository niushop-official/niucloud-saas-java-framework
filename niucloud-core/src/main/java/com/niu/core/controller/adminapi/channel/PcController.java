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
import jdk.jfr.Description;

/**
 * pc端
 */
@Description("pc端")
@RestController
@RequestMapping("/adminapi/channel/pc")
@SaCheckLogin
public class PcController {

    @Resource
    ICorePcService corePcService;

    /**
     * 获取pc端配置
     * @return Result<H5ConfigVo>
     */
    @Description("获取pc端配置")
    @GetMapping("/config")
    public Result<PcConfigVo> getPc() {
        Integer siteId= RequestUtils.siteId();
        return Result.success(corePcService.getPc(siteId));
    }

    /**
     * 设置pc端配置
     * @return Result
     */
    @Description("设置pc端配置")
    @PutMapping("/config")
    public Result<Object> setH5(@Validated @RequestBody SetPcParam param) {
        corePcService.setPc(param);
        return Result.success();
    }
}
