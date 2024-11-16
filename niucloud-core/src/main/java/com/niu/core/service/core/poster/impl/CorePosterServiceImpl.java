package com.niu.core.service.core.poster.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.event.pay.PayCreateEventDefiner;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.GetPosterParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CorePosterServiceImpl implements ICorePosterService {

    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 获取海报
     */
    @Override
    public void get(GetPosterParam param) {
        QueryWrapper query = new QueryWrapper<>();
        query.eq("site_id", param.getSiteId());
        query.eq("type", param.getType());
        query.eq("status", 1);
        query.last("limit 1");
        if (ObjectUtil.isNotEmpty(param.getId()) && param.getId() > 0) {
            query.eq("id", param.getId());
        } else {
            query.eq("is_default", 1);
        }
        SysPoster model = sysPosterMapper.selectOne(query);

        try {
            // 海报模板
            JSONObject poster = new JSONObject();
            if (model == null) {
                JSONArray template = template("", param.getType());
                if (template.size() > 0) {
                    poster = template.getJSONObject(0).getJSONObject("data");
                }
            } else {
                poster = JSONUtil.parseObj(model.getValue());
            }

            // 获取海报数据
            GetPosterDataEventDefiner.GetPosterDataEvent event = new GetPosterDataEventDefiner.GetPosterDataEvent();
            event.setSiteId(param.getSiteId());
            event.addAppSign("core");
            event.setName("GetPosterDataEvent");
            event.setType(param.getType());
            List<GetPosterDataEventDefiner.GetPosterDataResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (eventData.size() == 0) throw new CommonException("未获取到海报数据");

            GetPosterDataEventDefiner.GetPosterDataResult posterData = eventData.get(0);

            // 绘制海报
            create(poster, posterData);
        } catch (Exception e) {
            if (param.getIsThrowException()) throw new CommonException(e.getMessage());
        }
    }

    /**
     * 绘制海报
     *
     * @param poster
     * @param posterData
     */
    private void create(JSONObject poster, GetPosterDataEventDefiner.GetPosterDataResult posterData) {

    }

    /**
     * 海报模板
     * @param addon
     * @param type
     * @return
     */
    @Override
    public JSONArray template(String addon, String type) {
        final JSONArray template = new JSONArray();
        if (ObjectUtil.isNotEmpty(addon)) {
            JSONArray coreTemplate = JsonModuleLoader.build().findResultSet("code", "poster/template.json");
            coreTemplate.stream().forEach(e -> template.add(e));

            JSONArray addonTemplate = JsonModuleLoader.build().findResultSet(addon, "poster/template.json");
            addonTemplate.stream().forEach(e -> template.add(e));
        } else {
            JSONArray all = JsonModuleLoader.build().mergeResultSet("poster/template.json");
            all.stream().forEach(e -> template.add(e));
        }

        if (ObjectUtil.isNotEmpty(type) && template.size() > 0) {
            return CollUtil.filter(template, i -> {
                JSONObject item = (JSONObject) i;
                return item.getStr("type", "").equals(type);
            });
        }

        return template;
    }
}
