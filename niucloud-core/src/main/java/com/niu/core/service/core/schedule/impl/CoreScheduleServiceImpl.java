package com.niu.core.service.core.schedule.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.job.quartz.QuartzJobManager;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.sys.SysScheduleMapper;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoreScheduleServiceImpl extends ServiceImpl<SysScheduleMapper, SysSchedule> implements ICoreScheduleService {

    @Resource
    SysScheduleMapper sysScheduleMapper;

    @Resource
    private AddonMapper addonMapper;

    /**
     * 安装系统内置计划任务
     */
    @Override
    public void installSystemSchedule() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray schedule = jsonModuleLoader.getResultSet("core", "schedule.json");

        if (ObjectUtil.isNotEmpty(schedule)) {
            List<SysSchedule> scheduleList = sysScheduleMapper.selectList(new QueryWrapper<SysSchedule>().eq("addon", "").select("id,`key`"));
            Map<String, SysSchedule> scheduleMap = scheduleList.stream().collect(Collectors.toMap(SysSchedule::getKey, i -> i));

            List<SysSchedule> list = new LinkedList<>();

            for (int i = 0; i < schedule.size(); i++) {
                JSONObject item = schedule.getJSONObject(i);
                String key = item.getStr("key");
                if (scheduleMap.get(key) == null) {
                    SysSchedule sysScheduleVo = JSONUtil.toBean(item, SysSchedule.class);
                    sysScheduleVo.setAddon("");
                    sysScheduleVo.setStatus(1);
                    list.add(sysScheduleVo);
                }
            }

            if (list.size() > 0) {
                for (SysSchedule sysScheduleVo : list) {
                    try {
                        QuartzJobManager.stopJob(sysScheduleVo.getKey());
                        QuartzJobManager.startJob(sysScheduleVo);
                    } catch (Exception e) {
                    }
                }
                super.saveBatch(list);
            }
        }
    }

    /**
     * 卸载系统内置计划任务
     */
    @Override
    public void uninstallSystemSchedule() {
        sysScheduleMapper.delete(new QueryWrapper<SysSchedule>().eq("addon", ""));
    }

    /**
     * 安装插件内置计划任务
     */
    @Override
    public void installAddonSchedule(String addon) {
        Map<String, Class<?>> addonModule =  AddonModuleContext.getAddonModuleMap();
        JSONArray schedule = null;
        if (addonModule.get(addon) != null) {
            JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
            schedule = jsonModuleLoader.getResultSet(addon, "schedule.json");
        } else {
            File file = new File(WebAppEnvs.get().webRootDownAddon + addon + "/java/src/main/resources/"+ addon +"/loader/schedule.json");
            if (file.exists()) {
                try {
                    String content = FileUtils.readFileToString(file);
                    if (ObjectUtil.isNotEmpty(content)) {
                        schedule = JSONUtil.parseArray(content);
                    }
                } catch (Exception e) {
                }
            }
        }

        if (ObjectUtil.isNotEmpty(schedule)) {
            List<SysSchedule> scheduleList = sysScheduleMapper.selectList(new QueryWrapper<SysSchedule>().eq("addon", addon).select("id,`key`"));
            Map<String, SysSchedule> scheduleMap = scheduleList.stream().collect(Collectors.toMap(SysSchedule::getKey, i -> i));

            List<SysSchedule> list = new LinkedList<>();

            for (int i = 0; i < schedule.size(); i++) {
                JSONObject item = schedule.getJSONObject(i);
                String key = item.getStr("key");
                if (scheduleMap.get(key) == null) {
                    SysSchedule sysScheduleVo = JSONUtil.toBean(item, SysSchedule.class);
                    sysScheduleVo.setAddon(addon);
                    sysScheduleVo.setStatus(1);
                    list.add(sysScheduleVo);
                }
            }

            if (list.size() > 0) {
                for (SysSchedule sysScheduleVo : list) {
                    try {
                        QuartzJobManager.stopJob(sysScheduleVo.getKey());
                        QuartzJobManager.startJob(sysScheduleVo);
                    } catch (Exception e) {
                    }
                }
                super.saveBatch(list);
            }
        }
    }

    /**
     * 卸载插件内置计划任务
     */
    @Override
    public void uninstallAddonSchedule(String addon) {
        sysScheduleMapper.delete(new QueryWrapper<SysSchedule>().eq("addon", addon));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetSchedule() {
        try {
            sysScheduleMapper.delete(new QueryWrapper<SysSchedule>());
            installSystemSchedule();
            List<Addon> addonList = addonMapper.selectList(new LambdaQueryWrapper<Addon>().eq(Addon::getStatus, 1));
            addonList.forEach(addon -> {
                installAddonSchedule(addon.getKey());
            });
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
