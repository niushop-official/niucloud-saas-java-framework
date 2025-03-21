package com.niu.core.service.api.sys.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.sys.ITaskService;
import com.niu.core.service.core.member.ICoreMemberConfigService;
import com.niu.core.service.core.member.ICoreMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;


@Service
@Slf4j
public class TaskServiceImpl implements ITaskService {

    @Resource
    ICoreMemberConfigService coreMemberConfigService;

    @Resource
    ICoreMemberService coreMemberService;

    public JSONObject getGrowthTask() {
        JSONObject config = coreMemberConfigService.getGrowthRuleConfig(RequestUtils.siteId());
        if (config != null) {
            config = coreMemberService.getGrowthRuleContent(RequestUtils.siteId(), config, "task");

            JSONObject filteredObject = new JSONObject();
            for (String key : config.keySet()) {
                JSONObject value = config.getJSONObject(key);
                if (value.get("content") != null) {
                    filteredObject.set(key, value);
                }
            }
            return filteredObject;
        }
        return null;
    }

    public JSONObject getPointTask() {
        JSONObject config = coreMemberConfigService.getPointRuleConfig(RequestUtils.siteId()).getJSONObject("grant");
        if (config != null) {
            config = coreMemberService.getPointGrantRuleContent(RequestUtils.siteId(), config, "task");

            JSONObject filteredObject = new JSONObject();
            for (String key : config.keySet()) {
                JSONObject value = config.getJSONObject(key);
                if (value.get("content") != null) {
                    filteredObject.set(key, value);
                }
            }
            return filteredObject;
        }
        return new JSONObject();
    }
}
