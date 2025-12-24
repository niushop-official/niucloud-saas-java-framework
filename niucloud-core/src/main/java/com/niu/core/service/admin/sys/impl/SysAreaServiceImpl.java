package com.niu.core.service.admin.sys.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.core.service.admin.sys.param.SysAreaSearchParam;
import com.niu.core.service.admin.sys.vo.SysAreaByCodeVo;
import com.niu.core.service.admin.sys.vo.SysAreaListVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 地址实现
 */
@Service
public class SysAreaServiceImpl implements ISysAreaService {

    @Resource
    SysAreaMapper sysAreaMapper;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    @Resource
    Cached cached;

    private static final String AREA_CACHE_TAG = CacheTagEnum.AREA_CACHE.getTagName();

    @Override
    public List<SysArea> getListByPid(Integer pid) {
        return cached.rememberObject(AREA_CACHE_TAG, Arrays.asList("getListByPid", pid), uniqueKey -> {
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("pid", pid);
            return sysAreaMapper.selectList(queryWrapper);
        });
    }

    @Override
    public JSONArray getAreaTree(Integer level) {
        return cached.rememberObject(AREA_CACHE_TAG, Arrays.asList("getAreaTree", level), uniqueKey -> {
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.le("level", level);
            List<SysArea> list = sysAreaMapper.selectList(queryWrapper);
            JSONArray jsonArray = JSONUtil.parseArray(JSONUtil.toJsonStr(list));
            return TreeUtils.listToTree(jsonArray, "id", "pid", "child");
        });
    }

    @Override
    public Map<String, SysAreaByCodeVo> getAreaByAreaCode(Integer id) {
        return cached.rememberObject(AREA_CACHE_TAG, Arrays.asList("getAreaByAreaCode", id), uniqueKey -> {
            Map<String, SysAreaByCodeVo> area = new HashMap<>();
            String[] keys = {"", "province", "city", "district"};
            SysArea areaInfo = null;
            SysAreaByCodeVo vo = null;

            areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).select("id,level,pid,name"));
            if (areaInfo != null) {
                vo = new SysAreaByCodeVo();
                BeanUtils.copyProperties(areaInfo, vo);
                area.put(keys[areaInfo.getLevel()], vo);

                while (areaInfo.getLevel() > 1) {
                    areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", areaInfo.getPid()).select("id,level,pid,name"));
                    if (areaInfo != null) {
                        vo = new SysAreaByCodeVo();
                        BeanUtils.copyProperties(areaInfo, vo);
                        area.put(keys[areaInfo.getLevel()], vo);
                    }
                }
            }

            return area;
        });
    }

    @Override
    public Integer getAreaId(String name, Integer level) {
        return cached.rememberObject(AREA_CACHE_TAG, Arrays.asList("getAreaId", name, level), uniqueKey -> {
            SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().like("name", name).eq("level", level).last("limit 1"));
            if (areaInfo != null) {
                return areaInfo.getId();
            }
            return null;
        });
    }

    @Override
    public String getAreaName(Integer id) {
        return cached.rememberObject(AREA_CACHE_TAG, Arrays.asList("getAreaName", id), uniqueKey -> {
            SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).last("limit 1"));
            if (areaInfo != null) {
                return areaInfo.getName();
            }
            return null;
        });
    }

    /**
     * 地址列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAreaListVo>
     */
    @Override
    public PageResult<SysAreaListVo> list(PageParam pageParam, SysAreaSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        IPage<SysArea> iPage = sysAreaMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysAreaListVo> list = new LinkedList<>();
        for (SysArea item : iPage.getRecords()) {
            SysAreaListVo vo = new SysAreaListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 逆地址解析
     * @param location
     * @return
     */
    public JSONObject getAddressInfo(String location) {
        SysMapVo map = coreSysConfigService.getMap(RequestUtils.siteId());
        String result = HttpUtil.get("https://apis.map.qq.com/ws/geocoder/v1/?location="+ location +"&key=" + map.getKey());
        if (!JSONUtil.isJson(result)) return null;
        return JSONUtil.parseObj(result);
    }

    /**
     * 地址解析
     * @param address
     * @return
     */
    @Override
    public JSONObject getAddress(String address) {
        SysMapVo map = coreSysConfigService.getMap(RequestUtils.siteId());
        String result = HttpUtil.get("https://apis.map.qq.com/ws/geocoder/v1/?address="+ address +"&key=" + map.getKey());
        if (!JSONUtil.isJson(result)) return null;
        return JSONUtil.parseObj(result);
    }
}
