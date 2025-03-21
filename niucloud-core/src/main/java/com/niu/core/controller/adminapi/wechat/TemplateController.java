package com.niu.core.controller.adminapi.wechat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wechat.IWechatReplyService;
import com.niu.core.service.admin.wechat.IWechatTemplateService;
import com.niu.core.service.admin.wechat.param.*;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 公众号模板消息控制器
 */

@RestController
@RequestMapping("adminapi/wechat/template")
@SaCheckLogin
public class TemplateController {

    @Resource
    IWechatTemplateService wechatTemplateService;

    /**
     * 获取模板消息
     * @return Result<PageResult<WechatReplyListVo>>
     */
    @GetMapping("")
    public Result<List<AddonNoticeListVo>> list() {
        return Result.success(wechatTemplateService.list());
    }

    /**
     * 同步模板消息
     * @return Result
     */
    @PutMapping("/sync")
    public Result editKeyword(@RequestBody WechatTemplateSyncParam param) {
        wechatTemplateService.sync(param);
        return Result.success();
    }
}
