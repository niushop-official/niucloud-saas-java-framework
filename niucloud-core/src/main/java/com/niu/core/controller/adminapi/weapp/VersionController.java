package com.niu.core.controller.adminapi.weapp;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.weapp.IWeappTemplateService;
import com.niu.core.service.admin.weapp.IWeappVersionService;
import com.niu.core.service.admin.weapp.param.WeappTemplateSyncParam;
import com.niu.core.service.admin.weapp.param.WeappVersionAddParam;
import com.niu.core.service.admin.weapp.vo.WeappVersionListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 小程序版本管理
 */
@RestController
@RequestMapping("adminapi/weapp")
@SaCheckLogin
public class VersionController {

    @Resource
    IWeappVersionService weappVersionService;

    /**
     * 上传版本
     *
     * @return Result<PageResult<WechatReplyListVo>>
     */
    @PostMapping("/version")
    public Result add(@RequestBody WeappVersionAddParam param) {
        weappVersionService.add(param);
        return Result.success();
    }

    /**
     * 版本列表
     *
     * @return Result
     */
    @GetMapping("/version")
    public Result<PageResult<WeappVersionListVo>> list(@Validated PageParam pageParam) {
        return Result.success(weappVersionService.list(pageParam));
    }

    /**
     * 获取预览码
     *
     * @return Result
     */
    @GetMapping("/preview")
    public Result preview() {
        return Result.success("", weappVersionService.getWeappPreviewImage());
    }

    /**
     * 获取小程序上传日志
     *
     * @return Result
     */
    @GetMapping("/upload/{key}")
    public Result uploadLog(@PathVariable String key) {
        return Result.success(weappVersionService.getWeappCompileLog(key));
    }
}
