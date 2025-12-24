package com.niu.core.service.core.poster.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.image.ImageUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysPoster;
import com.niu.core.event.sys.GetPosterDataEventDefiner;
import com.niu.core.event.sys.PosterDrawEvent;
import com.niu.core.mapper.sys.SysPosterMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.poster.param.AddPosterParam;
import com.niu.core.service.core.poster.param.GetPosterParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class CorePosterServiceImpl implements ICorePosterService {

    @Resource
    SysPosterMapper sysPosterMapper;

    /**
     * 获取海报
     */
    @Override
    public String get(GetPosterParam param) {
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
            event.setParam(param.getParam());
            event.setChannel(param.getChannel());
            List<GetPosterDataEventDefiner.GetPosterDataResult> eventData = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (eventData.size() == 0) throw new CommonException("未获取到海报数据");

            GetPosterDataEventDefiner.GetPosterDataResult posterData = eventData.get(0);

            String dir = "upload/poster/" + param.getSiteId() + "/";
            String posterName = "poster_" + SecureUtil.md5(poster.toString()) + "_" + SecureUtil.md5(JSONUtil.parseObj(posterData).toString()) + "_" + param.getChannel() + ".png";

            if (new File(WebAppEnvs.get().webRootDownResource, dir + posterName).exists()) {
                return dir + posterName;
            }

            // 绘制海报
            create(param.getSiteId(), poster, posterData.getData(), dir, posterName);
            return dir + posterName;
        } catch (Exception e) {
            System.out.println("海报生成错误");
            e.printStackTrace();
            if (param.getIsThrowException()) throw new CommonException(e.getMessage());
            return "";
        }
    }

    /**
     * 绘制海报
     *
     * @param poster
     * @param posterData
     */
    private void create(Integer siteId, JSONObject poster, Map<String, Object> posterData, String dir, String posterName) throws Exception {
        ImageCombiner combiner = null;
        if (poster.getByPath("global.bgType", String.class).equals("color")) {
            int width = poster.getByPath("global.width", int.class);
            int height = poster.getByPath("global.height", int.class);
            String bgColor = poster.getByPath("global.bgColor", String.class);
            combiner = new ImageCombiner(width, height, Color.decode(bgColor), OutputFormat.PNG);
        } else {
            String bfgUrl = poster.getByPath("global.bgUrl", String.class);
            combiner = new ImageCombiner(ImageUtils.readImage(bfgUrl), OutputFormat.PNG);
        }

        JSONArray items = poster.getJSONArray("value");
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);

            PosterDrawEvent event = new PosterDrawEvent();
            event.setSiteId(siteId);
            event.setName("PosterDrawEvent");
            event.setCombiner(combiner);
            event.setPoster(item);
            event.setPosterData(posterData);
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        // 图片合成
        combiner.combine();

        FileTools.createDirs(new File(WebAppEnvs.get().webRootDownResource, dir).getPath());

        combiner.save(new File(WebAppEnvs.get().webRootDownResource, dir + posterName).getPath());
    }

    /**
     * 海报模板
     *
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

    @Override
    public void add(AddPosterParam param) {
        SysPoster sysPoster = new SysPoster();
        BeanUtils.copyProperties(param, sysPoster);
        sysPosterMapper.insert(sysPoster);
    }

    @Override
    public Integer getDefaultPosterIdByType(Integer siteId, String type, String addon) {
        QueryWrapper<SysPoster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId)
                .eq("type", type)
                .eq("addon", addon)
                .eq("is_default", 1)
                .eq("status", 1);

        SysPoster poster = sysPosterMapper.selectOne(queryWrapper);
        return poster != null ? poster.getId() : 0;
    }
}
