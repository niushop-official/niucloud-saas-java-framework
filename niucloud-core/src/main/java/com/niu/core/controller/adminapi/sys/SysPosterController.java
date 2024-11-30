package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.poster.PosterTypeEnum;
import com.niu.core.service.admin.sys.ISysPosterService;
import com.niu.core.service.admin.sys.param.*;
import com.niu.core.service.admin.sys.vo.SysPosterInfoVo;
import com.niu.core.service.admin.sys.vo.SysPosterListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.param.SysPosterTemplateSearchParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 海报控制器
 */

@RestController
@RequestMapping("adminapi/sys/poster")
@SaCheckLogin
public class SysPosterController {

    @Resource
    ISysPosterService sysPosterService;

    /**
     * 海报分页列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<SysPosterListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SysPosterListVo>> page(PageParam pageParam, @Validated SysPosterSearchParam searchParam) {
        return Result.success(sysPosterService.page(pageParam, searchParam));
    }

    /**
     * 海报列表
     * @param searchParam 搜索条件
     * @return Result<List<SysPosterListVo>>
     */
    @GetMapping("/list")
    public Result<List<SysPosterListVo>> list(@Validated SysPosterSearchParam searchParam) {
        return Result.success(sysPosterService.list(searchParam));
    }

    /**
     * 海报详情
     * @param id 主键ID
     * @return Result<SysPosterInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SysPosterInfoVo> info(@PathVariable Integer id) {
        SysPosterInfoVo info = sysPosterService.info(id);
        return Result.success(info);
    }

    /**
     * 海报添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SysPosterParam addParam) {
        sysPosterService.add(addParam);
        return Result.success();
    }

    /**
     * 海报编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable Integer id, @Validated @RequestBody SysPosterParam editParam) {
        sysPosterService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 海报删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        sysPosterService.del(id);
        return Result.success();
    }

    /**
     * 海报类型
     *
     * @param type
     * @return
     */
    @GetMapping("/type")
    public Result<Object> type(@RequestParam(defaultValue = "") String type) {
        if (type.isEmpty()) {
            return Result.success(PosterTypeEnum.getType());
        }
        return Result.success(PosterTypeEnum.getType(type));
    }

    /**
     * 自定义海报初始化数据
     *
     * @return
     */
    @GetMapping("/init")
    public Result init(SysPosterInitParam param) {
        return Result.success(sysPosterService.init(param));
    }

    /**
     * 海报模板
     *
     * @return
     */
    @GetMapping("/template")
    public Result template(SysPosterTemplateSearchParam param) {
        return Result.success(sysPosterService.template(param));
    }

    /**
     * 修改自定义海报状态
     *
     * @return
     */
    @PutMapping("/status")
    public Result modifyStatus(@RequestBody SysPosterModifyParam param) {
        sysPosterService.modifyStatus(param.getId(), param.getStatus());
        return Result.success();
    }

    /**
     * 将自定义海报修改为默认海报
     *
     * @return
     */
    @PutMapping("/default")
    public Result modifyDefault(@RequestBody SysPosterModifyParam param) {
        sysPosterService.modifyDefault(param.getId());
        return Result.success();
    }
}
