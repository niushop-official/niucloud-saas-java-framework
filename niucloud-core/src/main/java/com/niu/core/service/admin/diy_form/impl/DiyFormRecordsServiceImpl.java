package com.niu.core.service.admin.diy_form.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.diy_form.DiyFormFields;
import com.niu.core.entity.diy_form.DiyFormRecords;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.diy_form.DiyFormComponentEnum;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsMapper;
import com.niu.core.service.admin.diy_form.IDiyFormRecordsService;
import com.niu.core.service.admin.diy_form.IDiyFormService;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsFieldsSearchParam;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.admin.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.admin.diy_form.vo.DiyFormFieldsListVo;
import com.niu.core.service.admin.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.admin.diy_form.vo.DiyFormRecordsListVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsFieldsListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 万能表单填写记录实现
 */
@Service
public class DiyFormRecordsServiceImpl implements IDiyFormRecordsService {
        
    @Resource
    DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    IDiyFormService diyFormService;


    @Override
    public PageResult<DiyFormRecordsListVo> getPage(PageParam pageParam, DiyFormRecordsSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();
        MPJQueryWrapper<DiyFormRecords> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("ndfr.form_id, ndfr.member_id, nm.username, nm.member_no, nm.mobile, nm.nickname, nm.headimg,count(*) as write_count")
                .setAlias("ndfr")
                .leftJoin("?_member nm ON ndfr.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("ndfr.site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getFormId())) {
            queryWrapper.eq("ndfr.form_id", searchParam.getFormId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.or(j -> j.like("nm.nickname", searchParam.getKeyword()))
                    .or(j-> j.like("nm.mobile", searchParam.getKeyword()))
            );
        }
        queryWrapper.groupBy("ndfr.member_id");


