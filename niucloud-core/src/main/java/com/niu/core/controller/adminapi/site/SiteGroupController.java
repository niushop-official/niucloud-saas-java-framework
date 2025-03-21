package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.google.gson.JsonObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.loader.upload.UploadLoader;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.enums.member.TestEnum;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.param.SiteGroupAddParam;
import com.niu.core.service.admin.site.param.SiteGroupParam;
import com.niu.core.service.admin.site.param.SiteGroupSearchParam;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;
import com.niu.core.service.core.addon.CoreAddonBaseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺分组（分组权限）控制器
 */

@RestController
@RequestMapping("adminapi/site/group")
@SaCheckLogin
public class SiteGroupController {

    @Resource
    ISiteGroupService siteGroupService;

    /**
     * 店铺分组（分组权限）列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteGroupListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteGroupListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SiteGroupSearchParam searchParam) {
        PageResult<SiteGroupListVo> list = siteGroupService.list(pageParam, searchParam);
        return Result.success(list);
    }

    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteGroupAddParam siteGroupAddParam){
        siteGroupService.add(siteGroupAddParam);
        return Result.success();
    }

    /**
     * 店铺分组（分组权限）详情
     * @param id 主键ID
     * @return Result<SiteGroupInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteGroup> info(@Validated @PathVariable("id") Integer id) {
        SiteGroup info = siteGroupService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺分组（分组权限）编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody SiteGroupParam editParam) {
        siteGroupService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺分组（分组权限）删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        siteGroupService.del(id);
        return Result.success();
    }

    /**
     *获取所有分组
     * @return
     */
    @GetMapping("/all")
    public Result<List<SiteGroup>> all(){
        return Result.success(siteGroupService.getAll());
    }


    /**
     * 所有站點分組
     *
     * @param uid
     * @return
     */
    @GetMapping("/user")
    public Result<List<SiteGroupListVo>> getUserSiteGroupAll(@Validated @RequestParam("uid") Integer uid){
        List<SiteGroupListVo> siteGroupListVoList=new ArrayList<>();
        siteGroupListVoList=siteGroupService.getUserSiteGroupAll(uid);
        return Result.success(siteGroupListVoList);
    }


    @GetMapping("/test")
    public Result<Object> test(){
        CoreAddonBaseService coreAddonBaseService = new CoreAddonBaseService();
        return  Result.success(coreAddonBaseService.getPath());
    }




}
