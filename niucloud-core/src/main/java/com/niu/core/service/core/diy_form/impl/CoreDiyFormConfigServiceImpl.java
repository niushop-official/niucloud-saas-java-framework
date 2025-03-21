package com.niu.core.service.core.diy_form.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.diy_form.DiyFormSubmitConfig;
import com.niu.core.entity.diy_form.DiyFormWriteConfig;
import com.niu.core.mapper.diy_form.DiyFormSubmitConfigMapper;
import com.niu.core.mapper.diy_form.DiyFormWriteConfigMapper;
import com.niu.core.service.core.diy_form.ICoreDiyFormConfigService;
import com.niu.core.service.core.diy_form.param.DiyFormSubmitConfigParam;
import com.niu.core.service.core.diy_form.param.DiyFormWriteConfigParam;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.diy_form.vo.DiyFormWriteConfigInfoVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class CoreDiyFormConfigServiceImpl implements ICoreDiyFormConfigService {

    @Resource
    DiyFormSubmitConfigMapper diyFormSubmitConfigMapper;
    @Resource
    DiyFormWriteConfigMapper diyFormWriteConfigMapper;

    @Override
    public DiyFormWriteConfigInfoVo getWriteConfig(Integer siteId, Integer formId) {

        QueryWrapper<DiyFormWriteConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId).eq("form_id", formId);
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(writeConfig)) {
            return null;
        }
        DiyFormWriteConfigInfoVo infoVo = new DiyFormWriteConfigInfoVo();
        BeanUtil.copyProperties(writeConfig, infoVo);
        infoVo.setTimeLimitRule(writeConfig.getTimeLimitRule());
        JSONObject timeLimitRule = JSONUtil.parseObj(infoVo.getTimeLimitRule());
        if(timeLimitRule.containsKey("specify_time") && timeLimitRule.getJSONArray("specify_time").size() > 0)
        {
            JSONArray specifyTime = timeLimitRule.getJSONArray("specify_time");
            JSONArray specifyTimeVo = new JSONArray();
            specifyTimeVo.add(DateUtils.timestampToString(specifyTime.getLong(0)));
            specifyTimeVo.add(DateUtils.timestampToString(specifyTime.getLong(1)));
            timeLimitRule.put("specify_time", specifyTimeVo);
        }

        if(timeLimitRule.containsKey("open_day_time") && timeLimitRule.getJSONArray("open_day_time").size() > 0)
        {
            Long beginOfDay = DateUtil.beginOfDay(DateUtil.date()).getTime() / 1000;
            JSONArray openDayTime = timeLimitRule.getJSONArray("open_day_time");
            JSONArray openDayTimeVo = new JSONArray();
            openDayTimeVo.add(DateUtils.timestampToString( (beginOfDay + openDayTime.getLong(0)), "yyyy-MM-dd"));
            openDayTimeVo.add(DateUtils.timestampToString((beginOfDay + openDayTime.getLong(1)), "yyyy-MM-dd"));
            timeLimitRule.put("specify_time", openDayTimeVo);
        }
        return infoVo;
    }

    @Override
    public void addWriteConfig(DiyFormWriteConfigParam addParam) {
        if (addParam.getFormId() == null) {
            throw new CommonException("缺少表单id");
        }
        DiyFormWriteConfig writeConfig = new DiyFormWriteConfig();

        BeanUtil.copyProperties(addParam, writeConfig);
        writeConfig.setCreateTime(System.currentTimeMillis() / 1000);
        writeConfig.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormWriteConfigMapper.insert(writeConfig);
    }

    @Override
    public void editWriteConfig(DiyFormWriteConfigParam editParam) {

        QueryWrapper<DiyFormWriteConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", editParam.getId()).eq("form_id", editParam.getFormId()).eq("site_id", editParam.getSiteId());
        DiyFormWriteConfig existingConfig = diyFormWriteConfigMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(existingConfig)) {
            throw new CommonException("表单填写配置不存在");
        }
        BeanUtil.copyProperties(editParam, existingConfig);
        existingConfig.setLabelIds(editParam.getLabelIds().toString());
        existingConfig.setFormWriteRule(editParam.getFormWriteRule().toString());
        existingConfig.setMemberWriteRule(editParam.getMemberWriteRule().toString());
        existingConfig.setLevelIds(editParam.getLevelIds().toString());


        JSONObject timeLimitRule = editParam.getTimeLimitRule();
        if(timeLimitRule.containsKey("specify_time") && timeLimitRule.getJSONArray("specify_time").size() > 0)
        {
            JSONArray specifyTime = timeLimitRule.getJSONArray("specify_time");
            JSONArray specifyTimeVo = new JSONArray();
            specifyTimeVo.add(DateUtils.StringToTimestamp(specifyTime.getStr(0)));
            specifyTimeVo.add(DateUtils.StringToTimestamp(specifyTime.getStr(1)));
            timeLimitRule.put("specify_time", specifyTimeVo);
        }

        if(timeLimitRule.containsKey("open_day_time") && timeLimitRule.getJSONArray("open_day_time").size() > 0)
        {
            Long beginOfDay = DateUtil.beginOfDay(DateUtil.date()).getTime() / 1000;
            JSONArray openDayTime = timeLimitRule.getJSONArray("open_day_time");
            JSONArray openDayTimeVo = new JSONArray();
            String startTimeStr = DateUtil.format(DateUtil.date(), "yyyy-MM-dd") + " " + openDayTime.getStr(0) + ":00";
            String endTimeStr = DateUtil.format(DateUtil.date(), "yyyy-MM-dd") + " " + openDayTime.getStr(1) + ":00";
            openDayTimeVo.add(DateUtils.StringToTimestamp(startTimeStr) - beginOfDay);
            openDayTimeVo.add(DateUtils.StringToTimestamp(endTimeStr) - beginOfDay);
            timeLimitRule.put("specify_time", openDayTimeVo);
        }
        existingConfig.setTimeLimitRule(timeLimitRule.toString());
        existingConfig.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormWriteConfigMapper.updateById(existingConfig);
    }

    @Override
    public DiyFormSubmitConfigInfoVo getSubmitConfig(Integer siteId, Integer formId) {

        QueryWrapper<DiyFormSubmitConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId).eq("form_id", formId).last("limit 1");
        DiyFormSubmitConfig submitConfig = diyFormSubmitConfigMapper.selectOne(queryWrapper);

        DiyFormSubmitConfigInfoVo infoVo = new DiyFormSubmitConfigInfoVo();
        BeanUtil.copyProperties(submitConfig, infoVo);
        return infoVo;
    }

    @Override
    public void addSubmitConfig(DiyFormSubmitConfigParam addParam) {
        if (addParam.getFormId() == null) {
            throw new CommonException("缺少表单id");
        }
        DiyFormSubmitConfig submitConfig = new DiyFormSubmitConfig();
        submitConfig.setSubmitAfterAction(addParam.getSubmitAfterAction());
        submitConfig.setSuccessAfterAction(addParam.getSuccessAfterAction().toString());
        submitConfig.setTimeLimitRule(addParam.getTimeLimitRule().toString());
        submitConfig.setTimeLimitType(addParam.getTimeLimitType());
        submitConfig.setSiteId(addParam.getSiteId());
        submitConfig.setVoucherContentRule(addParam.getVoucherContentRule().toString());
        submitConfig.setTipsText(addParam.getTipsText());
        submitConfig.setTipsType(addParam.getTipsType());
        submitConfig.setFormId(addParam.getFormId());
        submitConfig.setCreateTime(System.currentTimeMillis() / 1000);
        submitConfig.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormSubmitConfigMapper.insert(submitConfig);
    }

    @Override
    public void editSubmitConfig(DiyFormSubmitConfigParam editParam) {

        QueryWrapper<DiyFormSubmitConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", editParam.getId()).eq("site_id", editParam.getSiteId()).eq("form_id", editParam.getFormId());
        DiyFormSubmitConfig existingConfig = diyFormSubmitConfigMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(existingConfig)) {
            throw new CommonException("表单提交成功页配置不存在");
        }
        existingConfig.setSubmitAfterAction(editParam.getSubmitAfterAction());
        existingConfig.setSuccessAfterAction(editParam.getSuccessAfterAction().toString());
        existingConfig.setTimeLimitRule(editParam.getTimeLimitRule().toString());
        existingConfig.setTimeLimitType(editParam.getTimeLimitType());

        existingConfig.setVoucherContentRule(editParam.getVoucherContentRule().toString());
        existingConfig.setTipsText(editParam.getTipsText());
        existingConfig.setTipsType(editParam.getTipsType());


        existingConfig.setUpdateTime(System.currentTimeMillis() / 1000);
        diyFormSubmitConfigMapper.updateById(existingConfig);
    }
}