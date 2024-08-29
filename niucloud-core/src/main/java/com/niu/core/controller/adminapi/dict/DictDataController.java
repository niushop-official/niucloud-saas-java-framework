package com.niu.core.controller.adminapi.dict;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.dict.IDictDataService;
import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.vo.DictDataInfoVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 字典数据管理
 */
@RestController
@RequestMapping("/api/data")
@SaCheckLogin
public class DictDataController {

    @Resource
    IDictDataService dictDataService;

    /**
     * 字典数据详情
     *
     * @param id 主键ID
     * @return Result<DictDataInfoVo>
     */
    @GetMapping("/info")
    public Result<DictDataInfoVo> info(@Validated @RequestParam("id") Integer id) {
        DictDataInfoVo detail = dictDataService.info(id);
        return Result.success(detail);
    }

    /**
     * 字典数据添加
     *
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody DictDataParam addParam) {
        dictDataService.add(addParam);
        return Result.success();
    }

    /**
     * 字典数据编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody DictDataParam editParam) {
        dictDataService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 字典数据删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        dictDataService.del(id);
        return Result.success();
    }

}
