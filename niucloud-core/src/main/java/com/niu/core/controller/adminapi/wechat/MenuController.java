package com.niu.core.controller.adminapi.wechat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.wechat.IWechatMenuService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 */
@RestController
@RequestMapping("adminapi/wechat")
@SaCheckLogin
public class MenuController {

    @Resource
    IWechatMenuService wechatMenuService;

    /**
     * 获取微信菜单配置
     */
    @GetMapping("/menu")
    public Result<JSONArray> info() {
        return Result.success(wechatMenuService.info());
    }

    /**
     * 设置微信菜单配置
     * @return Result<Object>
     */
    @PutMapping("/menu")
    public Result<Object> edit(@Validated @RequestBody JSONObject data) {
        JSONArray button = data.getJSONArray("button");
        wechatMenuService.edit(button);
        return Result.success();
    }
}
