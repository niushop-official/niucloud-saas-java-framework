package com.niu.core.controller.adminapi.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.enums.HttpEnum;
import com.niu.core.service.admin.diy_form.IDiyFormConfigService;
import com.niu.core.service.admin.diy_form.IDiyFormRecordsService;
import com.niu.core.service.admin.diy_form.IDiyFormService;
import com.niu.core.service.admin.diy_form.param.*;
import com.niu.core.service.admin.diy_form.vo.*;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;
import io.swagger.models.auth.In;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 万能表单表控制器
 */
@RestController
@RequestMapping("adminapi/diy")
public class DiyFormController {

    @Resource
    IDiyFormService diyFormService;

    @Resource
    IDiyFormRecordsService diyFormRecordsService;

    @Resource
    IDiyFormConfigService diyFormConfigService;

    /**
     * 万能表单表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<DiyFormListVo>>
     */
    @GetMapping("/form")
    public Result<PageResult<DiyFormListVo>> page(@Validated PageParam pageParam, @Validated DiyFormSearchParam searchParam) {
        PageResult<DiyFormListVo> list = diyFormService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 万能表单表详情
     *
     * @param id 主键ID
     * @return Result<DiyFormInfoVo>
     */
    @GetMapping("/form/{id}")
    public Result<DiyFormInfoVo> info(@PathVariable("id") Integer id) {
        DiyFormInfoVo info = diyFormService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 万能表单表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/form")
    public Result<Object> add(@Validated @RequestBody DiyFormParam addParam) {
        diyFormService.add(addParam);
        return Result.success();
    }

    /**
     * 万能表单表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/form/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody DiyFormParam editParam) {
        diyFormService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 万能表单表删除
     *
     * @param diyFormDeleteParam 参数
     * @return Result<Object>
     */
    @PutMapping("/form/delete")
    public Result<Object> del(@RequestBody DiyFormDeleteParam diyFormDeleteParam) {
        diyFormService.del(diyFormDeleteParam.getFormIds());
        return Result.success();
    }

    /**
     * 获取万能表单列表
     * @param searchParam 搜索参数
     * @return Result<List<DiyFormListVo>>
     */
    @GetMapping("/form/list")
    public Result<List<DiyFormListVo>> lists(@Validated DiyFormSearchParam searchParam) {
        List<DiyFormListVo> list = diyFormService.getList(searchParam);
        return Result.success(list);
    }

    /**
     * 获取万能表单初始化数据
     * @param params 参数
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/init")
    public Result<DiyFormInitVo> getInit(DiyFormInitParam params) {
        DiyFormInitVo initData = diyFormService.getInit(params);
        return Result.success(initData);
    }

    /**
     * 获取万能表单模板
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/template")
    public Result<Map<String, Object>> getTemplate(DiyFormTemplateParam params) {
        Map<String, Object> template = diyFormService.getTemplate(params);
        return Result.success(template);
    }

    /**
     * 修改页面分享内容
     * @param share 分享内容数据
     * @return Result<Object>
     */
    @PutMapping("/form/share")
    public Result<Object> modifyShare(@RequestBody DiyFormShareParam share) {
        diyFormService.modifyShare(share.getFormId(), share.getShare());
        return Result.success();
    }

    /**
     * 复制模版
     * @return Result<Integer>
     */
    @PostMapping("/form/copy")
    public Result<Integer> copy(@RequestBody DiyFormCopyParam diyFormCopyParam) {
        Integer id = diyFormService.copy(diyFormCopyParam.getFormId());
        return Result.success(HttpEnum.SUCCESS.getCode(), HttpEnum.SUCCESS.getMsg(), id);
    }

    /**
     * 获取万能表单类型
     * @return Result<List<Map<String, Object>>>
     */
    @GetMapping("/form/type")
    public Result<Object> getFormType() {
        JSONObject formTypeList = diyFormService.getFormType();
        return Result.success(formTypeList);
    }

    /**
     * 修改状态
     * @return Result<Object>
     */
    @PutMapping("/form/status")
    public Result<Object> modifyStatus(@RequestBody DiyFormStatusParam formStatusParam) {
        diyFormService.modifyStatus(formStatusParam);
        return Result.success();
    }

    /**
     * 获取使用记录
     * @param searchParam 查询数据
     * @return Result<PageResult<DiyFormRecordsListVo>>
     */
    @GetMapping("/form/records")
    public Result<PageResult<DiyFormRecordsListVo>> getRecordPages(PageParam pageParam, DiyFormRecordsSearchParam searchParam) {

        return Result.success(diyFormService.getRecordPages(pageParam, searchParam));
    }

    /**
     * 获取使用记录详情
     * @param recordsId 记录ID
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/records/{records_id}")
    public Result<DiyFormRecordsInfoVo> getRecordInfo(@PathVariable("records_id") Integer recordsId) {
        return Result.success(diyFormService.getRecordInfo(recordsId));
    }

    /**
     * 使用记录删除
     * @return Result<Object>
     */
    @DeleteMapping("/form/records/delete")
    public Result<Object> delRecord(DiyFormRecordsDelParam delParam) {
        diyFormService.delRecord(delParam.getFormId(), delParam.getRecordId());
        return Result.success();
    }

    /**
     * 获取万能表单字段记录
     * @return Result<List<Map<String, Object>>>
     */
    @GetMapping("/form/fields/list")
    public Result<Object> getFieldsList(DiyFormRecordsFieldsSearchParam diyFormRecordsFieldsSearchParam) {
        List<DiyFormFieldsListVo> fieldsList = diyFormService.getFieldsList(diyFormRecordsFieldsSearchParam);
        return Result.success(fieldsList);
    }

    /**
     * 获取表单填写配置
     * @param formId 表单ID
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/write/{form_id}")
    public Result<Object> getWriteConfig(@PathVariable("form_id") Integer formId) {

        return Result.success(diyFormConfigService.getWriteConfig(formId));
    }

    /**
     * 编辑表单填写配置
     * @param editParam 配置数据
     * @return Result<Object>
     */
    @PutMapping("/form/write")
    public Result<Object> editWriteConfig(@RequestBody DiyFormWriteConfigParam editParam) {
        diyFormConfigService.editWriteConfig(editParam);
        return Result.success();
    }

    /**
     * 获取表单提交成功页配置
     * @param formId 表单ID
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/submit/{form_id}")
    public Result<Object> getSubmitConfig(@PathVariable("form_id") Integer formId) {

        return Result.success(diyFormConfigService.getSubmitConfig(formId));
    }

    /**
     * 编辑表单提交成功页配置
     * @param editParam 配置数据
     * @return Result<Object>
     */
    @PutMapping("/form/submit")
    public Result<Object> editSubmitConfig(@RequestBody DiyFormSubmitConfigParam editParam) {
        diyFormConfigService.editSubmitConfig(editParam);
        return Result.success();
    }

    /**
     * 获取万能表单填表人统计列表
     * @param searchParam 查询数据
     * @return Result<PageResult<Map<String, Object>>>
     */
    @GetMapping("/form/records/member/stat")
    public Result<Object> memberStatPages(PageParam pageParam, com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam searchParam) {
        return Result.success(diyFormRecordsService.getPage(pageParam, searchParam));
    }

    /**
     * 获取万能表单字段统计列表
     * @param data 查询数据
     * @return Result<List<Map<String, Object>>>
     */
    @GetMapping("/form/records/field/stat")
    public Result<Object> fieldStatList(com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam data) {
        return Result.success(diyFormRecordsService.getFieldStatList(data));
    }

    /**
     * 获取万能表单微信小程序二维码
     * @param formId 表单ID
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/form/qrcode")
    public Result<Object> getQrcode(@RequestParam("form_id") Integer formId) {
        //Map<String, Object> qrcode = diyFormService.getQrcode(formId);
        return Result.success();
    }
}