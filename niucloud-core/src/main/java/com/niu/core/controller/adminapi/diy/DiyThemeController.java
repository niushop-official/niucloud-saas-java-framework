package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.diy.IDiyThemeService;
import com.niu.core.service.admin.diy.param.DiyThemeColorParam;
import com.niu.core.service.admin.diy.param.DiyThemeParam;
import com.niu.core.service.admin.diy.param.DiyThemeSetParam;
import jakarta.annotation.Resource;
import jdk.jfr.Description;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 主题管理
 */
@Description("主题管理")
@RestController
@RequestMapping("/adminapi/diy/theme")
@SaCheckLogin
public class DiyThemeController {

    @Resource
    IDiyThemeService diyThemeService;

    /**
     * 获取主题色
     * @return
     */
    @Description("获取主题色")
    @GetMapping("")
    public Result<JSONObject> getDiyTheme() {
        return Result.success(diyThemeService.getDiyTheme());
    }

    /**
     * 设置主题
     *
     * @return Result<Object>
     */
    @Description("设置主题")
    @PostMapping("")
    public Result<Object> setDiyTheme(@Validated @RequestBody DiyThemeSetParam diyThemeSetParam) {
        diyThemeService.setDiyTheme(diyThemeSetParam);
        return Result.success();
    }


    /**
     * 获取默认主题色
     * @return
     */
    @Description("获取默认主题色")
    @GetMapping("/color")
    public Result<Object> getDefaultThemeColor(@Validated DiyThemeColorParam data) {
        return Result.success(diyThemeService.getDefaultThemeColor(data));
    }

    /**
     * 添加主题
     *
     * @return Result<Object>
     */
    @Description("添加主题")
    @PostMapping("/add")
    public Result<Object> addDiyTheme(@Validated @RequestBody DiyThemeParam diyThemeParam) {
        diyThemeService.addDiyTheme(diyThemeParam);
        return Result.success();
    }

    /**
     * 主题色编辑
     *
     * @return Result<Object>
     */
    @Description("主题色编辑")
    @PutMapping("/{id}")
    public Result<Object> editDiyTheme(@PathVariable("id") Integer id, @Validated @RequestBody DiyThemeParam diyThemeParam) {
        diyThemeService.editDiyTheme(id, diyThemeParam);
        return Result.success();
    }

    /**
     * 主题色删除
     *
     * @return Result<Object>
     */
    @Description("主题色删除")
    @DeleteMapping("/delete/{id}")
    public Result<Object> delDiyTheme(@PathVariable("id") Integer id) {
        diyThemeService.delDiyTheme(id);
        return Result.success();
    }

}
