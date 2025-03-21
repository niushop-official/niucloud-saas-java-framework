package com.niu.shop.listener.treasure;

import lombok.Data;

import java.util.List;

@Data
public class SnowShowDataResult {
    private long count;
    private String url;
    private List<Content> list;

    @Data
    public static class Content {
        private Integer contentId;
        private Integer siteId;
        private Integer memberId;
        private Integer categoryId;
        private String topicIds;
        private Integer contentType;
        private String contentTitle;
        private String contentImage;
        private String contentVideo;
        private String contentCover;
        private String content;
        private String treasureIds;
        private Integer status;
        private String refuseReason;
        private Integer isRecommend;
        private String lat;
        private String lng;
        private String location;
        private Long createTime;
        private Long updateTime;
        private Integer likeNum;
        private Integer commentNum;
        private Integer shareNum;
        private Integer collectNum;
        private Integer viewNum;
    }
}
