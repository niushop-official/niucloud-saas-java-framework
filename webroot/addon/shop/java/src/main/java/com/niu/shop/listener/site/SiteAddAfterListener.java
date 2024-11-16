package com.niu.shop.listener.site;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.shop.service.core.shop.ICoreAddonService;
import org.springframework.stereotype.Component;

@EventListen("shop")
@Component
public class SiteAddAfterListener extends AbstractListener {

    private ICoreAddonService coreAddonService() {
        return SpringContext.bean(ICoreAddonService.class);
    }

    public void handleEvent(SiteAddAfterEvent event) {
        JSONArray app = JSONUtil.parseArray(event.getSiteGroup().getApp());
        if (app.contains("shop")) {
            coreAddonService().addonInit(event.getSite(), event.getSiteGroup());
        }
    }
}
