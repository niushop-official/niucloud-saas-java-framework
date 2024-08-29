package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysPosterService;
import com.niu.core.service.admin.sys.param.SysPosterParam;
import com.niu.core.service.admin.sys.param.SysPosterSearchParam;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 海报控制器
 */

@RestController
@RequestMapping("adminapi/poster")
@SaCheckLogin
public class SysPosterController {

    @Resource
    ISysPosterService sysPosterService;

    /**
     * 海报列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SysPosterListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<SysPosterListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SysPosterSearchParam searchParam) {
        PageResult<SysPosterListVo> list = sysPosterService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 海报详情
     * @param id 主键ID
     * @return Result<SysPosterInfoVo>
     */
    @GetMapping("/info")
    public Result<SysPosterInfoVo> info(@Validated @RequestParam("id") Integer id) {
        SysPosterInfoVo info = sysPosterService.info(id);
        return Result.success(info);
    }

    /**
     * 海报添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody SysPosterParam addParam) {
        sysPosterService.add(addParam);
        return Result.success();
    }

    /**
     * 海报编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody SysPosterParam editParam) {
        sysPosterService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 海报删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        sysPosterService.del(id);
        return Result.success();
    }

}
