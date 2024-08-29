package com.niu.core.service.admin.addon;

import com.niu.core.service.admin.addon.param.AddonDevelopAddParam;
import com.niu.core.service.admin.addon.param.AddonDevelopSearchParam;
import com.niu.core.service.admin.addon.vo.AddonDevelopInfoVo;
import com.niu.core.service.admin.addon.vo.AddonDevelopListVo;

import java.util.List;

/**
 * 插件开发
 */
public interface IAddonDevelopService {

    List<AddonDevelopListVo> list(AddonDevelopSearchParam searchParam);

    AddonDevelopInfoVo info(String key);

    void add(AddonDevelopAddParam param);

    void edit(AddonDevelopAddParam param);

    void del(String key);
}
