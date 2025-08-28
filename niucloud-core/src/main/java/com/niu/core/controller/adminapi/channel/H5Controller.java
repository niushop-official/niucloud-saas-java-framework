package com.niu.core.controller.adminapi.channel;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.channel.ICoreH5Service;
import com.niu.core.service.core.channel.param.SetH5Param;
import com.niu.core.service.core.channel.vo.H5ConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jdk.jfr.Description;

/**
 * h5端
 */
@Description("h5端")
@RestController
@RequestMapping("/adminapi/channel/h5")
@SaCheckLogin
public class H5Controller {

    @Resource
    ICoreH5Service coreH5Service;

    /**
     * 获取h5端配置
     * @return Result<H5ConfigVo>
     */
    @Description("获取h5端配置")
    @GetMapping("/config")
    public Result<H5ConfigVo> getH5() {
        Integer siteId= RequestUtils.siteId();
        return Result.success(coreH5Service.getH5(siteId));
    }

    /**
     * 设置h5端配置
     * @return Result
     */
    @Description("设置h5端配置")
    @PutMapping("/config")
    public Result<Object> setH5(@Validated @RequestBody SetH5Param param) {
        coreH5Service.setH5(param);
        return Result.success();
    }
}
