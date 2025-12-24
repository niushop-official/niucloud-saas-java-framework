package com.niu.core.service.admin.diy_form.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy_form.*;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.enums.diy_form.DiyFormComponentEnum;
import com.niu.core.enums.diy_form.DiyFormTemplateEnum;
import com.niu.core.enums.diy_form.DiyFormTypeEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.event.diy.DiyFormDelBeforeEventDefiner;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.diy_form.*;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy_form.IDiyFormService;
import com.niu.core.service.admin.diy_form.param.*;
import com.niu.core.service.admin.diy_form.vo.*;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy_form.ICoreDiyFormConfigService;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsSearchParam;
import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormRecordsListVo;
import com.niu.core.service.core.sys.ICoreScanService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.poifs.filesystem.Entry;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 万能表单实现
 */
@Service
@Slf4j
public class DiyFormServiceImpl implements IDiyFormService {
        
    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    DiyFormFieldsMapper diyFormFieldsMapper;

    @Resource
    DiyFormRecordsMapper diyFormRecordsMapper;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;

    @Resource
    DiyFormSubmitConfigMapper diyFormSubmitConfigMapper;

    @Resource
    DiyFormWriteConfigMapper diyFormWriteConfigMapper;

    @Resource
    ICoreDiyFormConfigService coreDiyFormConfigService;

    @Resource
    ICoreDiyFormRecordsService coreDiyFormRecordsService;

    @Resource
    IDiyService diyService;

    @Resource
    ICoreSysConfigService systemConfigService;

    @Resource
    SqlSessionFactory sqlSessionFactory;

    @Resource
    private AddonMapper addonMapper;


