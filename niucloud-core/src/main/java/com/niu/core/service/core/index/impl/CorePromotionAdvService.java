package com.niu.core.service.core.index.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.service.core.index.ICorePromotionAdvService;
import com.niu.core.service.core.niucloud.ICoreNiucloudConfigService;
import com.niu.core.service.core.niucloud.vo.NiucloudConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CorePromotionAdvService implements ICorePromotionAdvService {

    @Resource
    private ICoreNiucloudConfigService coreNiucloudConfigService;

    @Override
    public JSONArray getIndexAdvList() {
        Map<String, Object> params = new HashMap<>();
        NiucloudConfigVo config = coreNiucloudConfigService.getNiucloudConfig();
        params.put("code", config.getAuthCode());
        params.put("secret", config.getAuthSecret());
        params.put("recommend_type", "JAVA");
        JSONObject jsonObject = NiucloudUtils.Niucloud.get("promotion_adv", params);
        JSONArray data = jsonObject.getJSONArray("data");
        return data;
    }

}
