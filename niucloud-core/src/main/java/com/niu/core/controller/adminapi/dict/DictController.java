package com.niu.core.controller.adminapi.dict;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.dict.IDictService;
import com.niu.core.service.admin.dict.param.DictDataParam;
import com.niu.core.service.admin.dict.param.DictParam;
import com.niu.core.service.admin.dict.param.DictSearchParam;
import com.niu.core.service.admin.dict.vo.DictInfoVo;
import com.niu.core.service.admin.dict.vo.DictListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import jdk.jfr.Description;

/**
 * 字典数据管理
 */
@RestController
@RequestMapping("/adminapi/dict")
@SaCheckLogin
@Description("字典数据管理")
public class DictController {

    @Resource
    IDictService dictService;

    /**
     * 字典列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/dict")
    @Description("字典列表")
    public Result<PageResult<DictListVo>> info(@Validated PageParam pageParam, @Validated DictSearchParam searchParam) {
        PageResult<DictListVo> list = dictService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 字典数据详情(通过ID)
     * @param id 主键ID
     * @return Result<DictInfoVo>
     */
    @GetMapping("/dict/{id}")
    @Description("字典数据详情(通过ID)")
    public Result<DictInfoVo> info(@Validated @PathVariable("id") Integer id) {
        DictInfoVo detail = dictService.info(id);
        return Result.success(detail);
    }

    /**
     * 字典数据详情(通过类型)
     * @param type
     * @return Result<DictInfoVo>
     */
    @GetMapping("dictionary/type/{type}")
    @Description("字典数据详情(通过类型)")
    public Result<DictInfoVo> getKeyInfo(@Validated @PathVariable("type") String type) {
        DictInfoVo detail = dictService.info(type);
        return Result.success(detail);
    }

    /**
     * 字典数据添加
     * @param addParam 参数
     * @return Result<Object>
     */
    @PostMapping("/dict")
    @Description("字典数据添加")
    public Result<Object> add(@Validated @RequestBody DictParam addParam) {
        dictService.add(addParam);
        return Result.success();
    }

    /**
     * 字典数据编辑
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/dict/{id}")
    @Description("字典数据编辑")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody DictParam editParam) {
        dictService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 添加字典数据
     *
     * @param editParam 参数
     * @return Result<Object>
     */
    @PutMapping("/dictionary/{id}")
    @Description("添加字典数据")
    public Result<Object> addDictData(@Validated @PathVariable("id") Integer id, @Validated @RequestBody DictDataParam editParam) {
        dictService.addDictData(id, editParam);
        return Result.success();
    }
    /**
     * 字典数据删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/dict/{id}")
    @Description("字典数据删除")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        dictService.del(id);
        return Result.success();
    }

    /**
     * 字典数据列表
     * @return Result<DictListVo>
     */
    @GetMapping("/all")
    @Description("字典数据列表")
    public Result<List<DictListVo>> getAll() {
        return Result.success(dictService.getAll());
    }





}