    @Override
    public PageResult<DiyFormListVo> getPage(PageParam pageParam, DiyFormSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<DiyForm> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId());

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.and(i -> i.or(j -> j.like("title", searchParam.getTitle()))
                    .or(j-> j.like("page_title", searchParam.getTitle()))
            );
        }
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
        queryWrapper.eq("type", searchParam.getType());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getAddon())) {
        queryWrapper.eq("addon", searchParam.getAddon());
        }

        queryWrapper.orderByDesc("form_id");

        IPage<DiyForm> iPage = diyFormMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<DiyFormListVo> list = new LinkedList<>();
        for (DiyForm item : iPage.getRecords()) {
            DiyFormListVo vo = new DiyFormListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<DiyFormListVo> getList(DiyFormSearchParam searchParam) {

        QueryWrapper<DiyForm> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId());

        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.and(i -> i.or(j -> j.like("title", searchParam.getTitle()))
                    .or(j-> j.like("page_title", searchParam.getTitle()))
            );
        }
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq("type", searchParam.getType());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getAddon())) {
            queryWrapper.eq("addon", searchParam.getAddon());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq("status", searchParam.getStatus());
        }
        queryWrapper.orderByDesc("form_id");
        List<DiyForm> list = diyFormMapper.selectList(queryWrapper);
        List<DiyFormListVo> voList = new LinkedList<>();
        for (DiyForm item : list) {
            DiyFormListVo vo = new DiyFormListVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public DiyFormInfoVo getInfo(Integer id) {
        DiyForm model = diyFormMapper.selectOne(
        new QueryWrapper<DiyForm>()
        .eq("form_id", id)
        .eq("site_id", RequestUtils.siteId()));


        Assert.notNull(model, "万能表单不存在");

        DiyFormInfoVo vo = new DiyFormInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public Long getCount(DiyFormSearchParam searchParam) {
        QueryWrapper<DiyForm> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq("type", searchParam.getType());
        }
        Long count = diyFormMapper.selectCount(queryWrapper);
        if(ObjectUtil.isEmpty(count))
        {
            count = 0L;
        }
        return count;

    }

    @Override
    @Transactional
    public Integer add(DiyFormParam addParam) {
        checkPageTitleUnique(addParam.getPageTitle(), 0);

        DiyForm model = new DiyForm();

        model.setPageTitle(addParam.getPageTitle());
        model.setTitle(addParam.getTitle());
        model.setType(addParam.getType());
        model.setStatus(1);
        model.setTemplate(addParam.getTemplate());
        model.setValue(addParam.getValue());
        DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
        JSONObject type = diyFormTypeEnum.getType(addParam.getType());
        if(type.containsKey("addon"))
        {
            model.setAddon(type.getStr("addon"));
        }else{
            model.setAddon("");
        }

        model.setShare(addParam.getShare());
        model.setRemark(addParam.getRemark());
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormMapper.insert(model);
        List<DiyFormFields> diyFormFields = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(addParam.getValue())) {
            JSONObject value = JSONUtil.parseObj(addParam.getValue());
            JSONArray components = value.getJSONArray("value");
            for (Object componentObj : components) {
                JSONObject component = JSONUtil.parseObj(componentObj);
                String componentType = component.getStr("componentType");
                String componentName = component.getStr("componentName");
                if (!"diy_form".equals(componentType) || "FormSubmit".equals(componentName)) {
                    continue;
                }
                JSONObject field = component.getJSONObject("field");

                DiyFormFields fieldRecord = new DiyFormFields();
                fieldRecord.setSiteId(RequestUtils.siteId());
                fieldRecord.setFormId(model.getFormId());
                fieldRecord.setFieldKey(component.get("id").toString());
                fieldRecord.setFieldType(componentName);
                fieldRecord.setFieldName(field.containsKey("name") ? field.getStr("name") : "");
                if(field.containsKey("remark"))
                {
                    JSONObject remark = field.getJSONObject("remark");
                    if(remark.containsKey("text"))
                    {
                        fieldRecord.setFieldRemark(remark.getStr("text"));
                    }else{
                        fieldRecord.setFieldRemark("");
                    }
                }else{
                    fieldRecord.setFieldRemark("");
                }
                fieldRecord.setFieldDefault(field.containsKey("default") ? field.get("default").toString() : "");
                fieldRecord.setFieldRequired(field.containsKey("required") ? field.getInt("required") : 0);
                fieldRecord.setFieldHidden(component.getInt("isHidden"));
                fieldRecord.setFieldUnique(field.containsKey("unique") ? field.getInt("unique") : 0);
                fieldRecord.setPrivacyProtection(field.containsKey("privacyProtection") ? field.getInt("privacyProtection") : 0);
                fieldRecord.setCreateTime(System.currentTimeMillis() / 1000);
                fieldRecord.setUpdateTime(System.currentTimeMillis() / 1000);
                diyFormFields.add(fieldRecord);
            }
        }
        if (!diyFormFields.isEmpty()) {
            diyFormFieldsMapper.insert(diyFormFields);
        }

        DiyFormWriteConfigParam writeParam = new DiyFormWriteConfigParam();
        writeParam.setSiteId(RequestUtils.siteId());
        writeParam.setFormId(model.getFormId());
        // 初始化表单填写配置
        coreDiyFormConfigService.addWriteConfig(writeParam);

        DiyFormSubmitConfigParam submitConfigParam = new DiyFormSubmitConfigParam();
        submitConfigParam.setSiteId(RequestUtils.siteId());
        submitConfigParam.setFormId(model.getFormId());
        // 初始化表单提交成功页配置
        coreDiyFormConfigService.addSubmitConfig(submitConfigParam);

        return model.getFormId();
    }

    @Override
    public void edit(Integer id, DiyFormParam editParam) {
        checkPageTitleUnique(editParam.getPageTitle(), id);

        DiyForm model = diyFormMapper.selectOne(
                new QueryWrapper<DiyForm>()
                        .eq("form_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "万能表单不存在");
        model.setTitle(editParam.getTitle());
        model.setPageTitle(editParam.getPageTitle());
        model.setTemplate(editParam.getTemplate());
        model.setValue(editParam.getValue());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormMapper.updateById(model);
        List<DiyFormFields> formFieldsList = diyFormFieldsMapper.selectList(new QueryWrapper<DiyFormFields>().eq("form_id", id));
        Map<String, DiyFormFields> formFieldsListMap = formFieldsList.stream().collect(Collectors.toMap(DiyFormFields::getFieldKey, a -> a));
        List<String> existFieldKeys = new ArrayList<>();
        List<DiyFormFields> diyFormFields = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(editParam.getValue())) {
            JSONObject value = JSONUtil.parseObj(editParam.getValue());
            JSONArray components = value.getJSONArray("value");
            for (Object componentObj : components) {
                JSONObject component = JSONUtil.parseObj(componentObj);
                String componentType = component.getStr("componentType");
                String componentName = component.getStr("componentName");
                if (!"diy_form".equals(componentType) || "FormSubmit".equals(componentName)) {
                    continue;
                }
                JSONObject field = component.getJSONObject("field");

                DiyFormFields fieldRecord = new DiyFormFields();
                fieldRecord.setSiteId(RequestUtils.siteId());
                fieldRecord.setFormId(model.getFormId());
                fieldRecord.setFieldKey(component.get("id").toString());
                fieldRecord.setFieldType(componentName);
                fieldRecord.setFieldName(field.containsKey("name") ? field.getStr("name") : "");
                if(field.containsKey("remark"))
                {
                    JSONObject remark = field.getJSONObject("remark");
                    if(remark.containsKey("text"))
                    {
                        fieldRecord.setFieldRemark(remark.getStr("text"));
                    }else{
                        fieldRecord.setFieldRemark("");
                    }
                }else{
                    fieldRecord.setFieldRemark("");
                }
                fieldRecord.setFieldDefault(field.containsKey("default") ? field.get("default").toString() : "");
                fieldRecord.setFieldRequired(field.containsKey("required") ? field.getInt("required") : 0);
                fieldRecord.setFieldHidden(component.getInt("isHidden"));
                fieldRecord.setFieldUnique(field.containsKey("unique") ? field.getInt("unique") : 0);
                fieldRecord.setPrivacyProtection(field.containsKey("privacyProtection") ? field.getInt("privacyProtection") : 0);
                fieldRecord.setUpdateTime(System.currentTimeMillis() / 1000);

                if(formFieldsListMap.containsKey(component.getStr("id")))
                {
                    diyFormFieldsMapper.update(fieldRecord, new QueryWrapper<DiyFormFields>().eq("site_id", RequestUtils.siteId()).eq("field_id", formFieldsListMap.get(component.getStr("id")).getFieldId()));
                    existFieldKeys.add(component.getStr("id"));
                }else{
                    diyFormFields.add(fieldRecord);
                }

            }
            if (!diyFormFields.isEmpty()) {
                diyFormFieldsMapper.insert(diyFormFields);
            }
            for (Map.Entry<String, DiyFormFields> entry : formFieldsListMap.entrySet()) {

                if(!existFieldKeys.contains(entry.getKey())) {
                    diyFormFieldsMapper.delete(new QueryWrapper<DiyFormFields>().eq("site_id", RequestUtils.siteId()).eq("field_id", entry.getValue().getFieldId()));
                }
            }
        }else{
            diyFormFieldsMapper.delete(new QueryWrapper<DiyFormFields>().eq("site_id", RequestUtils.siteId()).eq("form_id", model.getFormId()));
        }

    }

    @Override
    @Transactional
    public void del(List<Integer> formIds) {
        Long count = diyFormMapper.selectCount(new QueryWrapper<DiyForm>().in("form_id", formIds).eq("site_id", RequestUtils.siteId()).eq("status", 1));
        if(count > 0){
            throw new CommonException("存在正在使用的表单，无法删除");
        }
        //事件检测是否可以被删除
        DiyFormDelBeforeEventDefiner.DiyFormDelBeforeEvent event = new DiyFormDelBeforeEventDefiner.DiyFormDelBeforeEvent();
        for (Integer formId : formIds) {
            event.setFormId(formId);
            event.setSiteId(RequestUtils.siteId());
            List<DiyFormDelBeforeEventDefiner.DiyFromDelBeforeResult> result = EventAndSubscribeOfPublisher.publishAndCallback(event);
            for (DiyFormDelBeforeEventDefiner.DiyFromDelBeforeResult res : result) {
                if(ObjectUtil.isNotEmpty(res))
                {
                    if(!res.getAllowOperate())  throw new CommonException("存在正在使用的表单，无法删除");
                }
            }
        }

        diyFormMapper.delete(new QueryWrapper<DiyForm>().eq("site_id", RequestUtils.siteId()).in("form_id", formIds));
        diyFormFieldsMapper.delete(new QueryWrapper<DiyFormFields>().eq("site_id", RequestUtils.siteId()).in("form_id", formIds));
        diyFormSubmitConfigMapper.delete(new QueryWrapper<DiyFormSubmitConfig>().eq("site_id", RequestUtils.siteId()).in("form_id", formIds));
        diyFormWriteConfigMapper.delete(new QueryWrapper<DiyFormWriteConfig>().eq("site_id", RequestUtils.siteId()).in("form_id", formIds));
    }

    @Override
    public DiyFormInitVo getInit(DiyFormInitParam params) {

        long time = System.currentTimeMillis() / 1000;
        DiyFormInfoVo data = new DiyFormInfoVo();

        if (!params.getFormId().equals(0)) {
            data = getInfo(params.getFormId());
        }

        if (ObjectUtil.isNotEmpty(data.getType())) {
            DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
            JSONObject currentType = diyFormTypeEnum.getType(data.getType());
            String typeName = currentType.getStr("title");
            data.setTypeName(typeName);
        } else {
            if (ObjectUtil.isEmpty(params.getType())) {
                throw new CommonException("DIY_FORM_TYPE_NOT_EXIST");
            }
            String type = params.getType();

            // 新页面赋值
            String pageTitle = params.getTitle() != null ? params.getTitle() : "表单页面" + time;

            DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
            JSONObject currentType = diyFormTypeEnum.getType(params.getType());
            String typeName = currentType.getStr("title");
            String value = "";

            data = new DiyFormInfoVo();
            data.setFormId(0);
            data.setPageTitle(pageTitle);
            data.setTitle(typeName);
            data.setType(type);
            data.setTypeName(typeName);
            data.setValue(value);
            data.setStatus(1);
        }

        DiyFormInitVo initVo = new DiyFormInitVo();
        BeanUtils.copyProperties(data, initVo);
        initVo.setComponent(getComponentList(data.getType()));
        initVo.setDomainUrl(systemConfigService.getSceneDomain(RequestUtils.siteId()));
        return initVo;
    }

    @Override
    public boolean modifyShare(Integer formId, String share) {
        DiyForm diyForm = new DiyForm();
        diyForm.setShare(share);
        diyFormMapper.update(diyForm, new QueryWrapper<DiyForm>().eq("form_id", formId).eq("site_id", RequestUtils.siteId()));
        return true;
    }

    @Override
    public JSONObject getComponentList(String type) {

        DiyFormComponentEnum formComponentEunm = new DiyFormComponentEnum();
        JSONObject formComponentList = formComponentEunm.getComponent();
        for (String formComponentObj:formComponentList.keySet()) {
            JSONObject formComponent = formComponentList.getJSONObject(formComponentObj);
            JSONObject list = formComponent.getJSONObject("list");
            Iterator<Map.Entry<String, Object>> iterator = list.entrySet().iterator();
            while (iterator.hasNext()) {
                JSONObject cv = JSONUtil.parseObj(iterator.next());
                if(cv.containsKey("support"))
                {
                    JSONArray support = cv.getJSONArray("support");
                    if (support != null && support.size() > 0 && !support.contains(type)) {
                        iterator.remove();
                        continue;
                    }
                }
                cv.remove("sort");
                cv.remove("support");
            }
            // 根据 sort 排序
            formComponent.put("list", sortJSONObjectBySortField(list));
        }

        componentType(formComponentList, "diy_form");

        JSONObject data = formComponentList;
        if ("DIY_FORM".equals(type)) {
            JSONObject diyComponentList = diyService.getComponentList("");
            componentType(diyComponentList, "diy");
            data = mergeJsonObjects(formComponentList, diyComponentList);;
        }
        return data;

    }
    private void componentType(JSONObject componentList, String type) {
        if (componentList != null && !componentList.isEmpty()) {
            for (String value : componentList.keySet()) {
                JSONObject valueObj = componentList.getJSONObject(value);
                JSONObject list = valueObj.getJSONObject("list");
                if (list != null && !list.isEmpty()) {
                    for (String v : list.keySet()) {
                        JSONObject vObj = list.getJSONObject(v);
                        vObj.putOpt("componentType", type);
                    }
                }
            }
        }
    }

    private JSONObject sortJSONObjectBySortField(JSONObject originalJsonObject) {
        // 提取所有内部 JSONObject 到一个 List 中
        List<Map.Entry<String, Object>> entryList = new ArrayList<>(originalJsonObject.entrySet());

        // 对 List 进行排序
        Collections.sort(entryList, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                JSONObject jsonObj1 = (JSONObject) o1.getValue();
                JSONObject jsonObj2 = (JSONObject) o2.getValue();
                int sort1 = jsonObj1.getInt("sort");
                int sort2 = jsonObj2.getInt("sort");
                return Integer.compare(sort1, sort2);
            }
        });

        // 创建一个新的 JSONObject 来存放排序后的结果
        JSONObject sortedJsonObject = new JSONObject();
        for (Map.Entry<String, Object> entry : entryList) {
            sortedJsonObject.put(entry.getKey(), entry.getValue());
        }

        return sortedJsonObject;
    }
    private JSONObject mergeJsonObjects(JSONObject jsonObject1, JSONObject jsonObject2) {
        Set<String> keySet = jsonObject2.keySet();
        for (String key : keySet) {
            Object value = jsonObject2.get(key);
            if (jsonObject1.containsKey(key)) {
                Object existingValue = jsonObject1.get(key);
                if (existingValue instanceof JSONObject && value instanceof JSONObject) {
                    // 递归合并嵌套的 JSONObject
                    jsonObject1.put(key, mergeJsonObjects((JSONObject) existingValue, (JSONObject) value));
                } else if (existingValue instanceof JSONArray && value instanceof JSONArray) {
                    // 合并 JSONArray
                    JSONArray array = new JSONArray();
                    array.addAll((JSONArray) existingValue);
                    array.addAll((JSONArray) value);
                    jsonObject1.put(key, array);
                } else {
                    // 覆盖原有值
                    jsonObject1.put(key, value);
                }
            } else {
                jsonObject1.put(key, value);
            }
        }
        return jsonObject1;
    }
    @Override
    public JSONObject getPageData(String type, String name) {
        return ObjectUtil.defaultIfNull(PagesEnum.getPages(type, name), new JSONObject());
    }

    @Override
    @Transactional
    public int copy(Integer formId) {
        DiyForm diyForm = diyFormMapper.selectOne(new QueryWrapper<DiyForm>().eq("form_id", formId).eq("site_id", RequestUtils.siteId()));
        if(ObjectUtil.isEmpty(diyForm)) throw new CommonException("万能表单不存在");
        DiyFormSubmitConfig diyFormSubmitConfig = diyFormSubmitConfigMapper.selectOne(new QueryWrapper<DiyFormSubmitConfig>().eq("form_id", formId));
        DiyFormWriteConfig diyFormWriteConfig = diyFormWriteConfigMapper.selectOne(new QueryWrapper<DiyFormWriteConfig>().eq("form_id", formId));
        List<DiyFormFields> diyFormFields = diyFormFieldsMapper.selectList(new QueryWrapper<DiyFormFields>().eq("form_id", formId));

        // 复制表单信息
        DiyForm newDiyForm = new DiyForm();
        BeanUtils.copyProperties(diyForm, newDiyForm);
        newDiyForm.setFormId(null); // 清空原有的 form_id，让数据库自动生成新的
        newDiyForm.setPageTitle(newDiyForm.getPageTitle() + "_副本");
        newDiyForm.setStatus(0);
        newDiyForm.setShare("");
        newDiyForm.setWriteNum(0);
        long currentTime = System.currentTimeMillis() / 1000;
        newDiyForm.setCreateTime(currentTime);
        newDiyForm.setUpdateTime(currentTime);

        // 插入新的表单信息
        diyFormMapper.insert(newDiyForm);
        int newFormId = newDiyForm.getFormId();

        // 复制表单字段
        if (!ObjectUtil.isEmpty(diyFormFields)) {
            List<DiyFormFields> newFormFieldList = new ArrayList<>();
            for (DiyFormFields item : diyFormFields) {
                DiyFormFields newField = new DiyFormFields();
                BeanUtils.copyProperties(item, newField);
                newField.setFieldId(null); // 清空原有的 field_id，让数据库自动生成新的
                newField.setFormId(newFormId);
                newField.setWriteNum(0);
                newField.setCreateTime(currentTime);
                newField.setUpdateTime(currentTime);
                newFormFieldList.add(newField);
            }
            MybatisBatch<DiyFormFields> mybatisBatch = new MybatisBatch<>(sqlSessionFactory, newFormFieldList);
            MybatisBatch.Method<DiyFormFields> method = new MybatisBatch.Method<>(DiyFormFieldsMapper.class);
            mybatisBatch.execute(method.insert());
        }

        // 复制填写配置
        if (!ObjectUtil.isEmpty(diyFormWriteConfig)) {
            DiyFormWriteConfigParam newWriteConfig = new DiyFormWriteConfigParam();
            BeanUtils.copyProperties(diyFormWriteConfig, newWriteConfig);
            newWriteConfig.setFormId(newFormId);
            newWriteConfig.setId(null);
            coreDiyFormConfigService.addWriteConfig(newWriteConfig);
        }

        // 复制提交配置
        if (!ObjectUtil.isEmpty(diyFormSubmitConfig)) {
            DiyFormSubmitConfigParam newSubmitConfig = new DiyFormSubmitConfigParam();
            BeanUtils.copyProperties(diyFormSubmitConfig, newSubmitConfig);
            newSubmitConfig.setFormId(newFormId);
            newSubmitConfig.setId(null);
            coreDiyFormConfigService.addSubmitConfig(newSubmitConfig);
        }

        return newFormId;
    }

    @Override
    public JSONObject getTemplate(DiyFormTemplateParam params) {
        DiyFormTemplateEnum diyFormTemplateEnum = new DiyFormTemplateEnum();
        return diyFormTemplateEnum.getTemplate(params.getType(), params.getTemplateKey());
    }

    @Override
    public JSONObject getFormType() {
        DiyFormTypeEnum types = new DiyFormTypeEnum();
        return types.getType();
    }

    @Override
    public boolean modifyStatus(DiyFormStatusParam formStatusParam) {
        DiyForm diyForm = new DiyForm();
        diyForm.setStatus(formStatusParam.getStatus());
        diyFormMapper.update(diyForm, new QueryWrapper<DiyForm>().eq("form_id", formStatusParam.getFormId()).eq("site_id", RequestUtils.siteId()));
        return true;
    }

    @Override
    public PageResult<DiyFormRecordsListVo> getRecordPages(PageParam pageParam, DiyFormRecordsSearchParam searchParam) {
        searchParam.setSiteId(RequestUtils.siteId());
        return coreDiyFormRecordsService.page(pageParam, searchParam);
    }

    @Override
    public DiyFormRecordsInfoVo getRecordInfo(int recordId) {
        return coreDiyFormRecordsService.info(recordId);
    }

    @Override
    @Transactional
    public boolean delRecord(Integer formId, Integer recordId) {
        try {
            // 减少填写数量
            QueryWrapper<DiyForm> formQueryWrapper = new QueryWrapper<>();
            formQueryWrapper.eq("form_id", formId);
            DiyForm diyForm = diyFormMapper.selectOne(formQueryWrapper);
            if (diyForm != null) {
                diyForm.setWriteNum(diyForm.getWriteNum() - 1);
                diyFormMapper.updateById(diyForm);
            }

            // 删除记录
            QueryWrapper<DiyFormRecords> recordsQueryWrapper = new QueryWrapper<>();
            recordsQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("record_id", recordId);
            diyFormRecordsMapper.delete(recordsQueryWrapper);

            // 删除万能表单填写字段
            QueryWrapper<DiyFormRecordsFields> fieldsQueryWrapper = new QueryWrapper<>();
            fieldsQueryWrapper.eq("site_id", RequestUtils.siteId())
                    .eq("record_id", recordId);
            diyFormRecordsFieldsMapper.delete(fieldsQueryWrapper);

            return true;
        } catch (Exception e) {
            // 事务会自动回滚，因为使用了 @Transactional 注解
            throw new CommonException("删除记录失败: " + e.getMessage());
        }
    }

    @Override
    public List<DiyFormFieldsListVo> getFieldsList(DiyFormRecordsFieldsSearchParam diyFormRecordsFieldsSearchParam) {
        QueryWrapper<DiyFormFields> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("form_id", diyFormRecordsFieldsSearchParam.getFormId());
        if(diyFormRecordsFieldsSearchParam.getSort().equals("asc"))
        {
            queryWrapper.orderByAsc(diyFormRecordsFieldsSearchParam.getOrder());
        }else {
            queryWrapper.orderByDesc(diyFormRecordsFieldsSearchParam.getOrder());
        }
        List<DiyFormFields> list = diyFormFieldsMapper.selectList(queryWrapper);
        List<DiyFormFieldsListVo> listVo = new LinkedList<>();
        for (DiyFormFields item : list) {
            DiyFormFieldsListVo vo = new DiyFormFieldsListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public Map<String, Object> getQrcode(int formId) {
        String page = "app/pages/index/diy_form";
        Map<String, Object> dataItem = new HashMap<>();
        dataItem.put("form_id", formId);

        String dir = "upload/" + RequestUtils.siteId() + "/diy_form_qrcode";
        String path = "";

        try {
            path = QrcodeUtils.qrcodeToFile(RequestUtils.siteId(),"weapp", "", page, dataItem, dir);
        } catch (Exception e) {
            log.error("万能表单微信小程序二维码生成error: {}, 错误文件: {}, 错误行号: {}", e.getMessage(), e.getStackTrace()[0].getFileName(), e.getStackTrace()[0].getLineNumber());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        return result;
    }

    @Override
    public DiyFormSelectPageVo getSelectPage(PageParam pageParam, DiyFormSelectParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        // 验证表单ID集合
        List<Integer> verifyFormIds = new ArrayList<>();
        if (param.getVerifyFormIds() != null && !param.getVerifyFormIds().isEmpty()) {
            // 查询存在的表单ID
            List<DiyForm> existForms = diyFormMapper.selectList(
                    new QueryWrapper<DiyForm>()
                            .select("form_id")
                            .in("form_id", param.getVerifyFormIds())
            );

            verifyFormIds = existForms.stream()
                    .map(DiyForm::getFormId)
                    .collect(Collectors.toList());
        }

        // 构建查询条件
        LambdaQueryWrapper<DiyForm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DiyForm::getSiteId, RequestUtils.siteId())
                .eq(DiyForm::getStatus, 1)
                .orderByDesc(DiyForm::getFormId);

        // 添加搜索条件
        if (StringUtils.hasText(param.getTitle())) {
            queryWrapper.like(DiyForm::getTitle, param.getTitle());
        }
        if (StringUtils.hasText(param.getType())) {
            queryWrapper.eq(DiyForm::getType, param.getType());
        }
        if (StringUtils.hasText(param.getAddon())) {
            queryWrapper.eq(DiyForm::getAddon, param.getAddon());
        }

        // 分页查询
        IPage<DiyForm> formPage = diyFormMapper.selectPage(new Page<>(page, limit), queryWrapper);

        if (formPage.getTotal() == 0){
            return new DiyFormSelectPageVo();
        }
        List<DiyFormInfoVo> resultList = new ArrayList<>();
        formPage.getRecords().forEach(item -> {
            DiyFormInfoVo diyFormInfoVo = new DiyFormInfoVo();
            BeanUtils.copyProperties(item, diyFormInfoVo);
            JSONObject currentType = new DiyFormTypeEnum().getType(item.getType());
            String typeName = currentType.getStr("title");
            diyFormInfoVo.setTypeName(typeName);
            Addon addon = addonMapper.selectOne(new LambdaQueryWrapper<Addon>().eq(Addon::getKey, item.getAddon()));
            String addonName =ObjectUtil.isNotEmpty(addon) ? addon.getTitle() : "";
            diyFormInfoVo.setAddonName(addonName);
            resultList.add(diyFormInfoVo);
        });

        DiyFormSelectPageVo result = new DiyFormSelectPageVo();
        result.setData(resultList);
        result.setCurrentPage(page);
        result.setPerPage(limit);
        result.setTotal(formPage.getTotal());
        result.setVerifyFormIds(verifyFormIds);
        return result;
    }

    private void checkPageTitleUnique(String title, Integer formId) {
        QueryWrapper queryWrapper = new QueryWrapper<>()
            .eq("site_id", RequestUtils.siteId())
            .eq("page_title", title)
            .last("limit 1");
        if (formId != 0) queryWrapper.ne("form_id", formId);
        DiyForm form = diyFormMapper.selectOne(queryWrapper);
        if (form != null) {
            throw new CommonException("表单名称必须是唯一的");
        }
    }
}
