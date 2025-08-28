package com.niu.core.controller.adminapi.generator;

import com.niu.core.common.component.context.MapperMap;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.generator.IGenerateService;
import com.niu.core.service.admin.generator.param.GenerateCodeParam;
import com.niu.core.service.admin.generator.param.GenerateEditParam;
import com.niu.core.service.admin.generator.param.GenerateParam;
import com.niu.core.service.admin.generator.param.GenerateSearchParam;
import com.niu.core.service.admin.generator.vo.*;
import com.niu.core.service.core.generator.vo.CoreGenerateTemplateVo;
import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdk.jfr.Description;

@Description("代码生成")
@RestController
@RequestMapping("adminapi/generator")
public class GenerateController {

    @Resource
    IGenerateService generateService;

    /**
     * 生成列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Description("生成列表")
    @GetMapping("/generator")
    public Result<PageResult<GenerateListVo>> list(@Validated PageParam pageParam,
                                                   @Validated GenerateSearchParam searchParam) {
        PageResult<GenerateListVo> list = generateService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 生成详情
     * @param id
     * @return
     */
    @Description("生成详情")
    @GetMapping("/generator/{id}")
    public Result<GenerateDetailVo> detail(@PathVariable("id") Integer id) {
        GenerateDetailVo detail = generateService.getInfo(id);
        return Result.success(detail);
    }

    /**
     * 添加代码生成
     * @param generateParam
     * @return
     */
    @Description("添加代码生成")
    @PostMapping("/generator")
    public Result<Map<String, Integer>> add(@Validated @RequestBody GenerateParam generateParam) {
        Integer id = generateService.add(generateParam);
        Map<String, Integer> map = new HashMap<>();
        map.put("id", id);
        return Result.success( map);
    }

    /**
     * 编辑代码生成
     * @param id
     * @param generateParam
     * @return
     */
    @Description("编辑代码生成")
    @PutMapping("/generator/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody GenerateEditParam generateParam) {
        generateService.edit(id, generateParam);
        return Result.success();
    }

    /**
     * 删除代码生成
     * @param id
     * @return
     */
    @Description("删除代码生成")
    @DeleteMapping("/generator/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        generateService.del(id);
        return Result.success();
    }

    /**
     * 下载生成代码
     * @param generateCodeParam
     * @return
     */
    @Description("下载生成代码")
    @PostMapping("/download")
    public Result<Map<String, String>> create(@Validated @RequestBody GenerateCodeParam generateCodeParam) {
        generateService.generate(generateCodeParam);
        Map<String, String> data = new HashMap<>();
        if (generateCodeParam.getGenerateType().equals("2")) data.put("file", "upload/generate/package.zip");
        return Result.success(data);
    }

    /**
     * 数据表列表
     * @param content
     * @return
     */
    @Description("数据表列表")
    @GetMapping("/table")
    public Result<List<Map<String, Object>>> tableList(@RequestParam(name = "name", defaultValue = "") String name, @RequestParam(name = "content", defaultValue = "") String content) {
        List<Map<String, Object>> list = generateService.tableList(name, content);
        return Result.success(list);
    }

    /**
     * 生成预览
     * @param id
     * @return
     */
    @Description("生成预览")
    @GetMapping("/preview/{id}")
    public Result<List<GeneratePreviewVo>> preview(@PathVariable("id") Integer id) {

        return Result.success(generateService.preview(id));
    }

    /**
     * 检测文件
     * @param id
     * @return
     */
    @Description("检测文件")
    @GetMapping("/check_file")
    public Result<Integer> checkFile(@RequestParam("id") String id) {

        return Result.success(1,"操作成功", generateService.checkFile(id));
    }

    /**
     * 数据表字段
     * @param tableName
     * @return
     */
    @Description("数据表字段")
    @GetMapping("/table_column")
    public Result<List<TableFiledVo>> getTableColumn(@RequestParam("table_name") String tableName) {
        List<TableFiledVo> list = generateService.getTableColumn(tableName);
        return Result.success(list);
    }

    /**
     * 获取mapper
     * @param addon
     * @return
     */
    @Description("获取mapper")
    @GetMapping("/all_model")
    public Result<?> getAllMapper(@RequestParam("addon") String addon) {
        if (addon.equals("system")) addon = "core";
        return Result.success(MapperMap.getMappersByModule(addon));
    }

    /**
     * 根据mapper获取表字段
     * @param model
     * @return
     */
    @Description("根据mapper获取表字段")
    @GetMapping("/model_table_column")
    public Result<?> gettableColumn(@RequestParam("model") String model) {
        return Result.success(generateService.getTableColumnByMapper(model));
    }
}
