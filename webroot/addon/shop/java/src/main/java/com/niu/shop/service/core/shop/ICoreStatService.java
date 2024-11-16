package com.niu.shop.service.core.shop;

import com.niu.shop.service.core.shop.param.AddStatParam;
import com.niu.shop.service.core.shop.vo.StatDataVo;
import com.niu.shop.service.core.shop.vo.StatVo;

public interface ICoreStatService {

    /**
     * 添加统计数据
     *
     * @param siteId
     * @param data
     */
    void addStat(Integer siteId, AddStatParam data);

    /**
     * 查询统计总和
     * @param siteId
     * @return
     */
    StatVo getStat(Integer siteId);

    /**
     * 查询统计总和
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    StatVo getStat(Integer siteId, Long startTime, Long endtTime);

    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    StatDataVo getStatData(Integer siteId, Long startTime, Long endtTime);
}
