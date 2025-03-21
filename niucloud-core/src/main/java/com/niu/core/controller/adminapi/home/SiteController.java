package com.niu.core.controller.adminapi.home;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.home.IAuthSiteService;
import com.niu.core.service.admin.home.param.HomeSiteAddParam;
import com.niu.core.service.admin.home.param.SiteParam;
import com.niu.core.service.admin.home.param.SiteSearchParam;
import com.niu.core.service.admin.home.vo.SiteInfoVo;
import com.niu.core.service.admin.home.vo.SiteListVo;
import com.niu.core.service.admin.home.vo.UserCreateSiteVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 站点控制器
 */

@RestController
@RequestMapping("adminapi/home")
@SaCheckLogin
public class SiteController {

    @Resource
    IAuthSiteService authSiteService;

    /**
     * 站点列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteListVo>>
     */
    @GetMapping("/site")
    public Result<PageResult<SiteListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SiteSearchParam searchParam) {
        PageResult<SiteListVo> list = authSiteService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 站点详情
     *
     * @param id 主键ID
     * @return Result<SiteInfoVo>
     */
    @GetMapping("/site/{id}")
    public Result<SiteInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SiteInfoVo info = authSiteService.info(id);
        return Result.success(info);
    }

    /**
     * 站点编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/site/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteParam editParam) {
        authSiteService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 获得可选择的店铺套餐
     *
     * @return
     */
    @GetMapping("/site/group")
    public Result<List<UserCreateSiteVo>> getSiteGroup() {
        return Result.success(authSiteService.getSiteGroup());
    }

    /**
     * 用户自己创建站点
     *
     * @param homeSiteAddParam
     * @return
     */
    @PostMapping("/site/create")
    public Result<Object> create(@Validated @RequestBody HomeSiteAddParam homeSiteAddParam){
        authSiteService.createSite(homeSiteAddParam);
        return Result.success();
    }

}
