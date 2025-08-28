package com.niu.core.controller.adminapi.weapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.weapp.IWeappTemplateService;
import com.niu.core.service.admin.weapp.param.WeappTemplateSyncParam;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import jdk.jfr.Description;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 小程序订阅模板消息控制器
 */

@RestController
@RequestMapping("adminapi/weapp/template")
@SaCheckLogin
@Description("小程序订阅消息")
public class TemplateController {

    @Resource
    IWeappTemplateService weappTemplateService;

    /**
     * 获取模板消息
     * @return Result<PageResult<WechatReplyListVo>>
     */
    @GetMapping("")
    @Description("获取模板消息")
    public Result<List<AddonNoticeListVo>> list() {
        return Result.success(weappTemplateService.list());
    }

    /**
     * 同步模板消息
     * @return Result
     */
    @PutMapping("/sync")
    @Description("同步模板消息")
    public Result editKeyword(@RequestBody WeappTemplateSyncParam param) {
        weappTemplateService.sync(param);
        return Result.success();
    }
}
