package com.niu.core.listener.site;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.event.site.SiteAddAfterEvent;
import com.niu.core.service.admin.diy.IDiyService;
import com.niu.core.service.admin.diy.param.SetDiyDataParam;
import org.springframework.stereotype.Component;

@EventListen("code")
@Component
public class SiteAddAfterListener extends AbstractListener {

    private IDiyService diyService() {
        return SpringContext.bean(IDiyService.class);
    }

    /**
     * 监听事件
     *
     * @param event
     */
    public void handleEvent(SiteAddAfterEvent event) {
        SiteGroup siteGroup = event.getSiteGroup();
        JSONArray app = JSONUtil.parseArray(siteGroup.getApp());

        SetDiyDataParam setParam = new SetDiyDataParam();
        setParam.setSiteId(event.getSiteId());
        setParam.setType("index");
        setParam.setKey("DIY_INDEX");
        setParam.setIsStart(app.size() > 1 ? 1 : 0);
        setParam.setMainApp(app);
        diyService().setDiyData(setParam);

        setParam.setType("member_index");
        setParam.setKey("DIY_MEMBER_INDEX");
        diyService().setDiyData(setParam);
    }

}