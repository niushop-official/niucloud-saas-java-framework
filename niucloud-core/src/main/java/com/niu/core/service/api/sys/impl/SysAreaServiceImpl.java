package com.niu.core.service.api.sys.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.object.ObjectGroupUtils;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.api.sys.ISysAreaService;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import com.niu.core.service.api.sys.tools.TencentGeocoder;
import com.niu.core.service.api.sys.vo.SysAreaLevelVo;
import com.niu.core.service.api.sys.vo.SysAreaListVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysAreaServiceImpl implements ISysAreaService {

    // 是否使用缓存
    static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Autowired
    private ICoreSysConfigService coreSysConfigService;

    @Override
    public SysArea getAreaById(Integer id) {
        // 缓存分组名称
        String group = SysArea.class.getSimpleName();
        // 缓存Key值计算参数
        List<Object> paramList = Arrays.asList(id);
        // 自动缓存
        return cached.rememberObject(useCache, group, paramList, (uniqueKey) -> {
            SysArea sysArea = sysAreaMapper.selectById(id);
            log.info("result object ->" + sysArea);
            return sysArea;
        });
    }

    @Override
    public List<SysAreaListVo> getListByPid(Integer pid) {
        // 缓存分组名称
        String group = SysAreaListVo.class.getSimpleName();
        // 缓存Key值计算参数
        List<Object> paramList = Arrays.asList(pid);
        // 自动缓存
        List<SysAreaListVo> outterSysAreaList = cached.remember(useCache, group, paramList, uniqueKey -> {
            List<SysAreaListVo> sysAreaVoList = null;
            try {
                QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id,name");
                queryWrapper.eq("pid", pid);
                List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
                sysAreaVoList = CollectUtils.convert(sysAreaList, SysAreaListVo.class);
            } catch (Exception e) {
                log.error("查询区域错误：", e.getMessage());
                throw new BaseException("查询区域错误");
            }
            return sysAreaVoList;
        });
        return outterSysAreaList;
    }

    @Override
    public List<SysAreaLevelVo> getAreaTree(Integer level) {
        if (level == 1) {
            // 参数级别：1，返回1级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name");
            queryWrapper.eq("level", level);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            return CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
        } else if (level == 2) {
            // 参数级别：2，返回1级包含2级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name, level");
            queryWrapper.in("level", 1, 2);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            List<SysAreaLevelVo> sysAreaLevelVoList = CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
            Map<Integer, List<SysAreaLevelVo>> sysAreaMap = ObjectGroupUtils.groupList(sysAreaLevelVoList, "pid");
            return assembleAreaTree(sysAreaLevelVoList, sysAreaMap, 1, 2);
        } else if (level == 3) {
            // 参数级别：3，返回1级包含2级，2级包含3级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name, level");
            queryWrapper.in("level", 1, 2, 3);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            List<SysAreaLevelVo> sysAreaLevelVoList = CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
            Map<Integer, List<SysAreaLevelVo>> sysAreaMap = ObjectGroupUtils.groupList(sysAreaLevelVoList, "pid");
            return assembleAreaTree(sysAreaLevelVoList, sysAreaMap, 1, 3);
        }
        return new ArrayList<>();
    }

    /**
     * 组装一级树
     *
     * @param sysAreaLevelList
     * @param sysAreaLevelVoMap
     * @param level
     * @param depth
     * @return
     */
    private List<SysAreaLevelVo> assembleAreaTree(List<SysAreaLevelVo> sysAreaLevelList, Map<Integer, List<SysAreaLevelVo>> sysAreaLevelVoMap, int level, int depth) {
        List<SysAreaLevelVo> sysAreaLevelVoTree = new ArrayList<>();
        for (SysAreaLevelVo sysAreaLevelVo : sysAreaLevelList) {
            if (sysAreaLevelVo.getLevel() == level) {
                SysAreaLevelVo newSysAreaLevelVo = new SysAreaLevelVo();
                BeanUtils.copyProperties(sysAreaLevelVo, newSysAreaLevelVo);
                sysAreaLevelVoTree.add(newSysAreaLevelVo);
                assembleAreaTree(newSysAreaLevelVo, sysAreaLevelVoMap, level, depth);
            }
        }
        return sysAreaLevelVoTree;
    }

    /**
     * 递归子树
     *
     * @param sysAreaLevelVo
     * @param sysAreaLevelVoMap
     * @param level
     * @param depth
     */
    private void assembleAreaTree(SysAreaLevelVo sysAreaLevelVo, Map<Integer, List<SysAreaLevelVo>> sysAreaLevelVoMap, int level, int depth) {
        List<SysAreaLevelVo> sysAreaLevelVoCacheList = sysAreaLevelVoMap.get(sysAreaLevelVo.getId());
        sysAreaLevelVo.setChild(sysAreaLevelVoCacheList);
        if (sysAreaLevelVoCacheList != null && sysAreaLevelVoCacheList.size() > 0) {
            if (level <= depth) {
                for (SysAreaLevelVo sysAreaLevelVoCache : sysAreaLevelVoCacheList) {
                    assembleAreaTree(sysAreaLevelVoCache, sysAreaLevelVoMap, level + 1, depth);
                }
            }
        }
    }

    @Override
    public JSONObject getAreaByAreaCode(Integer code) {
        SysArea sysArea = this.sysAreaMapper.selectById(code);
        JSONObject jsonObject = new JSONObject();
        if (sysArea != null) {
            if (sysArea.getLevel() == 1) {
                jsonObject.putOpt("province", convertJsonObject(sysArea));
            } else if (sysArea.getLevel() == 2) {
                jsonObject.putOpt("city", convertJsonObject(sysArea));
                SysArea provinceSysArea = this.sysAreaMapper.selectById(sysArea.getPid());
                if (provinceSysArea != null) {
                    jsonObject.putOpt("province", convertJsonObject(provinceSysArea));
                }
            } else if (sysArea.getLevel() == 3) {
                jsonObject.putOpt("district", convertJsonObject(sysArea));
                SysArea citySysArea = this.sysAreaMapper.selectById(sysArea.getPid());
                if (citySysArea != null) {
                    jsonObject.putOpt("city", convertJsonObject(citySysArea));
                    SysArea provinceSysArea = this.sysAreaMapper.selectById(citySysArea.getPid());
                    if (provinceSysArea != null) {
                        jsonObject.putOpt("province", convertJsonObject(provinceSysArea));
                    }
                }
            }
        }
        return jsonObject;
    }

    /**
     * @param sysArea
     * @return
     */
    private JSONObject convertJsonObject(SysArea sysArea) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("id", sysArea.getId());
        jsonObject.putOpt("level", sysArea.getLevel());
        jsonObject.putOpt("pid", sysArea.getPid());
        jsonObject.putOpt("name", sysArea.getName());
        return jsonObject;
    }

    @Override
    public JSONObject getAddressByLatlng(SysAreaAddressByLatlngParam param) {
        SysMapVo sysMapVo = coreSysConfigService.getMap(param.siteId());
        String key = sysMapVo.getKey();
        String addressJsonString = TencentGeocoder.getAddress(key, param.getLatlng(), param.getGetPoi());
        JSONObject jsonObjectResult = TencentGeocoder.formatData(addressJsonString);
        String province = jsonObjectResult.getStr("province");
        jsonObjectResult.putOpt("province_id", this.queryCodeByNameAndLevel(province, 1));
        String city = jsonObjectResult.getStr("city");
        jsonObjectResult.putOpt("city_id", this.queryCodeByNameAndLevel(city, 2));
        String district = jsonObjectResult.getStr("district");
        jsonObjectResult.putOpt("district_id", this.queryCodeByNameAndLevel(district, 3));
        return jsonObjectResult;
    }

    /**
     * @param areaName
     * @param level
     * @return
     */
    private Integer queryCodeByNameAndLevel(String areaName, int level) {
        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("name", areaName);
        queryWrapper.eq("level", level);
        SysArea sysArea = this.sysAreaMapper.selectOne(queryWrapper);
        if (sysArea != null) {
            return sysArea.getId();
        }
        return null;
    }

}
