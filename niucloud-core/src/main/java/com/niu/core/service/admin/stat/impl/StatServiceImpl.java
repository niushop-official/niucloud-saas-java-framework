package com.niu.core.service.admin.stat.impl;

import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.enums.common.SexEnum;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteSearchParam;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.admin.stat.vo.*;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.param.CoreAddonSearchParam;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.member.param.MemberStatSearchParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatServiceImpl implements IStatService {

    @Resource
    ISystemService systemService;
    @Resource
    ICoreMemberService coreMemberService;
    @Resource
    ISiteService siteService;
    @Resource
    ISiteGroupService siteGroupService;
    @Resource
    ICoreAddonService coreAddonService;

    /**
     * 首页统计数据
     *
     * @return
     */
    @Override
    public StatInfoVo getIndexData() {
        StatInfoVo statInfoVo = new StatInfoVo();
        String[] createTimes = new String[2];
        createTimes[0] = DateUtils.currInitDate();
        createTimes[1] = DateUtils.currDate();
        /**
         * 会员统计类
         */
        StatToDayVo statToDayVo = new StatToDayVo();
        //总会员数
        Integer totalMemberCount = coreMemberService.getMemberCount(new MemberStatSearchParam());
        statToDayVo.setTotalMemberCount(totalMemberCount);

        //今天注册总会员数
        MemberStatSearchParam todayMemberParam = new MemberStatSearchParam();
        todayMemberParam.setCreateTime(createTimes);
        statToDayVo.setTodayMemberCount(coreMemberService.getMemberCount(todayMemberParam));
        //总站点数
        statToDayVo.setTotalSiteCount(siteService.getSiteCountByCondition(new SiteSearchParam()));
        //今日站点数
        SiteSearchParam todaySiteParam = new SiteSearchParam();
        todaySiteParam.setCreateTime(createTimes);
        todaySiteParam.setAppType(AppTypeEnum.SITE.getName());
        statToDayVo.setTodaySiteCount(siteService.getSiteCountByCondition(todaySiteParam));
        //正常站点数
        SiteSearchParam normaSiteParam = new SiteSearchParam();
        normaSiteParam.setStatus(1);
        normaSiteParam.setAppType(AppTypeEnum.SITE.getName());
        statToDayVo.setNormaSiteCount(siteService.getSiteCountByCondition(normaSiteParam));
        //到期站点数
        SiteSearchParam expireSiteParam = new SiteSearchParam();
        expireSiteParam.setStatus(2);
        expireSiteParam.setAppType(AppTypeEnum.SITE.getName());
        statToDayVo.setExpireSiteCount(siteService.getSiteCountByCondition(expireSiteParam));
        //即将到期站点数
        SiteSearchParam weekExpireSiteParam = new SiteSearchParam();
        String[] expireTimes = new String[2];
        expireTimes[0] = DateUtils.currDate();
        expireTimes[1] = DateUtils.getDateAddDay(7);
        weekExpireSiteParam.setStatus(1);
        weekExpireSiteParam.setExpireTime(expireTimes);
        weekExpireSiteParam.setAppType(AppTypeEnum.SITE.getName());
        statToDayVo.setWeekExpireSiteCount(siteService.getSiteCountByCondition(weekExpireSiteParam));

        /**
         * 系统数据类
         */
        StatSystemVo statSystemVo = new StatSystemVo();
        statSystemVo = systemService.getSystemInfo();

        statInfoVo.setTodayData(statToDayVo);
        statInfoVo.setSystem(statSystemVo);

        /**
         * 站点、会员数据统计数据
         */
        StatDateVo memberCountVo = new StatDateVo();
        StatDateVo siteCountVo = new StatDateVo();
        List<String> dates = new ArrayList<>();
        List<Integer> memberValues = new ArrayList<>();
        List<Integer> siteValues = new ArrayList<>();
        Integer statNum = 7;
        for (Integer i = 0; i <= statNum; i++) {

            String itemDay = DateUtils.getDateAddDay(i - statNum);
            String[] startEndDate = DateUtils.getStartEndByDay(itemDay);

            MemberStatSearchParam itemMemberParam = new MemberStatSearchParam();
            itemMemberParam.setCreateTime(startEndDate);
            Integer itemMemberCount = coreMemberService.getMemberCount(itemMemberParam);
            dates.add(startEndDate[0]);
            memberValues.add(itemMemberCount);
            SiteSearchParam itemSiteParam = new SiteSearchParam();
            itemSiteParam.setCreateTime(startEndDate);
            Integer itemSiteCount = siteService.getSiteCountByCondition(itemSiteParam);
            siteValues.add(itemSiteCount);
        }
        memberCountVo.setDate(dates);
        memberCountVo.setValue(memberValues);
        siteCountVo.setDate(dates);
        siteCountVo.setValue(siteValues);

        statInfoVo.setMemberCountStat(memberCountVo);
        statInfoVo.setSiteStat(siteCountVo);

        /**
         * 会员性别类型统计
         */
        StatTypeVo memberStat = new StatTypeVo();
        List<String> sexlist = new ArrayList<>();
        sexlist.add(SexEnum.MAN.getName());
        sexlist.add(SexEnum.WOMAN.getName());
        sexlist.add(SexEnum.UNKNOWN.getName());

        List<Integer> sexCountList = new ArrayList<>();
        MemberStatSearchParam sexMemberParam = new MemberStatSearchParam();
        sexMemberParam.setSex(SexEnum.MAN.getValue());
        Integer manSexCount = coreMemberService.getMemberCount(sexMemberParam);
        sexMemberParam.setSex(SexEnum.WOMAN.getValue());
        Integer womanSexCount = coreMemberService.getMemberCount(sexMemberParam);
        sexCountList.add(manSexCount);
        sexCountList.add(womanSexCount);
        sexCountList.add(totalMemberCount - manSexCount - womanSexCount);
        memberStat.setType(sexlist);
        memberStat.setValue(sexCountList);
        statInfoVo.setMemberStat(memberStat);

        /**
         * 站点分组 统计
         */
        StatTypeVo siteGroupStat = new StatTypeVo();
        List<String> grouplist = new ArrayList<>();
        List<Integer> groupCountList = new ArrayList<>();

        List<SiteGroup> groupList = siteGroupService.getAll();
        for (SiteGroup siteGroup : groupList) {
            grouplist.add(siteGroup.getGroupName());
            SiteSearchParam siteGroupParam = new SiteSearchParam();
            siteGroupParam.setGroupId(siteGroup.getGroupId());
            groupCountList.add(siteService.getSiteCountByCondition(siteGroupParam));
        }
        siteGroupStat.setType(grouplist);
        siteGroupStat.setValue(groupCountList);
        statInfoVo.setSiteGroupStat(siteGroupStat);
        /**
         * 所有应用安装统计
         */
        StatAppVo appVo = new StatAppVo();
        Integer totalAddonCount = coreAddonService.getLocalAddonCount();
        Integer installAddonCount = coreAddonService.getAddonCountByCondition(new CoreAddonSearchParam());
        appVo.setAppCount(totalAddonCount);
        appVo.setAppInstalledCount(installAddonCount);
        appVo.setAppNoInstalledCount(Math.max(totalAddonCount - installAddonCount, 0));
        statInfoVo.setApp(appVo);
        return statInfoVo;
    }
}
