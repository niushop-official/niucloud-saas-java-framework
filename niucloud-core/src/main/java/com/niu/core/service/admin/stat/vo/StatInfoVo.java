package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<StatAboutVo> about;
    private StatAppVo app;
    private StatDateVo memberCountStat;
    private StatTypeVo memberStat;
    private StatTypeVo siteGroupStat;
    private StatDateVo siteStat;
    private StatSystemVo system;
    private StatToDayVo todayData;
    private StatVersionVo version;

    /**
     * 基础构造函数
     */
    public StatInfoVo(){
        List<StatAboutVo> aboutlist=new ArrayList<>();
        aboutlist.add(new StatAboutVo("微信扫码关注", "static/resource/icon/index_icon/wx_qrcode.jpg", "Niucloud官方公众号"));
        aboutlist.add(new StatAboutVo("更多内容请扫码加入", "static/resource/icon/index_icon/wework_qrcode.png", "添加企业微信群"));
        this.about=aboutlist;
    }

}
