package com.niu.core.service.core.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.enums.notice.NoticeEnum;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.member.vo.LoginConfigVo;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CoreDiyConfigService implements ICoreDiyConfigService {

    @Resource
    AddonMapper addonMapper;

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public List<BottomConfigVo> getBottomList() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray bottomJson = jsonModuleLoader.mergeContentToArray("diy/bottom.json");

        List<BottomConfigVo> list = new ArrayList<>();
        for (Object bottomMap : bottomJson) {
            JSONObject jsonObject = (JSONObject) bottomMap;
            BottomConfigVo vo = new BottomConfigVo();
            String key = jsonObject.getStr("key");

            Addon addon = new Addon();
            if(key.equals("app"))
            {
                addon.setTitle("系统");
            }else {
                addon = addonMapper.selectOne(new QueryWrapper<Addon>().eq("key", key));
            }

            vo.setInfo(addon);
            vo.setKey(key);
            JSONObject bottomValueJson = jsonObject.getJSONObject("value");
            BottomConfigValue bottomConfigValue = JSONUtil.toBean(bottomValueJson, BottomConfigValue.class);
            vo.setValue(bottomConfigValue);
            list.add(vo);
        }

        return list;
    }

    @Override
    public BottomConfigVo getBottomConfig(Integer siteId, String key) {
        List<BottomConfigVo> list = getBottomList();

        BottomConfigVo config = new BottomConfigVo();
        for (BottomConfigVo vo : list)
        {
            if(vo.getKey().equals(key))
                config = vo;
        }
        if(config.getKey().equals(""))
        {
            throw new AdminException("未找到该配置");
        }
        String configKey = "diy_bottom_" + key;
        JSONObject jsonObject = coreConfigService.getConfigValue(siteId, configKey);
        if(ObjectUtil.isNotEmpty(jsonObject))
        {
            BottomConfigValue bottomConfigValue = JSONUtil.toBean(jsonObject, BottomConfigValue.class);
            config.setValue(bottomConfigValue);

        }

        return config;
    }

    /**
     * 设置底部导航
     *
     * @param siteId
     * @param bottomConfigValue
     * @param key
     */
    @Override
    public void setBottomConfig(Integer siteId, BottomConfigValue bottomConfigValue, String key) {
        coreConfigService.setConfig(siteId, "diy_bottom_" + key, JSONUtil.parseObj(bottomConfigValue));
    }

    /**
     * 启动页配置
     *
     * @param siteId
     * @param value
     * @param name
     */
    @Override
    public void setStartUpPageConfig(Integer siteId, StartUpPageConfigParam value, String name) {
        coreConfigService.setConfig(siteId, "START_UP_PAGE_" + name.toUpperCase(), JSONUtil.parseObj(value));
    }

    /**
     * 获取启动页配置
     *
     * @param siteId
     * @param name
     * @return
     */
    public StartUpPageConfigVo getStartUpPageConfig(Integer siteId, String name) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "START_UP_PAGE_" + name.toUpperCase());
        if (config.isEmpty()) return null;
        return JSONUtil.toBean(config, StartUpPageConfigVo.class);
    }
}
