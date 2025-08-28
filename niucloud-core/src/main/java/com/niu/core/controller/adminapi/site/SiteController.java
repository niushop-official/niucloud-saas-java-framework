package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.system.CaptchaUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import com.niu.core.service.admin.site.vo.ShowMarketingVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import jdk.jfr.Description;

/**
 * 站点控制器
 */

@RestController
@RequestMapping("adminapi/site")
@SaCheckLogin
@Description("站点管理")
public class SiteController {

    @Resource
    ISiteService siteService;

    @Resource
    IAuthService authService;

    /**
     * 站点列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteListVo>>
     */
    @GetMapping("/site")
    @Description("站点列表")
    public Result<PageResult<SiteListVo>> list(@Validated PageParam pageParam,
                                               @Validated SiteSearchParam searchParam) {
        PageResult<SiteListVo> list = siteService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 站点详情
     * @param id 主键ID
     * @return Result<SiteInfoVo>
     */
    @GetMapping("/site/{id}")
    @Description("站点详情")
    public Result<SiteInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SiteInfoVo info = siteService.info(id);
        return Result.success(info);
    }

    /**
     * 站点添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/site")
    @Description("站点添加")
    public Result<Object> add(@Validated @RequestBody SiteAddParam addParam) {
        siteService.add(addParam);
        return Result.success();
    }

    /**
     * 站点编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/site/{id}")
    @Description("站点编辑")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody SiteEditParam editParam) {
        siteService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 站点删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/site/{id}")
    @Description("站点删除")
    public Result<Object> del(@Validated @PathVariable("id") Integer id, CaptchaUtils.Captcha captcha) {
        CaptchaUtils.check(captcha.getCaptchaKey(), captcha.getCaptchaCode());
        siteService.del(id);
        return Result.success();
    }

    /**
     * 关闭站点
     * @param id
     * @return
     */
    @PutMapping("/closesite/{id}")
    @Description("关闭站点")
    public Result<Object> closeSite(@Validated @PathVariable("id") Integer id){
        siteService.closeSite(id);
        return Result.success();
    }

    /**
     * 开启站点
     * @param id
     * @return
     */
    @PutMapping("/opensite/{id}")
    @Description("开启站点")
    public Result<Object> openSite(@Validated @PathVariable("id") Integer id){
        siteService.openSite(id);
        return Result.success();
    }

    /**
     * 站点状态列表
     * @return
     */
    @GetMapping("/statuslist")
    @Description("站点状态列表")
    public Result<Map<Integer, String>> statusList(){
        return Result.success(SiteStatusEnum.getMap());
    }


    /**
     * 获取所有菜单
     *
     * @return
     */
    @GetMapping("/site/menu")
    @Description("获取所有菜单")
    public Result<JSONArray> menu(){
        return Result.success(authService.getAuthMenuTreeList("all"));
    }

    /**
     * 获取站点支持的应用插件
     *
     */
    @GetMapping("/addons")
    @Description("获取站点支持的应用插件")
    public Result<List<Addon>> addons() {
        return Result.success(siteService.getSiteAddons());
    }

    /**
     * 获取应用工具
     * @return
     */
    @GetMapping("/showApp")
    @Description("获取应用工具")
    public Result<Map<String, ShowAppListVo>> showApp() {
        return Result.success(siteService.getShowAppTools());
    }

    /**
     * 获取营销列表
     */
    @GetMapping("/showMarketing")
    @Description("获取营销列表")
    public Result<?> showMarketing() {
        return Result.success(siteService.getShowMarketingTools());
    }

    /**
     * 获取是否允许切换站点
     * @return
     */
    @GetMapping("/allow_change")
    @Description("获取是否允许切换站点")
    public Result<?> getIsAllowChangeSite(){
        return Result.success(authService.getIsAllowChangeSite());
    }

    /**
     * 设置是否允许切换站点
     */
    @PutMapping("/allow_change")
    @Description("设置是否允许切换站点")
    public Result<?> setIsAllowChangeSite(@RequestBody Map<String, Integer> param){
        authService.setIsAllowChangeSite(param);
        return Result.success();
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captcha/create")
    @Description("生成验证码")
    public Result<CaptchaUtils.Captcha> captchaCreate() {
        return Result.success(CaptchaUtils.create());
    }

    /**
     * 站点初始化
     */
    @PostMapping("/init")
    @Description("站点初始化")
    public Result<Boolean> init(@RequestBody @Validated SiteInitParam param) {
        CaptchaUtils.check(param.getCaptchaKey(), param.getCaptchaCode());
        return Result.success(siteService.siteInit(param.getSiteId()));
    }
}
