package com.niu.core.controller.adminapi.stat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.stat.IStatHourService;
import com.niu.core.service.admin.stat.param.StatHourParam;
import com.niu.core.service.admin.stat.param.StatHourSearchParam;
import com.niu.core.service.admin.stat.vo.StatHourInfoVo;
import com.niu.core.service.admin.stat.vo.StatHourListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 小时统计控制器
 */

@RestController
@RequestMapping("adminapi/hour")
@SaCheckLogin
public class StatHourController {

    @Resource
    IStatHourService statHourService;

    /**
     * 小时统计列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<StatHourListVo>>
     */
    @GetMapping("/list")
    public Result<PageResult<StatHourListVo>> list(@Validated PageParam pageParam,
                                                     @Validated StatHourSearchParam searchParam) {
        PageResult<StatHourListVo> list = statHourService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 小时统计详情
     * @param id 主键ID
     * @return Result<StatHourInfoVo>
     */
    @GetMapping("/info")
    public Result<StatHourInfoVo> info(@Validated @RequestParam("id") Integer id) {
        StatHourInfoVo info = statHourService.info(id);
        return Result.success(info);
    }

    /**
     * 小时统计添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody StatHourParam addParam) {
        statHourService.add(addParam);
        return Result.success();
    }

    /**
     * 小时统计编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody StatHourParam editParam) {
        statHourService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 小时统计删除
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        statHourService.del(id);
        return Result.success();
    }

}
