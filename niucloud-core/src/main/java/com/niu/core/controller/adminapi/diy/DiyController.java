package com.niu.core.controller.adminapi.diy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.DiyPageInitParam;
import com.niu.core.service.admin.diy.param.DiyPageParam;
import com.niu.core.service.admin.diy.param.DiyPageSearchParam;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.admin.diy.vo.DiyPageInfoVo;
import com.niu.core.service.admin.diy.vo.DiyPageListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 自定义页面管理
 */
@RestController
@RequestMapping("/adminapi/diy")
@SaCheckLogin
@Description("自定义页面管理")
public class DiyController {

    @Resource
    IDiyService diyPageService;

    @Resource
    ICoreAddonService coreAddonService;

    /**
     * 自定义页面分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("/diy")
    @Description("自定义页面分页列表")
    public Result<PageResult<DiyPageListVo>> list(@Validated PageParam pageParam, @Validated DiyPageSearchParam searchParam) {
        PageResult<DiyPageListVo> list = diyPageService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 获取自定义页面列表
     * @param searchParam
     * @return
     */
    @GetMapping("/list")
    @Description("获取自定义页面列表")
    public Result<List<DiyPageListVo>> allList(DiyPageSearchParam searchParam) {
        return Result.success(diyPageService.allList(searchParam));
    }

    /**
     * 自定义页面详情
     *
     * @param id 主键ID
     * @return Result<DiyPageInfoVo>
     */
    @GetMapping("/diy/{id}")
    @Description("自定义页面详情")
    public Result<DiyPageInfoVo> info(@PathVariable("id") Integer id) {
        DiyPageInfoVo info = diyPageService.info(id);
        return Result.success(info);
    }

    /**
     * 自定义页面添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/diy")
    @Description("自定义页面添加")
    public Result<Object> add(@Validated @RequestBody DiyPageParam addParam) {
        diyPageService.add(addParam);
        return Result.success();
    }

    /**
     * 自定义页面编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/diy/{id}")
    @Description("自定义页面编辑")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody DiyPageParam editParam) {
        diyPageService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 自定义页面删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/diy/{id}")
    @Description("自定义页面删除")
    public Result<Object> del(@PathVariable("id") Integer id) {
        diyPageService.del(id);
        return Result.success();
    }

    /**
     * 页面初始化数据
     * @param
     * @return
     */
    @GetMapping("/init")
    @Description("页面初始化数据")
    public Result<DiyPageInfoVo> getPageInit(@Validated DiyPageInitParam param) {
        return Result.success(diyPageService.getPageInit(param));
    }

    /**
     * 存在的应用插件列表
     *
     * @return
     */
    @GetMapping("/apps")
    @Description("存在的应用插件列表")
    public Result<Map<String, InstallAddonListVo>> info() {
        return Result.success(coreAddonService.getInstallAddonList());
    }

    /**
     * 获取自定义链接列表
     * @return
     */
    @GetMapping("/link")
    @Description("获取自定义链接列表")
    public Result<JSONObject> getLink() {
        return Result.success(diyPageService.getLink());
    }

    @Data
    public static class Id {
        private Integer id;
    }

    /**
     * 设为使用
     *
     * @return Result<Object>
     */
    @PutMapping("/use/{id}")
    @Description("设置为使用")
    public Result<Object> setUse(@PathVariable("id") Integer id) {
        diyPageService.setUse(id);
        return Result.success();
    }

    /**
     * 获取页面模板
     * @return
     */
    @GetMapping("/template")
    @Description("获取页面模板")
    public Result<JSONObject> getTemplate(TemplateParam param) {
        return Result.success(diyPageService.getTemplate(param));
    }

    /**
     * 获取模板页面
     * @param type
     * @param mode
     * @return
     */
    @GetMapping("/template/pages")
    @Description("获取模板页面")
    public Result<JSONObject> getTemplatePages(@RequestParam(name = "type", defaultValue = "") String type, @RequestParam(name = "mode", defaultValue = "") String mode) {
        return Result.success(PagesEnum.getPages(type, mode));
    }

    /**
     * 切换模板
     * @param param
     * @return
     */
    @PutMapping("/change")
    @Description("切换模板")
    public Result<Object> changeTemplate(@Validated @RequestBody StartUpPageConfigParam param) {
        diyPageService.changeTemplate(param);
        return Result.success();
    }

    /**
     * 获取页面装修列表
     * @param searchParam
     * @return
     */
    @GetMapping("/decorate")
    @Description("获取页面装修列表")
    public Result<JSONObject> getDecoratePage(DiyPageSearchParam searchParam) {
        return Result.success(diyPageService.getDecoratePage(searchParam));
    }

    /**
     * 自定义页面分页列表，轮播搜索组件用
     * @return
     */
    @GetMapping("/carousel_search")
    @Description("自定义页面分页列表，轮播搜索组件用")
    public Result<PageResult<DiyPageListVo>> getPageByCarouselSearch(@Validated PageParam pageParam) {
        return Result.success(diyPageService.getPageByCarouselSearch(pageParam));
    }

    /**
     * 复制
     * @param id
     * @return
     */
    @PostMapping("/copy")
    @Description("复制")
    public Result<Object> copy(@RequestBody Id id) {
        diyPageService.copy(id.getId());
        return Result.success();
    }

    /**
     *  获取自定义链接
     * @param pageParam
     * @return
     */
    @GetMapping("/page_link")
    public Result<PageResult<DiyPageListVo>> getPageLink(PageParam pageParam) {
        return Result.success(diyPageService.getPageLink(pageParam));
    }
}
