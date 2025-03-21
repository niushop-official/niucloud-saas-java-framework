package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysRoleService;
import com.niu.core.service.admin.sys.param.SysRoleParam;
import com.niu.core.service.admin.sys.param.SysRoleSearchParam;
import com.niu.core.service.admin.sys.vo.SysRoleListVo;
import com.niu.core.service.admin.sys.vo.SysRoleInfoVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 角色管理
 */
@RestController
@RequestMapping("/adminapi/sys/")
@SaCheckLogin
public class SysRoleController {

    @Resource
    ISysRoleService sysRoleService;

    @GetMapping("role/all")
    public Result<List<SysRoleListVo>> all(){
        List<SysRoleListVo> sysRoleListVoList=sysRoleService.getAllRole();
        return Result.success(sysRoleListVoList);
    }
    /**
     * 角色列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return Result<Object>
     */
    @GetMapping("/role")
    public Result<PageResult<SysRoleListVo>> list(@Validated PageParam pageParam,
                                                  @Validated SysRoleSearchParam searchParam) {
        PageResult<SysRoleListVo> list = sysRoleService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 角色添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/role")
    public Result<Object> add(@Validated @RequestBody SysRoleParam addParam) {
        sysRoleService.add(addParam);
        return Result.success();
    }

    /**
     * 用户组详情
     * @param roleId
     * @return
     */
    @GetMapping("/role/{roleId}")
    public Result<SysRoleInfoVo> info(@Validated @PathVariable("roleId") Integer roleId){
        SysRoleInfoVo sysRoleInfoVo= sysRoleService.info(roleId);
        return Result.success(sysRoleInfoVo);
    }

    /**
     * 角色编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/role/{roleId}")
    public Result<Object> edit(@Validated @PathVariable("roleId") Integer roleId, @Validated @RequestBody SysRoleParam editParam) {
        sysRoleService.edit(roleId, editParam);
        return Result.success();
    }

    /**
     * 角色删除
     *
     * @param roleId 参数
     * @return Result<Object>
     */
    @DeleteMapping("/role/{roleId}")
    public Result<Object> del(@Validated @PathVariable("roleId") Integer roleId) {
        sysRoleService.del(roleId);
        return Result.success();
    }

}
