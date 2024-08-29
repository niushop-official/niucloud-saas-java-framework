package com.niu.core.service.core.site;

import com.niu.core.entity.site.SiteGroup;

import java.util.Set;

public interface ICoreSiteGroupService {

    /**
     * 根据ID查找站点组
     *
     * @param groupId
     * @return
     */
    public SiteGroup findUseCacheById(Integer groupId);

    /**
     * 刷新缓存
     *
     * @param groupId
     * @return
     */
    public SiteGroup refreshCacheById(Integer groupId);

    /**
     * 移除缓存
     *
     * @param groupId
     * @return
     */
    public SiteGroup removeCacheById(Integer groupId);

    /**
     * 通过站点ID获取套餐中的：app值和addon值，且合并返回
     *
     * @param siteId
     * @return
     */
    Set<String> getAddonNamesBySiteId(Integer siteId);

}
