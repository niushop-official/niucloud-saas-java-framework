package com.niu.core.service.core.diy_form.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy_form.DiyForm;
import com.niu.core.entity.diy_form.DiyFormFields;
import com.niu.core.entity.diy_form.DiyFormRecords;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.diy_form.DiyFormFieldsMapper;
import com.niu.core.mapper.diy_form.DiyFormMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsMapper;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsFieldsListVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;
import io.swagger.models.auth.In;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;


/**
 * 万能表单填写记录实现
 */
@Service
public class CoreDiyFormRecordsServiceImpl implements ICoreDiyFormRecordsService {
        
    @Resource
    DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    DiyFormFieldsMapper diyFormFieldsMapper;


    @Override
    public PageResult<DiyFormRecordsListVo> page(PageParam pageParam, DiyFormRecordsSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        MPJQueryWrapper<DiyFormRecords> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("ndfr.record_id, ndfr.site_id, ndfr.form_id, ndfr.value, ndfr.member_id, ndfr.relate_id, ndfr.create_time, nm.username, nm.member_no, nm.mobile, nm.nickname, nm.headimg")
                .setAlias("ndfr")
                .leftJoin("?_member nm ON ndfr.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("ndfr.site_id", searchParam.getSiteId());
        if (ObjectUtil.isNotEmpty(searchParam.getFormId())) {
            queryWrapper.eq("ndfr.form_id", searchParam.getFormId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("nm.nickname", searchParam.getKeyword()))
                    .or(j-> j.like("nm.username", searchParam.getKeyword()))
                    .or(j-> j.like("nm.member_no", searchParam.getKeyword()))
                    .or(j-> j.like("nm.mobile", searchParam.getKeyword()))
            );
        }

        queryWrapper.orderByDesc("ndfr.create_time");
        IPage<DiyFormRecordsListVo> iPage = diyFormRecordsMapper.selectJoinPage(new Page<>(page, limit), DiyFormRecordsListVo.class, queryWrapper);

        for (DiyFormRecordsListVo vo : iPage.getRecords()) {
            List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(new QueryWrapper<DiyFormRecordsFields>().eq("record_id", vo.getRecordId()));

            Member memberVo = new Member();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
            if(ObjectUtil.isNotEmpty(list)){
                Map<String, DiyFormRecordsFieldsListVo> map = new HashMap<>();
                for (DiyFormRecordsFields item : list) {
                    DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                    BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                    map.put(item.getFieldKey(), diyFormRecordsFieldsListVo);
                }
                vo.setRecordsFieldList(map);
            }
        }
        return PageResult.build(iPage);
    }

    @Override
    public DiyFormRecordsInfoVo info(Integer id) {
        DiyFormRecords model = diyFormRecordsMapper.selectOne(
        new QueryWrapper<DiyFormRecords>()
        .eq("record_id", id)
        .eq("site_id", RequestUtils.siteId()));


        Assert.notNull(model, "万能表单填写记录不存在");

        DiyFormRecordsInfoVo vo = new DiyFormRecordsInfoVo();
        BeanUtils.copyProperties(model, vo);
        List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(new QueryWrapper<DiyFormRecordsFields>().eq("record_id", vo.getRecordId()));
        if(ObjectUtil.isNotEmpty(list)){
            Map<String, DiyFormRecordsFieldsListVo> map = new HashMap<>();
            for (DiyFormRecordsFields item : list) {
                DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                map.put(item.getFieldKey(), diyFormRecordsFieldsListVo);
            }
            vo.setRecordsFieldList(map);
        }
        return vo;
    }

    @Override
    @Transactional
    public Integer add(DiyFormRecordsParam addParam) {

        DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("site_id", addParam.getSiteId()).eq("form_id", addParam.getFormId()));
        if (diyForm == null) return null;

        if (diyForm.getStatus() == 0) return null;

