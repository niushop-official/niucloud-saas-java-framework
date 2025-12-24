package com.niu.core.service.core.verify.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.entity.verify.Verify;
import com.niu.core.event.sys.VerifyCheckEventDefiner;
import com.niu.core.event.sys.VerifyEventDefiner;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.api.sys.param.SysVerifyByCodeParam;
import com.niu.core.service.api.sys.param.SysVerifyCodeParam;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.verify.ICoreVerifyService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ICoreVerifyServiceImpl implements ICoreVerifyService {

    private static final String CACHE_VERIFY = "verify";

    @Resource
    Cached cached;

    @Resource
    VerifyMapper verifyMapper;

    @Resource
    VerifierMapper verifierMapper;

    /**
     * 核销员核销
     * @param siteId
     * @param memberId
     * @param code
     */
    @Override
    public void verifyCode(Integer siteId, Integer memberId, String code) {
        // 1、获取核销码数据
        Map<String, Object> verifyMap = this.getCodeData(code);
        // 检测站点数据
        if (verifyMap.get("site_id") == null || !verifyMap.get("site_id").equals(siteId)) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        // 检测核销员身份
        LambdaQueryWrapper<Verifier> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Verifier::getMemberId, memberId);
        queryWrapper.eq(Verifier::getSiteId, siteId);
        Boolean verifier = verifierMapper.exists(queryWrapper);
        if (!verifier) {
            throw new RuntimeException("核销员不存在");
        }
        // 核销操作
        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", verifyMap.get("site_id"));
        verifyDataMap.put("code", code);
        verifyDataMap.put("body", verifyMap.get("body"));
        verifyDataMap.put("data", verifyMap.get("data"));
        verifyDataMap.put("value", verifyMap.get("value"));
        verifyDataMap.put("type", verifyMap.get("type"));
        verifyDataMap.put("relate_tag", verifyMap.get("relate_tag"));
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", memberId);
        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(siteId);
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);

        // 添加核销记录
        Verify model = new Verify();
        BeanUtils.copyProperties(verifyEvent, model);
        model.setData(JSONUtil.parseObj(verifyEvent.getData()).toString());
        verifyMapper.insert(model);

        // 核销码失效
        this.clearCode(code);
    }

    public Map<String, Object> getInfoByCode(Integer siteId, Integer memberId, String code) {
        Map<String, Object> codeMap = this.getCodeData(code);
        if (codeMap.get("site_id") == null || !codeMap.get("site_id").equals(siteId)) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }

        // 2、通过事件获取核销创建，取第一条
        VerifyCheckEventDefiner.VerifyCheckEvent verifyCheckEvent = new VerifyCheckEventDefiner.VerifyCheckEvent();
        verifyCheckEvent.setData(codeMap);
        verifyCheckEvent.setSiteId(siteId);
        List<VerifyCheckEventDefiner.VerifyCheckEventResult> list = EventAndSubscribeOfPublisher.publishAndCallback(verifyCheckEvent);
        if (CollectionUtils.isNotEmpty(list)) {
            for (VerifyCheckEventDefiner.VerifyCheckEventResult checkEventResult : list) {
                if (checkEventResult != null && checkEventResult.getData() != null && !checkEventResult.getData().isEmpty()){
                    codeMap = checkEventResult.getData();
                    break;
                }
            }
        }
        LambdaQueryWrapper<Verifier> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Verifier::getMemberId, memberId);
        queryWrapper.eq(Verifier::getSiteId, siteId);
        queryWrapper.last("LIMIT 1");
        Verifier verifier = verifierMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(verifier) || !verifier.getVerifyType().contains(codeMap.get("type").toString())){
            throw new CommonException("该核销员没有权限");
        }

        //处理时间的问题 解析JSON字符串
        JSONObject jsonObject = JSONUtil.parseObj(codeMap.get("value").toString());
        // 获取content下的diy数组
        JSONArray diyArray = jsonObject.getJSONObject("content").getJSONArray("diy");
        // 遍历diy数组
        for (int i = 0; i < diyArray.size(); i++) {
            JSONObject diyItem = diyArray.getJSONObject(i);
            JSONArray listArray = diyItem.getJSONArray("list");

            // 遍历list数组
            for (int j = 0; j < listArray.size(); j++) {
                JSONObject listItem = listArray.getJSONObject(j);
                String title = listItem.getStr("title");

                // 如果是支付时间，则转换格式
                if ("支付时间".equals(title)) {
                    long timestamp = listItem.getLong("value");
                    String timeStr = DateUtils.timestampToString(timestamp);
                    listItem.put("value", timeStr);
                }
            }
        }
        codeMap.put("value", jsonObject);
        return codeMap;
    }

    /**
     * 管理端核销
     * @param siteId
     * @param code
     */
    @Override
    public void adminVerify(Integer siteId, String code) {
        Map<String, Object> verifyMap = getCodeData(code);
        // 检测站点数据
        if (verifyMap.get("site_id") == null || !verifyMap.get("site_id").equals(siteId)) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }

        Map<String, Object> verifyDataMap = new HashMap<>();
        verifyDataMap.put("site_id", siteId);
        verifyDataMap.put("code", code);
        verifyDataMap.put("body", verifyMap.get("body"));
        verifyDataMap.put("data", verifyMap.get("data"));
        verifyDataMap.put("value", verifyMap.get("value"));
        verifyDataMap.put("type", verifyMap.get("type"));
        verifyDataMap.put("relate_tag", verifyMap.get("relate_tag"));
        verifyDataMap.put("create_time", DateUtils.currTime());
        verifyDataMap.put("verifier_member_id", 0);
        verifyDataMap.put("is_admin", 1);

        // 核销
        VerifyEventDefiner.VerifyEvent verifyEvent = new VerifyEventDefiner.VerifyEvent();
        verifyEvent.setSiteId(siteId);
        verifyEvent.build(verifyDataMap);
        EventAndSubscribeOfPublisher.publishAndCallback(verifyEvent);

        // 添加核销记录
        Verify model = new Verify();
        BeanUtils.copyProperties(verifyEvent, model);
        model.setIsAdmin(1);
        model.setData(JSONUtil.parseObj(verifyEvent.getData()).toString());
        verifyMapper.insert(model);

        // 核销码失效
        this.clearCode(code);
    }

    @Override
    public Map<String, Object> adminGetInfoByCode(Integer siteId, String code) {
        Map<String, Object> verifyMap = getCodeData(code);
        // 检测站点数据
        if (verifyMap.get("site_id") == null || !verifyMap.get("site_id").equals(siteId)) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }

        VerifyCheckEventDefiner.VerifyCheckEvent verifyCheckEvent = new VerifyCheckEventDefiner.VerifyCheckEvent();
        verifyCheckEvent.setData(verifyMap);
        verifyCheckEvent.setSiteId(siteId);
        List<VerifyCheckEventDefiner.VerifyCheckEventResult> list = EventAndSubscribeOfPublisher.publishAndCallback(verifyCheckEvent);
        if (CollectionUtils.isNotEmpty(list)) {
            for (VerifyCheckEventDefiner.VerifyCheckEventResult checkEventResult : list) {
                if (checkEventResult != null && checkEventResult.getData() != null && !checkEventResult.getData().isEmpty()){
                    verifyMap = checkEventResult.getData();
                    break;
                }
            }
        }

        return verifyMap;
    }

    private Map<String, Object> getCodeData(String verifyCode) {
        Map<String, Object> codeMap = (Map<String, Object>) cached.tag(CACHE_VERIFY).get(verifyCode);
        if (codeMap == null) {
            // 核销码已过期
            throw new RuntimeException("当前核销码已核销或已失效");
        }
        return codeMap;
    }

    /**
     * 设置核销码数据缓存
     *
     * @param verifyCode
     * @param valueMap
     * @param timeoutSeconds
     */
    private void createCode(String verifyCode, Map<String, Object> valueMap, Long timeoutSeconds) {
        if (timeoutSeconds != null && timeoutSeconds > 0) {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap, timeoutSeconds);
        } else {
            cached.tag(CACHE_VERIFY).put(verifyCode, valueMap);
        }
    }

    /**
     * 清除核销
     *
     * @param verifyCode
     */
    private void clearCode(String verifyCode) {
        cached.tag(CACHE_VERIFY).remove(verifyCode);
    }
}
