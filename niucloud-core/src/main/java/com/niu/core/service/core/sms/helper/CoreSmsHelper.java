package com.niu.core.service.core.sms.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.loader.factory.LoaderProviderFactory;
import com.niu.core.common.component.sms.factory.SmsProviderFactory;
import com.niu.core.common.component.sms.model.SmsSend;
import com.niu.core.entity.sys.SysNoticeSmsLog;
import com.niu.core.service.core.notice.ICoreNoticeSmsLogService;
import com.niu.core.service.core.sys.ICoreConfigService;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送短信-->助手类
 */
public class CoreSmsHelper {

    /**
     * 根据站点<系统配置表>发送短信
     *
     * @param siteId
     * @param dataMap
     */
    public static Object send(Integer siteId, String smsKey, Map<String, Object> dataMap) {
        ICoreNoticeSmsLogService coreNoticeSmsLogService = SpringContext.bean(ICoreNoticeSmsLogService.class);
        JSONObject jsonObject = getSmsConfig(siteId);
        // 默认使用的短信名称
        String smsName = jsonObject.getStr("default");
        // 根据名称获取配置
        JSONObject configObject = jsonObject.getJSONObject(smsName);
        // 发送前添加日志
        SysNoticeSmsLog sysNoticeSmsLog = new SysNoticeSmsLog();
        Object result = null;
        // 使用系统组件进行发送
        try {
            // 短信手机号
            String mobile = (String) dataMap.get("mobile");
            // 短信内容
            String content = getSmsContentConfig(smsKey);
            if (StrUtil.isEmpty(content)) {
                content = (String) dataMap.get("sms_content");
            }
            String templateId = (String) dataMap.get("templateId");
            String code = (String) dataMap.get("code");
            // 发送实体类
            SmsSend smsSend = new SmsSend();
            smsSend.setMobile(mobile);
            smsSend.setTemplateId(templateId);
            smsSend.setKeyword(code);
            smsSend.setMessage(content);
            // 组装日志实体类
            sysNoticeSmsLog.setSiteId(siteId);
            sysNoticeSmsLog.setMobile(mobile);
            sysNoticeSmsLog.setSmsType(smsName);
            sysNoticeSmsLog.setKey(smsKey);
            sysNoticeSmsLog.setTemplateId(templateId);
            sysNoticeSmsLog.setContent(content);
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("code", code);
            sysNoticeSmsLog.setParams(JSONUtil.toJsonStr(paramsMap));
            sysNoticeSmsLog.setCreateTime(DateUtils.currTime());
            sysNoticeSmsLog.setSendTime(DateUtils.currTime());
            // 发送操作
            result = SmsProviderFactory.get(smsName).send(configObject, smsSend);
            sysNoticeSmsLog.setResult("");
        } catch (Exception e) {
            sysNoticeSmsLog.setResult("");
            throw e;
        } finally {
            coreNoticeSmsLogService.add(sysNoticeSmsLog);
        }
        return result;
    }

    /**
     * 获取短信的配置内容
     *
     * @param smsKey
     * @return
     */
    public static String getSmsContentConfig(String smsKey) {
        JSONObject smsJsonObject = LoaderProviderFactory.get().mergeJSONObject("sms.json");
        if (smsJsonObject != null) {
            JSONObject contentJsonObject = smsJsonObject.getJSONObject(smsKey);
            if (contentJsonObject != null) {
                return contentJsonObject.getStr("content");
            }
        }
        return "";
    }

    /**
     * 获取站点的短信配置
     *
     * @return
     */
    public static JSONObject getSmsConfig(Integer siteId) {
        ICoreConfigService coreConfigService = SpringContext.bean(ICoreConfigService.class);
        JSONObject smsObject = coreConfigService.getConfigValue(siteId, "SMS");
        return smsObject;
    }

}
