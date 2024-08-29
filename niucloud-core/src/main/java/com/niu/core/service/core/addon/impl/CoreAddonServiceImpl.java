package com.niu.core.service.core.addon.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.service.admin.addon.param.AddonParam;
import com.niu.core.service.core.addon.CoreAddonBaseService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.param.CoreAddonSearchParam;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreAddonServiceImpl extends CoreAddonBaseService implements ICoreAddonService {

    @Resource
    AddonMapper addonMapper;

    /**
     * 获取插件信息
     * @param key
     * @return
     */
    public Addon getInfoByKey(String key) {
        return addonMapper.selectOne(new MPJQueryWrapper<Addon>().select("*").eq("`key`", key));
    }
    /**
     * 获取本地已下载的插件数
     *
     * @return
     */
    @Override
    public Integer getLocalAddonCount() {
        Integer localAddonCount=15;
        return localAddonCount;
    }

    /**
     * 通过条件 查询插件数量
     *
     * @param coreAddonSearchParam
     * @return
     */
    @Override
    public Integer getAddonCountByCondition(CoreAddonSearchParam coreAddonSearchParam) {
        QueryWrapper<Addon> queryWrapper = new QueryWrapper<>();
        Long addonCount = addonMapper.selectCount(queryWrapper);
        return addonCount.intValue();
    }

    @Override
    public Map<String,InstallAddonListVo> getInstallAddonList() {
        List<Addon> addonList = addonMapper.selectList(new MPJQueryWrapper<Addon>().select("title, icon, `key`, `desc`, `status`, author, version, install_time, update_time, cover, type").eq("status", AddonStatusEnum.ON.getCode()));
        Map<String,InstallAddonListVo> map = new HashMap<>();
        if (ObjectUtil.isNotEmpty(addonList)) {
            for(Addon item: addonList)
            {
                InstallAddonListVo installAddonListVo = new InstallAddonListVo();
                BeanUtils.copyProperties(item, installAddonListVo);
                map.put(item.getKey(), installAddonListVo);
            }
        }
        return map;
    }

    /**
     * 设置插件(安装或更新)
     */
    @Override
    public void set(Addon addon) {
        Addon model = addonMapper.selectOne(new QueryWrapper<Addon>().eq("`key`", addon.getKey()));

        if (model != null) {
            addon.setUpdateTime(System.currentTimeMillis() / 1000);
            addonMapper.update(addon, new QueryWrapper<Addon>().eq("`key`", addon.getKey()));
        } else {
            addon.setInstallTime(System.currentTimeMillis() / 1000);
            addonMapper.insert(addon);
        }
    }
}
