package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.param.SysUserRoleSearchParam;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 用户权限管理
 */
@RestController
@RequestMapping("/api/user_role")
@SaCheckLogin
public class SysUserRoleController {

    @Resource
    ISysUserRoleService sysUserService;

    /**
     * 用户权限列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("")
    public Result<PageResult<SysUserRoleListVo>> list(@Validated PageParam pageParam,
                                                          @Validated SysUserRoleSearchParam searchParam) {
        PageResult<SysUserRoleListVo> list = sysUserService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 用户权限详情
     *
     * @param id 主键ID
     * @return Result<SysUserRoleInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SysUserRoleInfoVo> info(@Validated @PathVariable("id") Integer id) {
        SysUserRoleInfoVo info = sysUserService.info(id);
        return Result.success(info);
    }

    /**
     * 用户权限添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SysUserRoleParam addParam) {
        sysUserService.add(addParam);
        return Result.success();
    }

    /**
     * 用户权限编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SysUserRoleParam editParam) {
        sysUserService.edit(editParam);
        return Result.success();
    }

    /**
     * 用户权限删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        sysUserService.del(id);
        return Result.success();
    }

}
