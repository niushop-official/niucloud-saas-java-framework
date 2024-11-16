package com.niu.core.service.core.notice.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.component.loader.factory.LoaderProviderFactory;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.entity.sys.SysNoticeLog;
import com.niu.core.service.core.notice.ICoreNoticeLogService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.sms.helper.CoreSmsHelper;
import com.niu.core.service.core.weapp.helper.CoreWeappHelper;
import com.niu.core.service.core.wechat.helper.CoreWechatHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统发送通知<短信、微信公众号、微信小程序>-->助手类
 */
public class CoreNoticeHelper {

    /**
     * 发送通知
     *
     * @param siteId    ----> 站点ID
     * @param noticeKey ----> 通知的Key值
     * @param dataMap   ---->  发送的数据
     */
    public static <V> Map<String, V> send(Integer siteId, String noticeKey, Map<String, Object> dataMap) {
        Map<String, V> resultDataMap = new HashMap<>();
        // 参数判空
        if (siteId == null) {
            throw new RuntimeException("站点ID为空.");
        }
        if (StrUtil.isEmpty(noticeKey)) {
            throw new RuntimeException("通知业务key为空.");
        }
        // 获取取站点配置信息
        ICoreNoticeService coreNoticeService = SpringContext.bean(ICoreNoticeService.class);
        SysNotice sysNotice = coreNoticeService.find(siteId, noticeKey);
        if (sysNotice == null) {
            throw new RuntimeException("站点配置信息为空.");
        }
        // 加载通知配置信息
        JSONObject configJson = LoaderProviderFactory.get().mergeJSONObject("notice/notice.json");
        if (configJson == null) {
            throw new RuntimeException("站点配置信息为空.");
        }
        JSONObject noticeJson = configJson.getJSONObject(noticeKey);
        if (noticeJson == null) {
            throw new RuntimeException("站点配置信息为空.");
        }
        String key = noticeJson.getStr("key");
        boolean async = noticeJson.getBool("async", false);
        String name = noticeJson.getStr("name");
        String title = noticeJson.getStr("title");
        String receiverType = noticeJson.getStr("receiver_type");
        String code = noticeJson.getJSONObject("variable").getStr("code");
        // 记录日志
        ICoreNoticeLogService coreNoticeLogService = SpringContext.bean(ICoreNoticeLogService.class);
        // 发送通知，记录日志
        SysNoticeLog sysNoticeLog = new SysNoticeLog();
        try {
            sysNoticeLog.setSiteId(siteId);
            sysNoticeLog.setKey(noticeKey);
            // 短信消息
            if (sysNotice.getIsSms() == 1) {
                buildSysNoticeLogForSms(siteId, sysNotice, noticeJson, sysNoticeLog, dataMap);
                // 发送参数
                Map<String, Object> smsDataMap = new HashMap<>();
                smsDataMap.put("mobile", dataMap.get("mobile"));
                smsDataMap.put("code", dataMap.get("code"));
                smsDataMap.put("templateId", sysNotice.getSmsId());
                smsDataMap.put("sms_content", sysNotice.getSmsContent());
                if (async) {
                    ThreadPoolManager.runTask(() -> {
                        CoreSmsHelper.send(siteId, key, smsDataMap);
                    });
                } else {
                    Object result = CoreSmsHelper.send(siteId, key, smsDataMap);
                    resultDataMap.put("sms", (V) result);
                }
            }
            // 微信消息
            if (sysNotice.getIsWeapp() == 1) {
                buildSysNoticeLogForWeapp(siteId, sysNotice, noticeJson, sysNoticeLog, dataMap);
                Map<String, Object> weappDataMap = new HashMap<>();
                if (async) {
                    ThreadPoolManager.runTask(() -> {
                        CoreWeappHelper.send(siteId, key, weappDataMap);
                    });
                } else {
                    Object result = CoreWeappHelper.send(siteId, key, weappDataMap);
                    resultDataMap.put("weapp", (V) result);
                }
            }
            // 微信小程序消息
            if (sysNotice.getIsWechat() == 1) {
                buildSysNoticeLogForWechat(siteId, sysNotice, noticeJson, sysNoticeLog, dataMap);
                Map<String, Object> weappDataMap = new HashMap<>();
                if (async) {
                    ThreadPoolManager.runTask(() -> {
                        CoreWechatHelper.send(siteId, key, weappDataMap);
                    });
                } else {
                    Object result = CoreWechatHelper.send(siteId, key, weappDataMap);
                    resultDataMap.put("wechat", (V) result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 发送失败
            throw e;
        } finally {
            // 记录日志
            coreNoticeLogService.add(sysNoticeLog);
            return resultDataMap;
        }
    }

    /**
     * @param siteId
     * @param sysNotice
     * @param noticeJson
     * @param sysNoticeLog
     * @param dataMap
     */
    static void buildSysNoticeLogForWechat(Integer siteId, SysNotice sysNotice, JSONObject noticeJson, SysNoticeLog sysNoticeLog, Map<String, Object> dataMap) {
        sysNoticeLog.setNoticeType("wechat");
        // uid
        Integer uid = (Integer) dataMap.get("uid");
        if (uid != null) {
            sysNoticeLog.setUid(uid);
        }
        // 会员信息
        Integer memberId = (Integer) dataMap.get("memberId");
        if (memberId != null) {
            sysNoticeLog.setMemberId(memberId);
        }
        // 昵称
        String nickname = (String) dataMap.get("nickname");
        if (StrUtil.isNotEmpty(nickname)) {
            sysNoticeLog.setNickname(nickname);
        }
    }

    /**
     * @param siteId
     * @param sysNotice
     * @param noticeJson
     * @param sysNoticeLog
     * @param dataMap
     */
    static void buildSysNoticeLogForWeapp(Integer siteId, SysNotice sysNotice, JSONObject noticeJson, SysNoticeLog sysNoticeLog, Map<String, Object> dataMap) {
        sysNoticeLog.setNoticeType("weapp");
        // uid
        Integer uid = (Integer) dataMap.get("uid");
        if (uid != null) {
            sysNoticeLog.setUid(uid);
        }
        // 会员信息
        Integer memberId = (Integer) dataMap.get("memberId");
        if (memberId != null) {
            sysNoticeLog.setMemberId(memberId);
        }
        // 昵称
        String nickname = (String) dataMap.get("nickname");
        if (StrUtil.isNotEmpty(nickname)) {
            sysNoticeLog.setNickname(nickname);
        }
    }

    /**
     * @param siteId       站点ID
     * @param sysNotice    系统通知实体
     * @param noticeJson   通知JSON配置
     * @param sysNoticeLog 通知记录日志实体
     * @param dataMap      发送参数
     */
    static SysNoticeLog buildSysNoticeLogForSms(Integer siteId, SysNotice sysNotice, JSONObject noticeJson, SysNoticeLog sysNoticeLog, Map<String, Object> dataMap) {
        sysNoticeLog.setNoticeType("sms");
        // uid
        Integer uid = (Integer) dataMap.get("uid");
        if (uid != null) {
            sysNoticeLog.setUid(uid);
        }
        // 会员信息
        Integer memberId = (Integer) dataMap.get("memberId");
        if (memberId != null) {
            sysNoticeLog.setMemberId(memberId);
        }
        // 昵称
        String nickname = (String) dataMap.get("nickname");
        if (StrUtil.isNotEmpty(nickname)) {
            sysNoticeLog.setNickname(nickname);
        }
        // 接收人
        String mobile = (String) dataMap.get("mobile");
        if (StrUtil.isNotEmpty(mobile)) {
            sysNoticeLog.setReceiver(mobile);
        }
        // content
        String content = sysNotice.getSmsContent();
        sysNoticeLog.setContent(content);
        //
        sysNoticeLog.setIsClick(0);
        sysNoticeLog.setIsVisit(0);
        sysNoticeLog.setVisitTime(0);
        sysNoticeLog.setCreateTime(DateUtils.currTime());
        // param参数
        String code = (String) dataMap.get("code");
        String key = noticeJson.getStr("key");
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("site_id", siteId);
        paramsMap.put("code", code);
        paramsMap.put("mobile", mobile);
        paramsMap.put("key", key);
        Map<String, Object> paramsDataMap = new HashMap<>();
        paramsDataMap.put("code", code);
        paramsMap.put("params", paramsDataMap);
        sysNoticeLog.setParams(JSONUtil.toJsonStr(paramsMap));
        return sysNoticeLog;
    }

}