        DiyFormRecords model = new DiyFormRecords();
        BeanUtils.copyProperties(addParam, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        diyFormRecordsMapper.insert(model);
        List<DiyFormFields> list = diyFormFieldsMapper.selectList(new QueryWrapper<DiyFormFields>().eq("form_id", addParam.getFormId()).eq("site_id", addParam.getSiteId()));
        Map<String, DiyFormFields> fieldList = new HashMap<>();
        if(ObjectUtil.isNotEmpty(list)){

            for (DiyFormFields item : list) {
                fieldList.put(item.getFieldKey(), item);
            }
        }
        List<DiyFormRecordsFields> recordsFieldsList = new ArrayList<>();

        if(ObjectUtil.isNotEmpty(addParam.getValue()))
        {
            JSONArray components = JSONUtil.parseArray(addParam.getValue());
            for (Object componentObj : components) {
                JSONObject component = JSONUtil.parseObj(componentObj);
                // 过滤非表单组件和表单提交按钮组件
                if (!"diy_form".equals(component.getStr("componentType")) || "FormSubmit".equals(component.getStr("componentName")) || component.getBool("isHidden")) {
                    continue;
                }

                JSONObject field = component.getJSONObject("field");
                Object fieldValue = field.get("value");
                Object checkFieldValue = fieldValue;
                if (fieldValue instanceof List) {
                    // 这里假设 DiyFormRecordsFieldsMapper 有对应的方法，需要根据实际情况修改
                  //  checkFieldValue = diyFormRecordsFieldsMapper.getRenderValueAttr("", (List<?>) fieldValue, (String) component.get("componentName"));
                    checkFieldValue = "";
                }

                DiyFormFields formFieldInfo = fieldList.get(component.getStr("id"));
                if (formFieldInfo != null) {
                    if (formFieldInfo.getFieldRequired() == 1 && fieldValue == null) {
                        throw new CommonException((String) field.getOrDefault("name", component.get("componentTitle")) + "不能为空");
                    } else if (checkFieldValue == null) {
                        // 过滤空数据
                        continue;
                    }

                    // 检测字段是否重复
                    QueryWrapper<DiyFormRecordsFields> uniqueQuery = new QueryWrapper<>();
                    uniqueQuery.eq("site_id", addParam.getSiteId())
                            .eq("form_id", addParam.getFormId())
                            .eq("field_key", component.get("id"))
                            .eq("field_type", component.get("componentName"));
                    List<Object> fieldValues = diyFormRecordsFieldsMapper.selectObjs(uniqueQuery);
                    if (formFieldInfo.getFieldUnique() == 1 && fieldValues.contains(fieldValue)) {
                        throw new CommonException((String) field.getOrDefault("name", component.get("componentTitle")) + "不能重复");
                    }
                } else if (checkFieldValue == null) {
                    // 过滤空数据
                    continue;
                }

                if (fieldValue instanceof List) {
                    // 这里假设使用 JSON 工具类，需要根据实际情况引入
                    fieldValue = JSONUtil.parseArray(fieldValue).toString();
                }

                DiyFormRecordsFields recordsField = new DiyFormRecordsFields();
                recordsField.setSiteId(addParam.getSiteId());
                recordsField.setFormId(addParam.getFormId());
                recordsField.setRecordId(model.getRecordId());
                recordsField.setMemberId(addParam.getMemberId());
                recordsField.setFieldKey((String) component.get("id"));
                recordsField.setFieldType((String) component.get("componentName"));
                recordsField.setFieldName((String) field.getOrDefault("name", ""));
                recordsField.setFieldValue(fieldValue.toString());
                recordsField.setFieldRequired((Boolean) field.get("required") ? 1 : 0);
                recordsField.setFieldHidden((Boolean) component.get("isHidden") ? 1 : 0);
                recordsField.setFieldUnique(component.containsKey("unique")&&(Boolean) component.get("unique") ? 1 : 0);
                recordsField.setPrivacyProtection(component.containsKey("privacyProtection")&&(Boolean) component.get("privacyProtection") ? 1 : 0);
                recordsField.setCreateTime(System.currentTimeMillis() / 1000);
                recordsField.setUpdateTime(System.currentTimeMillis() / 1000);
                recordsFieldsList.add(recordsField);
            }
        }

        if (!recordsFieldsList.isEmpty()) {
            diyFormRecordsFieldsMapper.insert(recordsFieldsList);

            // 累计填写数量
            diyFormMapper.update(new UpdateWrapper<DiyForm>().eq("form_id", addParam.getFormId()).setSql(" write_num = write_num + 1"));

            for (DiyFormRecordsFields field : recordsFieldsList) {
                // 字段累计填写数量
                diyFormFieldsMapper.update(new UpdateWrapper<DiyFormFields>().eq("form_id", addParam.getFormId()).eq("field_key", field.getFieldKey()).eq("site_id", addParam.getSiteId()).setSql(" write_num = write_num + 1"));
            }
        }
        return model.getRecordId();

    }

    @Override
    public void del(Integer siteId, Integer id) {
        diyFormRecordsMapper.delete(new QueryWrapper<DiyFormRecords>().eq("record_id", id).eq("site_id", siteId));
    }

}
