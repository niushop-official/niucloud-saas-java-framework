package com.niu.shop.service.core.marketing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopNewcomerBannerList {
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("toLink")
    private ToLink toLink;

    @Data
    public static class ToLink {
        private String action;
        private String name;
        private String parent;
        private String title;
        private String url;
    }
}
