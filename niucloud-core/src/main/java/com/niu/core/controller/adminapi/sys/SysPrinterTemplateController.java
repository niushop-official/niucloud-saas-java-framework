package com.niu.core.controller.adminapi.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.sys.ISysPrinterTemplateService;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateParam;
import com.niu.core.service.admin.sys.param.SysPrinterTemplateSearchParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 小票打印模板控制器
 */

@RestController
@RequestMapping("adminapi/sys/printer/template")
public class SysPrinterTemplateController {

    @Resource
    ISysPrinterTemplateService sysPrinterTemplateService;

    @GetMapping
    public Result<?> templatePageLists(@Validated PageParam pageParam, @Validated SysPrinterTemplateSearchParam searchParam) {
        return Result.success(sysPrinterTemplateService.page(pageParam, searchParam));
    }

    @GetMapping("/list")
    public Result<?> templateLists(@Validated SysPrinterTemplateSearchParam searchParam) {
        return Result.success(sysPrinterTemplateService.getList(searchParam));
    }

    @GetMapping("/{id}")
    public Result<?> info(@PathVariable("id") Integer id) {
        return Result.success(sysPrinterTemplateService.info(id));
    }

    @PostMapping
    public Result<?> add(@Validated @RequestBody SysPrinterTemplateParam addParam) {
        sysPrinterTemplateService.add(addParam);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @Validated @RequestBody SysPrinterTemplateParam editParam) {
        sysPrinterTemplateService.edit(id, editParam);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer id) {
        sysPrinterTemplateService.del(id);
        return Result.success();
    }
}
