package com.niu.core.service.core.site;

import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.service.admin.site.vo.SiteInfoVo;

import java.util.List;

/**
 * 插件服务接口类
 */
public interface ICoreSiteService {

    /**
     * @param siteId
     * @return
     */
    public Site findUseCacheById(Integer siteId);

    /**
     * @param siteId
     * @return
     */
    public Site refreshCacheById(Integer siteId);

    /**
     * @param siteId
     * @return
     */
    public Site removeCacheById(Integer siteId);

    /**
     * 通过站点id获取支持的应用插件
     *
     * @param siteId
     */
    List<String> getAddonKeysBySiteId(Integer siteId);

    /**
     * 站点详情
     *
     * @param siteId 主键ID
     * @return SiteInfoVo
     */
    SiteInfoVo getSiteCache(Integer siteId);

    /**
     * @param siteId
     * @return
     */
    List<Addon> getSiteAddons(Integer siteId);

    /**
     * 站点插件是否已执行初始化
     *
     * @param siteId
     * @param addon
     * @return
     */
    boolean siteAddonIsInit(Integer siteId, String addon);

    /**
     * 添加站点插件初始化记录
     *
     * @param siteId
     * @param addon
     */
    void addSiteAddonInitRecord(Integer siteId, String addon);

    /**
     * 站点到期自动关闭
     *
     */
    void siteExpireClose();
}
