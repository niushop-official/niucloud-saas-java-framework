package com.niu.core.service.core.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
public class CoreConfigServiceImpl implements ICoreConfigService {


    /** 站点tag */
    public static String cacheTagName = CacheTagEnum.SYS_CONFIG_CACHE.getTagName();

    /** 是否使用缓存 */
    public static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Resource
    SysConfigMapper sysConfigMapper;

    /**
     * 获取配置项
     *
     * @param siteId 站点id
     * @param key    关键字
     * @return
     */
    @Override
    public CoreSysConfigVo getConfig(Integer siteId, String key) {

        SysConfig model = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>().eq("config_key", key).eq("site_id", siteId).last(" limit 1"));
        CoreSysConfigVo vo = new CoreSysConfigVo();
        if (ObjectUtil.isNotNull(model)) {
            BeanUtils.copyProperties(model, vo);
            if (!model.getValue().equals("")) {
                vo.setValueJson(JSONUtil.parse(model.getValue()));
            }
        }
        return vo;
    }



    /**
     * 获取系统配置项 返回values
     *
     * @param siteId
     * @param key
     * @return
     */
    @Override
    public JSONObject getConfigValue(Integer siteId, String key) {

        Object jsonObject =  cached.rememberObject(useCache, cacheTagName, Arrays.asList("getConfigValue", siteId, key), uniqueKey -> {

            SysConfig model = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>().eq("config_key", key).eq("site_id", siteId).last(" limit 1"));
            if (ObjectUtil.isNotNull(model)) {
                if (!model.getValue().equals("")) {
                    return model.getValue();
                }else{
                    return "";
                }
            }
            return "";
        });
        if(ObjectUtil.isNotEmpty(jsonObject)){
            return JSONUtil.parseObj(jsonObject);
        }else{
            return new JSONObject();
        }
    }

    /**
     * 获取系统配置项 返回values
     *
     * @param siteId
     * @param key
     * @return
     */
    public JSONArray getConfigArrayValue(Integer siteId, String key) {

        Object jsonObject =  cached.rememberObject(useCache, cacheTagName, Arrays.asList("getConfigArrayValue", siteId, key), uniqueKey -> {
            SysConfig model = sysConfigMapper.selectOne(new QueryWrapper<SysConfig>().eq("config_key", key).eq("site_id", siteId).last(" limit 1"));
            if (ObjectUtil.isNotNull(model)) {
                if (!model.getValue().equals("")) {
                    return model.getValue();
                }else{
                    return "";
                }
            }
            return "";
        });

        if(ObjectUtil.isNotEmpty(jsonObject)){
            return JSONUtil.parseArray(jsonObject.toString());
        }else{
            return new JSONArray();
        }

    }

    /**
     * 修改系统配置项
     *
     * @param siteId
     * @param key
     * @param valueJson
     */
    @Override
    public void setConfig(Integer siteId, String key, JSON valueJson) {
        SysConfig model = sysConfigMapper.selectOne(
                new QueryWrapper<SysConfig>()
                        .eq("config_key", key)
                        .eq("site_id", siteId)
                        .last("limit 1"));
        if (ObjectUtil.isNull(model)) {
            SysConfig addModel = new SysConfig();
            //添加数据
            addModel.setSiteId(siteId);
            addModel.setConfigKey(key);
            addModel.setStatus(1);
            addModel.setUpdateTime(System.currentTimeMillis() / 1000);
            addModel.setAddon("");
            addModel.setValue(valueJson.toString());
            addModel.setCreateTime(System.currentTimeMillis() / 1000);
            sysConfigMapper.insert(addModel);

        } else {
            cached.tag(cacheTagName).clear();
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            model.setValue(valueJson.toString());
            sysConfigMapper.update(model, new QueryWrapper<SysConfig>().eq("config_key", key).eq("site_id", siteId));
        }
    }

}
