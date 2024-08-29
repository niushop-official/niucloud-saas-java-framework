package com.niu.core.event.site;

import com.niu.core.component.context.event.Event;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import lombok.Data;

/**
 * 自定义事件（采用SpringBoot的事件）
 */
@Data
public class SiteAddAfterEvent extends Event {

    private Site site;

    private SiteGroup siteGroup;
}