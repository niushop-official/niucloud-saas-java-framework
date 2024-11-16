package com.niu.core.service.core.site.factory;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.service.core.site.ICoreSiteGroupService;
import com.niu.core.service.core.site.ICoreSiteService;

public class CoreSiteServiceFactory {

    /**
     * @return
     */
    public static ICoreSiteService getCoreSiteService() {
        return SpringContext.bean(ICoreSiteService.class);
    }

    /**
     * @return
     */
    public static ICoreSiteGroupService getCoreSiteGroupService() {
        return SpringContext.bean(ICoreSiteGroupService.class);
    }

}