        queryWrapper.orderByDesc("ndfr.form_id");
        IPage<DiyFormRecordsListVo> iPage = diyFormRecordsMapper.selectJoinPage(new Page<>(page, limit), DiyFormRecordsListVo.class, queryWrapper);
        for (DiyFormRecordsListVo vo : iPage.getRecords()) {
            Member memberVo = new Member();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
        }
        return PageResult.build(iPage);
    }

    @Override
    public List<DiyFormFieldsListVo> getFieldStatList(DiyFormRecordsSearchParam searchParam) {

        DiyFormRecordsFieldsSearchParam diyFormRecordsFieldsSearchParam = new DiyFormRecordsFieldsSearchParam();
        diyFormRecordsFieldsSearchParam.setFormId(searchParam.getFormId());
        List<DiyFormFieldsListVo> fieldsList = diyFormService.getFieldsList(diyFormRecordsFieldsSearchParam);

        // 过滤简单字段列表
        String[] simpleTypes = {"FormRadio", "FormCheckbox", "FormDateScope", "FormTimeScope", "FormImage"};
        List<DiyFormFieldsListVo> simpleFieldList = fieldsList.stream()
                .filter(e -> !Arrays.asList(simpleTypes).contains(e.getFieldType()))
                .collect(Collectors.toList());

        // 过滤 JSON 字段列表
        String[] jsonTypes = {"FormRadio", "FormCheckbox", "FormDateScope", "FormTimeScope"};
        List<DiyFormFieldsListVo> jsonFieldList = fieldsList.stream()
                .filter(e -> Arrays.asList(jsonTypes).contains(e.getFieldType()))
                .collect(Collectors.toList());

        long totalCount = 0;
        for (DiyFormFieldsListVo field : simpleFieldList) {
            // 统计每个字段值的填写数量
            QueryWrapper<DiyFormRecordsFields> valueQueryWrapper = new QueryWrapper<>();
            valueQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("field_key", field.getFieldKey())
                    .eq("field_type", field.getFieldType());
            if (searchParam.getFormId() != null) {
                valueQueryWrapper.eq("form_id", searchParam.getFormId());
            }
            List<Map<String, Object>> valueList = diyFormRecordsFieldsMapper.selectMaps(valueQueryWrapper
                    .select("form_id, field_key, field_type, field_value, count(*) as write_count")
                    .groupBy("field_value"));
            for (int i = 0; i < valueList.size(); i++) {
                Map<String, Object> value = valueList.get(i);
                DiyFormComponentEnum diyFormComponentEnum = new DiyFormComponentEnum();
                JSONObject component = diyFormComponentEnum.getComponent(value.get("field_type").toString());
                if(component.containsKey("render")){
                    String driver = component.getStr("render");
                    if(ObjectUtil.isNotEmpty(driver))
                    {
                        try{
                            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                            Object obj = clazz.getDeclaredConstructor().newInstance();
                            Method method = clazz.getMethod("render", String.class, String.class);
                            value.put("render_value", method.invoke(obj, value.get("field_value").toString(), value.get("field_type").toString()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else{
                    value.put("render_value", value.get("field_value"));
                }
            }

            // 计算总数量
            totalCount = diyFormRecordsFieldsMapper.selectCount(new QueryWrapper<DiyFormRecordsFields>().eq("site_id", RequestUtils.siteId())
                    .eq("field_key", field.getFieldKey())
                    .eq("field_type", field.getFieldType()));

            if (totalCount > 0) {
                double totalPercent = 100;
                for (int i = 0; i < valueList.size(); i++) {
                    Map<String, Object> value = valueList.get(i);
                    double itemPercent;
                    if (i == valueList.size() - 1) {
                        itemPercent = totalPercent;
                    } else {
                        itemPercent = Math.round((Integer.parseInt(value.get("write_count").toString()) / (double) totalCount) * 100 * 100) / 100.0;
                    }
                    value.put("write_percent", itemPercent);
                    totalPercent = totalPercent - itemPercent;
                }
            }
            field.setValueList(valueList);
        }

        // 处理 JSON 字段列表
        for (DiyFormFieldsListVo field : jsonFieldList) {
            // 查询字段记录
            QueryWrapper<DiyFormRecordsFields> fieldQueryWrapper = new QueryWrapper<>();
            fieldQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("field_key", field.getFieldKey())
                    .eq("field_type", field.getFieldType());
            if (searchParam.getFormId() != null) {
                fieldQueryWrapper.eq("form_id", searchParam.getFormId());
            }
            List<Map<String, Object>> fieldList = diyFormRecordsFieldsMapper.selectMaps(fieldQueryWrapper);
            for (int i = 0; i < fieldList.size(); i++) {
                Map<String, Object> value = fieldList.get(i);
                DiyFormComponentEnum diyFormComponentEnum = new DiyFormComponentEnum();
                JSONObject component = diyFormComponentEnum.getComponent(value.get("field_type").toString());
                if(component.containsKey("render")){
                    String driver = component.getStr("render");
                    if(ObjectUtil.isNotEmpty(driver))
                    {
                        try{
                            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
                            Object obj = clazz.getDeclaredConstructor().newInstance();
                            Method method = clazz.getMethod("render", String.class, String.class);
                            value.put("render_value", method.invoke(obj, value.get("field_value").toString(), value.get("field_type").toString()));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }else{
                    value.put("render_value", value.get("field_value"));
                }
            }
            totalCount = 0;
            Map<String, Map<String, Object>> valueMap = new HashMap<>();
            for (Map<String, Object> record : fieldList) {
                if (!"FormCheckbox".equals(record.get("field_type"))) {
                    String key = record.get("field_key") + "_" + record.get("render_value");
                    if (valueMap.containsKey(key)) {
                        valueMap.get(key).put("write_count", (Integer) valueMap.get(key).get("write_count") + 1);
                        totalCount++;
                    } else {
                        valueMap.put(key, record);
                        valueMap.get(key).put("write_count", 1);
                        totalCount++;
                    }
                } else {
                    String[] values = record.get("render_value").toString().split(",");
                    for (String value : values) {
                        String key = record.get("field_key") + "_" + value;
                        if (valueMap.containsKey(key)) {
                            valueMap.get(key).put("write_count", (Integer) valueMap.get(key).get("write_count") + 1);
                            totalCount++;
                        } else {
                            Map<String, Object> newRecord = new HashMap<>(record);
                            newRecord.put("render_value", value);
                            newRecord.put("write_count", 1);
                            valueMap.put(key, newRecord);
                            totalCount++;
                        }
                    }
                }
            }

            if (totalCount > 0) {
                List<Map<String, Object>> valueList = new ArrayList<>(valueMap.values());
                double totalPercent = 100;
                for (int i = 0; i < valueList.size(); i++) {
                    Map<String, Object> value = valueList.get(i);
                    double itemPercent;
                    if (i == valueList.size() - 1) {
                        itemPercent = totalPercent;
                    } else {
                        itemPercent = Math.round((Integer.parseInt(value.get("write_count").toString()) / (double) totalCount) * 100 * 100) / 100.0;
                    }
                    value.put("write_percent", itemPercent);
                    totalPercent = totalPercent - itemPercent;
                }
                field.setValueList(valueList);
            }
        }

        // 合并结果
        List<DiyFormFieldsListVo> resultList = new ArrayList<>();
        resultList.addAll(simpleFieldList);
        resultList.addAll(jsonFieldList);

        return resultList;
    }

}
