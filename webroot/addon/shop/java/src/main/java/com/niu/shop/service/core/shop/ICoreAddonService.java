package com.niu.shop.service.core.shop;

import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.shop.service.core.shop.param.AddStatParam;
import com.niu.shop.service.core.shop.vo.StatDataVo;
import com.niu.shop.service.core.shop.vo.StatVo;

public interface ICoreAddonService {

    /**
     * 插件初始化
     *
     * @param site
     */
    void addonInit(Site site, SiteGroup siteGroup);
}
