package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteUserService;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.SiteGroupListVo;
import com.niu.core.service.admin.site.vo.SiteUserVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 店铺分组（分组权限）控制器
 */

@RestController
@RequestMapping("adminapi/site/")
@SaCheckLogin
public class UserController {

    @Resource
    ISiteUserService siteUserService;

    /**
     * 店铺分组（分组权限）列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteGroupListVo>>
     */
    @GetMapping("user")
    public Result<PageResult<SiteUserVo>> lists(@Validated PageParam pageParam, @Validated SiteUserSearchParam searchParam) {
        PageResult<SiteUserVo> list = siteUserService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 站点添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("user")
    public Result add(@Validated @RequestBody SiteUserParam addParam) {
        siteUserService.add(addParam);
        return Result.success();
    }

    /**
     * 查询用户详细信息
     * @param uid
     * @return
     */
    @GetMapping("user/{uid}")
    public Result<SiteUserVo> getinfo(@Validated @PathVariable("uid") Integer uid){
        SiteUserVo siteUserVo=siteUserService.getInfo(uid);
        return Result.success(siteUserVo);
    }

    /**
     * 编辑会员信息
     * @param siteUserParam
     * @param uid
     * @return
     */
    @PutMapping("user/{uid}")
    public Result edit(@Validated @RequestBody SiteUserParam siteUserParam, @Validated @PathVariable("uid") Integer uid){
        siteUserService.edit(uid, siteUserParam);
        return Result.success();
    }

    /**
     * 锁定会员
     * @param uid
     * @return
     */
    @PutMapping("user/lock/{uid}")
    public Result lock(@Validated @PathVariable("uid") Integer uid){
        siteUserService.lock(uid);
        return Result.success();
    }

    /**
     * 解锁会员
     * @param uid
     * @return
     */
    @PutMapping("user/unlock/{uid}")
    public Result unlock(@Validated @PathVariable("uid") Integer uid){
        siteUserService.unlock(uid);
        return Result.success();
    }

}
