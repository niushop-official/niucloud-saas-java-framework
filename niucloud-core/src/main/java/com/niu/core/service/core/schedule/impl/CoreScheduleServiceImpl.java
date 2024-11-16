package com.niu.core.service.core.schedule.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.sys.SysSchedule;
import com.niu.core.mapper.sys.SysScheduleMapper;
import com.niu.core.service.core.schedule.ICoreScheduleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoreScheduleServiceImpl extends ServiceImpl<SysScheduleMapper, SysSchedule> implements ICoreScheduleService {

    @Resource
    SysScheduleMapper sysScheduleMapper;

    /**
     * 安装系统内置计划任务
     */
    @Override
    public void installSystemSchedule() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray schedule = jsonModuleLoader.getResultSet("core", "schedule.json");

        if (ObjectUtil.isNotEmpty(schedule)) {
            List<SysSchedule> scheduleList = sysScheduleMapper.selectList(new QueryWrapper<SysSchedule>().eq("addon", "core").select("id,`key`"));
            Map<String, SysSchedule> scheduleMap = scheduleList.stream().collect(Collectors.toMap(SysSchedule::getKey, i -> i));

            List<SysSchedule> list = new LinkedList<>();

            for (int i = 0; i < schedule.size(); i++) {
                JSONObject item = schedule.getJSONObject(i);
                String key = item.getStr("key");
                if (scheduleMap.get(key) == null) {
                    SysSchedule sysScheduleVo = JSONUtil.toBean(item, SysSchedule.class);
                    list.add(sysScheduleVo);
                }
            }

            if (list.size() > 0) super.saveBatch(list);
        }
    }

    /**
     * 卸载系统内置计划任务
     */
    @Override
    public void uninstallSystemSchedule() {
        sysScheduleMapper.delete(new QueryWrapper<SysSchedule>().eq("addon", "core"));
    }

    /**
     * 安装插件内置计划任务
     */
    @Override
    public void installAddonSchedule(String addon) {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray schedule = jsonModuleLoader.getResultSet(addon, "schedule.json");

        if (ObjectUtil.isNotEmpty(schedule)) {
            List<SysSchedule> scheduleList = sysScheduleMapper.selectList(new QueryWrapper<SysSchedule>().eq("addon", "core").select("id,`key`"));
            Map<String, SysSchedule> scheduleMap = scheduleList.stream().collect(Collectors.toMap(SysSchedule::getKey, i -> i));

            List<SysSchedule> list = new LinkedList<>();

            for (int i = 0; i < schedule.size(); i++) {
                JSONObject item = schedule.getJSONObject(i);
                String key = item.getStr("key");
                if (scheduleMap.get(key) == null) {
                    SysSchedule sysScheduleVo = JSONUtil.toBean(item, SysSchedule.class);
                    list.add(sysScheduleVo);
                }
            }

            if (list.size() > 0) super.saveBatch(list);
        }
    }

    /**
     * 卸载插件内置计划任务
     */
    @Override
    public void uninstallAddonSchedule(String addon) {
        sysScheduleMapper.delete(new QueryWrapper<SysSchedule>().eq("addon", addon));
    }
}
