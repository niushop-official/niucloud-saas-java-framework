package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.core.service.admin.site.param.SiteParam;
import com.niu.core.service.admin.site.param.SiteSearchParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 站点控制器
 */

@RestController
@RequestMapping("adminapi/site")
@SaCheckLogin
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
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        siteService.del(id);
        return Result.success();
    }

    /**
     * 关闭站点
     * @param id
     * @return
     */
    @PutMapping("/closesite/{id}")
    public Result<Object> closeSite(@Validated @PathVariable Integer id){
        siteService.closeSite(id);
        return Result.success();
    }

    /**
     * 关闭站点
     * @param id
     * @return
     */
    @PutMapping("/opensite/{id}")
    public Result<Object> openSite(@Validated @PathVariable Integer id){
        siteService.openSite(id);
        return Result.success();
    }

    /**
     * 站点状态列表
     * @return
     */
    @GetMapping("/statuslist")
    public Result<Map<Integer, String>> statusList(){
        return Result.success(SiteStatusEnum.getMap());
    }


    /**
     * 获取所有菜单
     *
     * @return
     */
    @GetMapping("/site/menu")
    public Result<JSONArray> menu(){
        return Result.success(authService.getAuthMenuTreeList("all"));
    }

    /**
     * 获取站点支持的应用插件
     *
     */
    @GetMapping("/addons")
    public Result<List<Addon>> addons() {
        return Result.success(siteService.getSiteAddons());
    }
}
