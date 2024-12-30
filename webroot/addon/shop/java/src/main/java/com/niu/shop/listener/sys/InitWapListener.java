package com.niu.shop.listener.sys;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.member.MemberRegisterEvent;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import org.springframework.stereotype.Component;

@Component
@EventListen("shop")
public class InitWapListener extends AbstractListener {

    public void handleEvent(MemberRegisterEvent event) {
        int siteId = RequestUtils.siteId();
        if (ObjectUtil.isNotEmpty(event.getSiteId())) {
            siteId = event.getSiteId();
        }

        AddStatParam param = new AddStatParam();
        param.setAccessSum(1);
        SpringContext.bean(ICoreStatService.class).addStat(siteId, param);
    }
}
