package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 当天数据统计
 */
@Data
public class StatToDayVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer expireSiteCount;
    private Integer memberCount;
    private Integer normaSiteCount;
    private Integer siteCount;
    private Integer todayMemberCount;
    private Integer todaySiteCount;
    private Integer totalMemberCount;
    private Integer totalSiteCount;
    private Integer totalVisitCount;
    private Integer visitCount;
    private Integer weekExpireSiteCount;

    public StatToDayVo(){
        this.expireSiteCount=0;
        this.memberCount=0;
        this.normaSiteCount=0;
        this.siteCount=0;
        this.todayMemberCount=0;
        this.todaySiteCount=0;
        this.totalMemberCount=0;
        this.totalSiteCount=0;
        this.totalVisitCount=0;
        this.visitCount=0;
        this.weekExpireSiteCount=0;
    }
}
