package com.niu.core.service.api.diy.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.diy_form.*;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.diy_form.DiyFormTypeEnum;
import com.niu.core.mapper.diy_form.*;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.member.vo.MemberConfigVo;
import com.niu.core.service.api.diy.IDiyFormService;
import com.niu.core.service.api.diy.param.DiyFormParam;
import com.niu.core.service.api.diy.vo.*;
import com.niu.core.service.core.diy_form.ICoreDiyFormConfigService;
import com.niu.core.service.core.diy_form.ICoreDiyFormRecordsService;
import com.niu.core.service.core.diy_form.param.DiyFormRecordsParam;
import com.niu.core.service.core.diy_form.vo.DiyFormSubmitConfigInfoVo;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiyFormServiceImpl implements IDiyFormService {

    @Resource
    DiyFormMapper diyFormMapper;

    @Resource
    DiyFormFieldsMapper diyFormFieldsMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    MemberLabelMapper memberLabelMapper;

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
    private ICoreMemberConfigService coreMemberConfigService;

    @Override
    public DiyFormInfoVo getInfo(Integer formId) {
        // 查询 DiyFormWriteConfig
        QueryWrapper<DiyFormWriteConfig> writeConfigWrapper = new QueryWrapper<>();
        writeConfigWrapper.eq("form_id", formId)
                .eq("site_id", RequestUtils.siteId());
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(writeConfigWrapper);

        // 查询表单信息
        QueryWrapper<DiyForm> formWrapper = new QueryWrapper<>();
        formWrapper.eq("form_id", formId)
                .eq("status", 1)
                .eq("site_id", RequestUtils.siteId());
        DiyForm formInfo = diyFormMapper.selectOne(formWrapper);

        List<Map<String, String>> error = new ArrayList<>();
        DiyFormInfoVo info = new DiyFormInfoVo();
        if (formInfo != null) {
            BeanUtil.copyProperties(formInfo, info);
            // 过滤隐藏的组件
            JSONObject valueObj = JSONUtil.parseObj(formInfo.getValue());
            if (valueObj.containsKey("value")) {
                JSONArray valueArray = valueObj.getJSONArray("value");
                if (valueArray != null) {
                    JSONArray newArray = new JSONArray();
                    for (Object obj : valueArray) {
                        JSONObject item = (JSONObject) obj;
                        if (!item.getBool("isHidden", false)) {
                            newArray.add(item);
                        }
                    }
                    valueObj.put("value", newArray);
                    info.setValue(valueObj.toString());
                }
            }


            // 检查会员相关限制
            Integer memberId = RequestUtils.memberId();
            if (writeConfig != null && memberId != null) {
                Map<String, String> errorMsg;
                errorMsg = checkMemberCanJoinOrNot(memberId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkFormWriteTime(writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkFormWriteLimitNum(formId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
                errorMsg = checkMemberWriteLimitNum(memberId, formId, writeConfig);
                if (ObjectUtil.isNotEmpty(errorMsg)) {
                    error.add(errorMsg);
                }
            }
        } else {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("title", "当前表单无法查看");
            errorMap.put("type", "表单状态");
            errorMap.put("desc", "该表单已关闭");
            error.add(errorMap);
        }
        info.setError(error);
        return info;
    }

    @Override
    public Map<String, String> checkMemberCanJoinOrNot(Integer memberId, DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        // 查询会员信息
        QueryWrapper<Member> memberWrapper = new QueryWrapper<>();
        memberWrapper.eq("member_id", memberId)
                .eq("site_id", RequestUtils.siteId());
        Member memberInfo = memberMapper.selectOne(memberWrapper);
        if (memberInfo == null) {
            throw new ApiException("MEMBER_NOT_EXIST");
        }

        String joinMemberType = diyFormWriteConfig.getJoinMemberType();
        switch (joinMemberType) {
            case "all_member":
                break;
            case "selected_member_level":
                List<Integer> levelIds = JSONUtil.toList(JSONUtil.parseArray(diyFormWriteConfig.getLevelIds()), Integer.class);
                if (!levelIds.contains(memberInfo.getMemberLevel())) {
                    QueryWrapper<MemberLevel> levelWrapper = new QueryWrapper<>();
                    levelWrapper.in("level_id", levelIds)
                            .eq("site_id", RequestUtils.siteId());
                    List<String> levelNames = memberLevelMapper.selectObjs(levelWrapper.select("level_name")).stream()
                            .map(Object::toString)
                            .toList();
                    error.put("title", "当前表单无法查看");
                    error.put("type", "允许填写用户");
                    error.put("desc", "该表单已设置仅限“" + String.join("、", levelNames) + "等级”的用户填写");
                }
                break;
            case "selected_member_label":
                JSONArray memberLabels = JSONUtil.parseArray(memberInfo.getMemberLabel());
                if (memberLabels == null) {
                    memberLabels = new JSONArray();
                }
                List<Integer> labelIds = JSONUtil.toList(JSONUtil.parseArray(diyFormWriteConfig.getLabelIds()), Integer.class);
                boolean hasIntersection = false;
                for (Integer labelId : labelIds) {
                    if (memberLabels.contains(labelId)) {
                        hasIntersection = true;
                        break;
                    }
                }
                if (!hasIntersection) {
                    QueryWrapper<MemberLabel> labelWrapper = new QueryWrapper<>();
                    labelWrapper.in("label_id", labelIds)
                            .eq("site_id", RequestUtils.siteId());
                    List<String> labelNames = memberLabelMapper.selectObjs(labelWrapper.select("label_name")).stream()
                            .map(Object::toString)
                            .toList();
                    error.put("title", "当前表单无法查看");
                    error.put("type", "允许填写用户");
                    error.put("desc", "该表单已设置仅限“" + String.join("、", labelNames) + "标签”的用户填写");
                }
                break;
            default:
                throw new CommonException("未知的会员类型: " + joinMemberType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkMemberWriteLimitNum(Integer memberId, Integer formId, DiyFormWriteConfig diyFormWriteConfig) {

        Map<String, String> error = new HashMap<>();
        String memberWriteType = diyFormWriteConfig.getMemberWriteType();

        switch (memberWriteType) {
            case "no_limit":
                break;
            case "diy":
                JSONObject memberWriteRule = JSONUtil.parseObj(diyFormWriteConfig.getMemberWriteRule());
                String timeUnit = memberWriteRule.getStr("time_unit");
                String timeText;
                switch (timeUnit) {
                    case "day":
                        timeText = "天";
                        break;
                    case "week":
                        timeText = "周";
                        break;
                    case "month":
                        timeText = "月";
                        break;
                    case "year":
                        timeText = "年";
                        break;
                    default:
                        throw new CommonException("未知的时间单位: " + timeUnit);
                }

                int timeValue = (int) memberWriteRule.get("time_value");
                int numLimit = (int) memberWriteRule.get("num");

                LocalDateTime startTime = LocalDateTime.now();
                switch (timeUnit) {
                    case "day":
                        startTime = startTime.minus(timeValue, ChronoUnit.DAYS);
                        break;
                    case "week":
                        startTime = startTime.minus(timeValue, ChronoUnit.WEEKS);
                        break;
                    case "month":
                        startTime = startTime.minus(timeValue, ChronoUnit.MONTHS);
                        break;
                    case "year":
                        startTime = startTime.minus(timeValue, ChronoUnit.YEARS);
                        break;
                }

                QueryWrapper<DiyFormRecords> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("form_id", formId)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("member_id", memberId)
                        .ge("create_time", startTime);
                Long count = diyFormRecordsMapper.selectCount(queryWrapper);

                if (count >= numLimit) {
                    error.put("title", "您的填写次数已达上限");
                    error.put("type", "允许填写次数(每人)");
                    error.put("desc", "该表单已设置“每人每" + timeValue + timeText + "可填写" + numLimit + "次”");
                }
                break;
            default:
                throw new CommonException("未知的会员填写类型: " + memberWriteType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkFormWriteLimitNum(Integer formId, DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        String formWriteType = diyFormWriteConfig.getFormWriteType();

        switch (formWriteType) {
            case "no_limit":
                break;
            case "diy":
                JSONObject formWriteRule = JSONUtil.parseObj(diyFormWriteConfig.getFormWriteRule());
                String timeUnit = formWriteRule.getStr("time_unit");
                String timeText;
                switch (timeUnit) {
                    case "day":
                        timeText = "天";
                        break;
                    case "week":
                        timeText = "周";
                        break;
                    case "month":
                        timeText = "月";
                        break;
                    case "year":
                        timeText = "年";
                        break;
                    default:
                        throw new CommonException("未知的时间单位: " + timeUnit);
                }

                int timeValue = (int) formWriteRule.get("time_value");
                int numLimit = (int) formWriteRule.get("num");

                LocalDateTime startTime = LocalDateTime.now();
                switch (timeUnit) {
                    case "day":
                        startTime = startTime.minus(timeValue, ChronoUnit.DAYS);
                        break;
                    case "week":
                        startTime = startTime.minus(timeValue, ChronoUnit.WEEKS);
                        break;
                    case "month":
                        startTime = startTime.minus(timeValue, ChronoUnit.MONTHS);
                        break;
                    case "year":
                        startTime = startTime.minus(timeValue, ChronoUnit.YEARS);
                        break;
                }

                QueryWrapper<DiyFormRecords> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("form_id", formId)
                        .eq("site_id", RequestUtils.siteId())
                        .ge("create_time", startTime);
                Long count = diyFormRecordsMapper.selectCount(queryWrapper);

                if (count >= numLimit) {
                    error.put("title", "表单总填写次数已达上限");
                    error.put("type", "允许填写次数(总)");
                    error.put("desc", "该表单已设置“每" + timeValue + timeText + "可填写" + numLimit + "次”");
                }
                break;
            default:
                throw new CommonException("未知的表单填写类型: " + formWriteType);
        }
        return error;
    }

    @Override
    public Map<String, String> checkFormWriteTime(DiyFormWriteConfig diyFormWriteConfig) {
        Map<String, String> error = new HashMap<>();
        String timeLimitType = diyFormWriteConfig.getTimeLimitType();

        switch (timeLimitType) {
            case "no_limit":
                break;
            case "specify_time":
                JSONObject timeLimitRule = JSONUtil.parseObj(diyFormWriteConfig.getTimeLimitRule());
                JSONArray specifyTime = timeLimitRule.getJSONArray("specify_time");
                if (specifyTime != null && specifyTime.size() == 2) {
                    long currentTime = DateUtils.currTime();
                    if (currentTime < specifyTime.getLong(0) || currentTime > specifyTime.getLong(1)) {
                        String start = DateUtils.timestampToString(specifyTime.getLong(0));
                        String end = DateUtils.timestampToString(specifyTime.getLong(1));
                        error.put("title", "当前时间无法查看");
                        error.put("type", "允许查看时间");
                        error.put("desc", "该表单已设置“" + start + "-" + end + "”可查看");
                    }
                }
                break;
            case "open_day_time":
                JSONObject timeLimitRuleOpenDay = JSONUtil.parseObj(diyFormWriteConfig.getTimeLimitRule());
                JSONArray openDayTime = timeLimitRuleOpenDay.getJSONArray("open_day_time");
                if (openDayTime != null && openDayTime.size() == 2) {
                    LocalDate today = LocalDate.now();
                    LocalDateTime startTime = today.atStartOfDay().plusSeconds(openDayTime.getLong(0));
                    LocalDateTime endTime = today.atStartOfDay().plusSeconds(openDayTime.getLong(1));
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isBefore(startTime) || now.isAfter(endTime)) {
                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                        String startStr = startTime.format(timeFormatter);
                        String endStr = endTime.format(timeFormatter);
                        error.put("title", "当前时间无法查看");
                        error.put("type", "允许查看时间");
                        error.put("desc", "该表单已设置“每天" + startStr + "-" + endStr + "”可查看");
                    }
                }
                break;
            default:
                throw new CommonException("未知的时间限制类型: " + timeLimitType);
        }
        return error;
    }

    @Override
    public Integer addRecord(DiyFormRecordsParam diyFormRecordsParam) {
        diyFormRecordsParam.setSiteId(RequestUtils.siteId());
        diyFormRecordsParam.setMemberId(RequestUtils.memberId());

        // 检查表单是否存在且已开启
        QueryWrapper<DiyForm> formQueryWrapper = new QueryWrapper<>();
        formQueryWrapper.eq("form_id", diyFormRecordsParam.getFormId())
                .eq("site_id", RequestUtils.siteId());
        DiyForm formInfo = diyFormMapper.selectOne(formQueryWrapper);
        if (formInfo == null) {
            throw new ApiException("表单不存在");
        }
        if (formInfo.getStatus() == 0) {
            throw new ApiException("该表单已关闭");
        }

        // 查询表单填写配置
        QueryWrapper<DiyFormWriteConfig> writeConfigQueryWrapper = new QueryWrapper<>();
        writeConfigQueryWrapper.eq("form_id", diyFormRecordsParam.getFormId())
                .eq("site_id", RequestUtils.siteId());
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(writeConfigQueryWrapper);

        if (writeConfig != null) {
            // 检查会员是否可以参与
            Map<String, String> canJoinError = checkMemberCanJoinOrNot(RequestUtils.memberId(), writeConfig);
            if (!canJoinError.isEmpty()) {
                throw new ApiException(canJoinError.get("desc"));
            }

            // 检查表单填写时间
            Map<String, String> timeError = checkFormWriteTime(writeConfig);
            if (!timeError.isEmpty()) {
                throw new ApiException(timeError.get("desc"));
            }

            // 检查表单总填写次数限制
            Map<String, String> formLimitError = checkFormWriteLimitNum(diyFormRecordsParam.getFormId(), writeConfig);
            if (!formLimitError.isEmpty()) {
                throw new ApiException(formLimitError.get("desc"));
            }

            // 检查会员填写次数限制
            Map<String, String> memberLimitError = checkMemberWriteLimitNum(RequestUtils.memberId(), diyFormRecordsParam.getFormId(), writeConfig);
            if (!memberLimitError.isEmpty()) {
                throw new ApiException(memberLimitError.get("desc"));
            }
        }

        // 调用核心服务添加记录
        return coreDiyFormRecordsService.add(diyFormRecordsParam);
    }

    @Override
    public DiyFormRecordsInfoVo getResult(Integer recordId) {
        DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(new QueryWrapper<DiyFormRecords>().eq("record_id", recordId).eq("site_id", RequestUtils.siteId()).eq("member_id", RequestUtils.memberId()));
        if (ObjectUtil.isEmpty(diyFormRecords)) throw new ApiException("表单记录不存在");
        DiyFormRecordsInfoVo vo = new DiyFormRecordsInfoVo();
        BeanUtil.copyProperties(diyFormRecords, vo);
        DiyFormSubmitConfig diyFormSubmitConfig = diyFormSubmitConfigMapper.selectOne(new QueryWrapper<DiyFormSubmitConfig>().eq("form_id", vo.getFormId()));
        DiyFormSubmitConfigInfoVo configInfoVo = new DiyFormSubmitConfigInfoVo();
        if (ObjectUtil.isNotEmpty(diyFormSubmitConfig)) {
            BeanUtil.copyProperties(diyFormSubmitConfig, configInfoVo);
        }
        vo.setDiyFormSubmitConfig(configInfoVo);
        return vo;
    }

    @Override
    public DiyFormRecordsDetailVo getFormRecordInfo(Integer recordId) {
        DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(new QueryWrapper<DiyFormRecords>().eq("record_id", recordId).eq("site_id", RequestUtils.siteId()).eq("member_id", RequestUtils.memberId()));
        DiyFormRecordsDetailVo vo = new DiyFormRecordsDetailVo();
        if (ObjectUtil.isEmpty(diyFormRecords)) {
            return vo;
        }
        BeanUtil.copyProperties(diyFormRecords, vo);
        List<DiyFormRecordsFields> list = diyFormRecordsFieldsMapper.selectList(new QueryWrapper<DiyFormRecordsFields>().eq("record_id", vo.getRecordId()));
        if (ObjectUtil.isNotEmpty(list)) {
            List<DiyFormRecordsFieldsListVo> volist = new ArrayList<>();
            for (DiyFormRecordsFields item : list) {
                DiyFormRecordsFieldsListVo diyFormRecordsFieldsListVo = new DiyFormRecordsFieldsListVo();
                BeanUtils.copyProperties(item, diyFormRecordsFieldsListVo);
                volist.add(diyFormRecordsFieldsListVo);
            }
            vo.setRecordsFieldList(volist);
        }
        return vo;
    }

    @Override
    public Object getMemberInfoRecord() {
        MemberConfigVo memberConfig = coreMemberConfigService.getMemberConfig(RequestUtils.siteId());
        if (ObjectUtil.isEmpty(memberConfig.getFormId())) {
            return List.of();
        }
        Integer formId = memberConfig.getFormId();
        MPJQueryWrapper<DiyFormRecords> mpjqw = new MPJQueryWrapper<>();
        mpjqw.setAlias("fr").leftJoin("?_diy_form_records_fields frf on frf.record_id = fr.record_id".replace("?_", GlobalConfig.tablePrefix));
        mpjqw.select("frf.form_id, frf.form_field_id, frf.field_key, frf.field_type, frf.field_name, frf.field_value, frf.field_required, frf.field_unique, frf.privacy_protection");
        mpjqw.eq("fr.member_id", RequestUtils.memberId())
                .eq("fr.form_id", formId)
                .orderByDesc("fr.create_time");
        List<DiyFormRecordsFields> diyFormRecordsFields = diyFormRecordsMapper.selectJoinList(DiyFormRecordsFields.class, mpjqw);

        MPJQueryWrapper<DiyForm> diyFormMpjqw = new MPJQueryWrapper<>();
        diyFormMpjqw.setAlias("df")
                .leftJoin("?_diy_form_fields dfi on dfi.form_id = df.form_id".replace("?_", GlobalConfig.tablePrefix));
        diyFormMpjqw.select("df.form_id,df.type, dfi.field_id, dfi.field_key, dfi.field_type, dfi.field_name, dfi.field_required, dfi.field_hidden, dfi.field_unique, dfi.privacy_protection");
        diyFormMpjqw.eq("df.form_id", formId).eq("df.status", 1);
        List<DiyFormFields> diyFormFields = diyFormMapper.selectJoinList(DiyFormFields.class, diyFormMpjqw);


        if (ObjectUtil.isEmpty(diyFormFields)) {
            return List.of();
        }
        if (ObjectUtil.isEmpty(diyFormRecordsFields)) {
            return setResult(diyFormFields);
        }

        DiyMemberRecordVo result = setResult(diyFormFields);
        Map<String, DiyFormRecordsFields> fieldMap = diyFormRecordsFields.stream().collect(Collectors.toMap(DiyFormRecordsFields::getFieldKey, field -> field));
        for (DiyFormRecordsFields field : result.getFormField()) {
            if (fieldMap.containsKey(field.getFieldKey())) {
                field.setFieldValue(fieldMap.get(field.getFieldKey()).getFieldValue() == null ? "" : fieldMap.get(field.getFieldKey()).getFieldValue());
            }
        }
        return result;
    }

    @Override
    public void editRecord(DiyFormRecordsParam param) {
        DiyFormRecords diyFormRecords = diyFormRecordsMapper.selectOne(
            new QueryWrapper<DiyFormRecords>()
            .eq("record_id", param.getRecordId())
            .eq("site_id", RequestUtils.siteId())
            .eq("member_id", RequestUtils.memberId())
        );
        if (ObjectUtil.isEmpty(diyFormRecords)) {
            throw new ApiException("表单记录不存在");
        }
        DiyForm diyForm = diyFormMapper.selectOne(new LambdaQueryWrapper<DiyForm>().eq(DiyForm::getFormId, param.getFormId()));
        if (ObjectUtil.isEmpty(diyForm)) {
            throw new ApiException("表单不存在");
        }
        if (diyForm.getStatus() == 0) {
            throw new ApiException("该表单已关闭");
        }
        // 查询表单填写配置
        QueryWrapper<DiyFormWriteConfig> writeConfigQueryWrapper = new QueryWrapper<>();
        writeConfigQueryWrapper.eq("form_id", param.getFormId())
                .eq("site_id", RequestUtils.siteId());
        DiyFormWriteConfig writeConfig = diyFormWriteConfigMapper.selectOne(writeConfigQueryWrapper);

        if (writeConfig != null) {
            // 检查会员是否可以参与
            Map<String, String> canJoinError = checkMemberCanJoinOrNot(RequestUtils.memberId(), writeConfig);
            if (!canJoinError.isEmpty()) {
                throw new ApiException(canJoinError.get("desc"));
            }

            // 检查表单填写时间
            Map<String, String> timeError = checkFormWriteTime(writeConfig);
            if (!timeError.isEmpty()) {
                throw new ApiException(timeError.get("desc"));
            }

            // 检查表单总填写次数限制
            Map<String, String> formLimitError = checkFormWriteLimitNum(param.getFormId(), writeConfig);
            if (!formLimitError.isEmpty()) {
                throw new ApiException(formLimitError.get("desc"));
            }

            // 检查会员填写次数限制
            Map<String, String> memberLimitError = checkMemberWriteLimitNum(RequestUtils.memberId(), param.getFormId(), writeConfig);
            if (!memberLimitError.isEmpty()) {
                throw new ApiException(memberLimitError.get("desc"));
            }
        }
        coreDiyFormRecordsService.edit(param);
    }

    private DiyMemberRecordVo setResult(List<DiyFormFields> diyFormFields) {
        DiyMemberRecordVo vo = new DiyMemberRecordVo();
        DiyFormFields fieIds = diyFormFields.get(0);
        vo.setFormId(fieIds.getFormId());
        DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
        JSONObject type = diyFormTypeEnum.getType(fieIds.getType());
        vo.setTypeName(type != null ? type.getStr("title") : "");
        vo.setType(fieIds.getType());
        List<DiyFormRecordsFields> diyFormRecordsFields = new ArrayList<>();
        for (DiyFormFields diyFormField : diyFormFields) {
            DiyFormRecordsFields diyFormRecordsFields1 = new DiyFormRecordsFields();
            BeanUtils.copyProperties(diyFormField, diyFormRecordsFields1);
            diyFormRecordsFields1.setFormFieldId(diyFormField.getFieldId());
            diyFormRecordsFields1.setFieldValue(diyFormField.getFieldDefault() == null ? "" : diyFormField.getFieldDefault());
            diyFormRecordsFields.add(diyFormRecordsFields1);
        }
        vo.setFormField(diyFormRecordsFields);
        return vo;
    }
}
