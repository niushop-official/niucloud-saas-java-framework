package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.ISysNoticeService;
import com.niu.core.service.admin.sys.param.SysNoticeParam;
import com.niu.core.service.admin.sys.param.SysNoticeSearchParam;
import com.niu.core.service.admin.sys.vo.SysNoticeInfoVo;
import com.niu.core.service.admin.sys.vo.SysNoticeListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 通知模型控制器
 */

@RestController
@RequestMapping("adminapi/notice")
@SaCheckLogin
public class SysNoticeController {

    @Resource
    ISysNoticeService sysNoticeService;

    /**
     * 通知模型列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SysNoticeListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<SysNoticeListVo>> list(@Validated PageParam pageParam,
                                                     @Validated SysNoticeSearchParam searchParam) {
        PageResult<SysNoticeListVo> list = sysNoticeService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 通知模型详情
     * @param id 主键ID
     * @return Result<SysNoticeInfoVo>
     */
    @GetMapping("/info")
    public Result<SysNoticeInfoVo> info(@Validated @RequestParam("id") Integer id) {
        SysNoticeInfoVo info = sysNoticeService.info(id);
        return Result.success(info);
    }

    /**
     * 通知模型添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody SysNoticeParam addParam) {
        sysNoticeService.add(addParam);
        return Result.success();
    }

    /**
     * 通知模型编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody SysNoticeParam editParam) {
        sysNoticeService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 通知模型删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        sysNoticeService.del(id);
        return Result.success();
    }

}
