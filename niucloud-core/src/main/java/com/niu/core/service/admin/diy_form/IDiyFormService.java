package com.niu.core.service.admin.diy_form;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.diy_form.param.*;
import com.niu.core.service.admin.diy_form.vo.*;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;
import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 万能表单服务接口
 */
public interface IDiyFormService {

    /**
     * 万能表单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<DiyFormListVo>
     */
    PageResult<DiyFormListVo> getPage(PageParam pageParam, DiyFormSearchParam searchParam);

    /**
     * 万能表单列表
     * @param searchParam 搜索参数
     * @return List<DiyFormListVo>
     */
    List<DiyFormListVo> getList(DiyFormSearchParam searchParam);

    /**
     * 万能表单详情
     * @param id 主键ID
     * @return  DiyFormInfoVo
     */
    DiyFormInfoVo getInfo(Integer id);

    Long getCount(DiyFormSearchParam searchParam);

    /**
     * 万能表单添加
     * @param addParam 添加参数
     */
    Integer add(DiyFormParam addParam);

    /**
     * 万能表单编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, DiyFormParam editParam);

    /**
     * 万能表单删除
     * @param formIds 主键ID
     */
    void del(List<Integer> formIds);

    /**
     * 页面加载初始化
     * @param params 参数
     * @return 初始化数据
     * @throws Exception 可能抛出的数据库查询异常
     */
    DiyFormInitVo getInit(DiyFormInitParam params);

    /**
     * 修改分享内容
     * @param share 分享内容数据
     * @return 是否修改成功
     */
    boolean modifyShare(Integer formId, String share);

    /**
     * 获取组件列表（表单组件+自定义装修的组件）
     * @param type 支持表单类型
     * @return 组件列表
     */
    JSONObject getComponentList(String type);

    /**
     * 获取万能表单模板数据
     * @param type 类型
     * @param name 名称
     * @return 模板数据
     */
    JSONObject getPageData(String type, String name);

    /**
     * 复制万能表单
     * @param formId 参数
     * @return 复制后的表单 ID
     */
    int copy(Integer formId);

    /**
     * 获取页面模板
     * @return 页面模板数据
     */
    JSONObject getTemplate(DiyFormTemplateParam params);

    /**
     * 获取万能表单类型
     * @return 表单类型列表
     */
    JSONObject getFormType();

    /**
     * 修改状态
     */
    boolean modifyStatus(DiyFormStatusParam diyFormStatusParam);

    /**
     * 获取使用记录
     * @param searchParam 数据
     * @return 使用记录分页数据
     */
    PageResult<DiyFormRecordsListVo> getRecordPages(PageParam pageParam, DiyFormRecordsSearchParam searchParam);

    /**
     * 获取使用记录
     * @param recordId 记录 ID
     * @return 使用记录信息
     */
    DiyFormRecordsInfoVo getRecordInfo(int recordId);

    /**
     * 删除使用记录
     * @param formId
     * @param recordId
     * @return
     */
    boolean delRecord(Integer formId, Integer recordId);

    /**
     * 获取万能表单字段列表

     * @return 字段列表数据
     */
    List<DiyFormFieldsListVo> getFieldsList(DiyFormRecordsFieldsSearchParam diyFormRecordsFieldsSearchParam);

    /**
     * 获取万能表单微信小程序二维码
     * @param formId 表单 ID
     * @return 二维码路径信息
     * @throws Exception 可能抛出的异常
     */
    Map<String, Object> getQrcode(int formId);

}
