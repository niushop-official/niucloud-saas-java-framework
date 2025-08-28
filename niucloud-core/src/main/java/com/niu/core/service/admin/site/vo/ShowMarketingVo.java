package com.niu.core.service.admin.site.vo;

import lombok.Data;

import java.util.List;

@Data
public class ShowMarketingVo {
    private String title;
    private List<MarketingTool> list;

    @Data
    public static class MarketingTool {
        private String title;
        private String desc;
        private String icon;
        private String key;
        private String url;
    }
}
