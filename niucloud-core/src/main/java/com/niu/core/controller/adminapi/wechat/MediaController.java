package com.niu.core.controller.adminapi.wechat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatMediaParam;
import com.niu.core.service.admin.wechat.param.WechatMediaSearchParam;
import com.niu.core.service.admin.wechat.IWechatMediaService;
import com.niu.core.service.admin.wechat.vo.WechatMediaInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatMediaListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

/**
 * 微信素材控制器
 */

@RestController
@RequestMapping("adminapi/wechat")
@SaCheckLogin
public class MediaController {

    @Resource
    IWechatMediaService wechatMediaService;

    /**
     * 微信素材列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<WechatMediaListVo>>
     */
    @GetMapping("/media")
    public Result<PageResult<WechatMediaListVo>> list(@Validated PageParam pageParam, @Validated WechatMediaSearchParam searchParam) {
        PageResult<WechatMediaListVo> list = wechatMediaService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 上传图片素材
     */
    @PostMapping("/media/image")
    public Result image(@RequestParam("file") MultipartFile file) {
        return Result.success(wechatMediaService.image(file));
    }

    /**
     * 上传图片素材
     */
    @PostMapping("/media/video")
    public Result video(@RequestParam("file") MultipartFile file) {
        return Result.success(wechatMediaService.video(file));
    }

    /**
     * 同步图文
     * @return
     */
    @GetMapping("/sync/news")
    public Result syncNews() {
        wechatMediaService.syncNews(1);
        return Result.success();
    }
}
