package com.niu.core.service.core.site.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.utils.cache.CacheTag;
import com.niu.core.common.utils.json.JsonConverterUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.service.core.site.ICoreSiteGroupService;
import com.niu.core.service.core.site.ICoreSiteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class CoreSiteGroupServiceImpl extends ServiceImpl<SiteGroupMapper, SiteGroup> implements ICoreSiteGroupService {

    @Resource
    private Cached cached;

    // 将实体类名称作为缓存Key
    private String cacheTag = CacheTag.key(SiteGroup.class);
    // 是否使用缓存
    private boolean cacheUsed = true;

    @Resource
    private ICoreSiteService coreSiteService;

    @Override
    public SiteGroup findUseCacheById(Integer groupId) {
        SiteGroup siteGroup = null;
        if (cacheUsed) {
            siteGroup = (SiteGroup) cached.tag(cacheTag).get(String.valueOf(groupId));
        }
        if (siteGroup == null) {
            siteGroup = super.getById(groupId);
            if (siteGroup != null) {
                cached.tag(cacheTag).put(String.valueOf(groupId), siteGroup);
            }
        }
        return siteGroup;
    }

    @Override
    public SiteGroup refreshCacheById(Integer groupId) {
        SiteGroup siteGroup = super.getById(groupId);
        if (siteGroup != null && cacheUsed) {
            cached.tag(cacheTag).put(String.valueOf(groupId), siteGroup);
        }
        return siteGroup;
    }

    @Override
    public SiteGroup removeCacheById(Integer groupId) {
        SiteGroup siteGroup = null;
        if (cacheUsed) {
            siteGroup = (SiteGroup) cached.tag(cacheTag).get(String.valueOf(groupId));
            if (siteGroup != null) {
                cached.tag(cacheTag).remove(String.valueOf(groupId));
            }
        }
        return siteGroup;
    }

    @Override
    public Set<String> getAddonNamesBySiteId(Integer siteId) {
        Set<String> addonNameSet = new HashSet<>();
        Site site = coreSiteService.findUseCacheById(siteId);
        if (site == null) {
            return addonNameSet;
        }
        SiteGroup siteGroup = this.findUseCacheById(site.getGroupId());
        if (siteGroup == null) {
            return addonNameSet;
        }
        // 将app和addon的字段JSON值，二者合并到List中
        JsonConverterUtils.jsonArrayAppendSet(siteGroup.getApp(), addonNameSet);
        JsonConverterUtils.jsonArrayAppendSet(siteGroup.getAddon(), addonNameSet);
        return addonNameSet;
    }

}
