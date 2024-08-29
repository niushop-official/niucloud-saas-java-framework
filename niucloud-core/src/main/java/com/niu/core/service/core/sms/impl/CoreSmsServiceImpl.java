package com.niu.core.service.core.sms.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.sms.BaseSms;
import com.niu.core.common.loader.sms.SendResultVo;
import com.niu.core.common.loader.sms.SmsLoader;
import com.niu.core.entity.sys.SysNoticeSmsLog;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.mapper.sys.SysNoticeSmsLogMapper;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.niu.core.service.core.sms.ICoreSmsService;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreSmsServiceImpl implements ICoreSmsService {

    @Resource
    ICoreConfigService coreConfigService;

    @Resource
    SysNoticeSmsLogMapper sysNoticeSmsLogMapper;

    /**
     * 发送短信
     * @param siteId
     * @param notice
     * @param noticeData
     */
    public void send(Integer siteId, NoticeInfoVo notice, NoticeDataVo noticeData) {
        JSONObject config = this.getDefaultSmsConfig(siteId);

        SysNoticeSmsLog model = new SysNoticeSmsLog();
        model.setSiteId(siteId);
        model.setMobile(noticeData.getMobile());
        model.setSmsType(config.getStr("sms_type"));
        model.setKey(notice.getKey());
        model.setContent(notice.getSms().getStr("content", ""));
        model.setStatus(SmsStatusEnum.SENDING.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setParams(JSONUtil.parse(noticeData.getVars()).toString());
        sysNoticeSmsLogMapper.insert(model);

        BaseSms smsDriver = SmsLoader.getDriver(config.getStr("sms_type"), config);
        SendResultVo result = smsDriver.send(notice, noticeData);

        SysNoticeSmsLog updateModel = new SysNoticeSmsLog();
        updateModel.setId(model.getId());
        updateModel.setStatus(result.getStatus().getStatus());
        updateModel.setResult(ObjectUtil.defaultIfNull(result.getFailReason(), ""));
        sysNoticeSmsLogMapper.updateById(updateModel);

        if (result.getStatus().equals(SmsStatusEnum.FAIL.getStatus())) {
            throw new CommonException(updateModel.getResult());
        }
    }

    private JSONObject getDefaultSmsConfig(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "SMS");
        if (config.isEmpty()) throw new CommonException("短信未启用");

        String type = config.getStr("default", "");
        if (type.isEmpty()) throw new CommonException("短信未启用");

        config = config.getJSONObject(type);
        config.set("sms_type", type);

        return config;
    }
}
