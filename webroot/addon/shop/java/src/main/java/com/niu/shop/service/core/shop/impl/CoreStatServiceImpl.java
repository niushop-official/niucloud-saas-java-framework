package com.niu.shop.service.core.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.shop.entity.ShopStat;
import com.niu.shop.mapper.ShopStatMapper;
import com.niu.shop.service.core.shop.ICoreStatService;
import com.niu.shop.service.core.shop.param.AddStatParam;
import com.niu.shop.service.core.shop.param.StatParam;
import com.niu.shop.service.core.shop.vo.StatDataVo;
import com.niu.shop.service.core.shop.vo.StatVo;
import jodd.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoreStatServiceImpl implements ICoreStatService {

    @Resource
    ShopStatMapper shopStatMapper;
    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * 添加统计数据
     *
     * @param siteId
     * @param data
     */
    @Override
    public void addStat(Integer siteId, AddStatParam data) {

        ThreadPoolManager.runTask(() -> {
            lock.lock();
            try{
                ShopStat model = shopStatMapper.selectOne(new QueryWrapper<ShopStat>().eq("date", DateUtil.today()).eq("site_id", siteId));
                if (model == null) {
                    ShopStat stat = new ShopStat();
                    BeanUtil.copyProperties(data, stat);
                    stat.setSiteId(siteId);
                    stat.setDate(DateUtil.today());
                    stat.setDateTime(new DateTime(stat.getDate()).getTime() / 1000);
                    shopStatMapper.insert(stat);
                } else {
                    if (ObjectUtil.isNotEmpty(data.getOrderNum()))
                        model.setOrderNum(model.getOrderNum() + data.getOrderNum());
                    if (ObjectUtil.isNotEmpty(data.getSaleMoney()))
                        model.setSaleMoney(model.getSaleMoney().add(data.getSaleMoney()));
                    if (ObjectUtil.isNotEmpty(data.getRefundMoney()))
                        model.setRefundMoney(model.getRefundMoney().add(data.getRefundMoney()));
                    if (ObjectUtil.isNotEmpty(data.getAccessSum()))
                        model.setAccessSum(model.getAccessSum() + data.getAccessSum());
                    shopStatMapper.updateById(model);
                }
            }catch (Exception e){
                log.error("添加统计数据异常:com.niu.shop.service.core.shop", e);
            }finally {
                lock.unlock();
            }

        });
    }

    /**
     * 查询统计总和
     *
     * @param siteId
     * @return
     */
    @Override
    public StatVo getStat(Integer siteId) {
        StatVo vo = new StatVo();
        ShopStat model = shopStatMapper.selectOne(new QueryWrapper<ShopStat>()
                .eq("site_id", siteId)
                .select("SUM(order_num) AS order_num,SUM(sale_money) AS sale_money,SUM(refund_money) AS refund_money,SUM(access_sum) AS access_sum")
                .groupBy("site_id")
        );
        if (model != null) BeanUtil.copyProperties(model, vo);
        return vo;
    }

    /**
     * 查询统计总和
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatVo getStat(Integer siteId, Long startTime, Long endtTime) {
        StatVo vo = new StatVo();

        QueryWrapper QueryWrapper = new QueryWrapper<ShopStat>()
            .eq("site_id", siteId)
            .select("SUM(order_num) AS order_num,SUM(sale_money) AS sale_money,SUM(refund_money) AS refund_money,SUM(access_sum) AS access_sum")
                .groupBy("site_id");
        QueryMapperUtils.buildByBetween(QueryWrapper, "date_time", new Long[]{ startTime, endtTime });

        ShopStat model = shopStatMapper.selectOne(QueryWrapper);
        if (model != null) BeanUtil.copyProperties(model, vo);
        return vo;
    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getStatData(Integer siteId, Long startTime, Long endtTime) {
        StatDataVo vo = new StatDataVo();

        QueryWrapper QueryWrapper = new QueryWrapper<ShopStat>()
                .eq("site_id", siteId);
        QueryMapperUtils.buildByBetween(QueryWrapper, "date_time", new Long[]{ startTime, endtTime });

        List<ShopStat> list = shopStatMapper.selectList(QueryWrapper);
        Map<String, ShopStat> statMap = list.stream().collect(Collectors.toMap(ShopStat::getDate, i -> i));

        int day = (int) Math.ceil((endtTime - startTime) / 86400);

        for (int i = 0; i < day; i++) {
            String date = DateUtil.format(new DateTime((startTime + i * 86400) * 1000), "yyyy-MM-dd");
            ShopStat data = statMap.get(date);
            if (data == null) {
                vo.getAccessSum().add(0);
                vo.getSaleMoney().add(new BigDecimal(0));
                vo.getRefundMoney().add(new BigDecimal(0));
                vo.getOrderNum().add(0);
            } else {
                vo.getAccessSum().add(data.getAccessSum());
                vo.getSaleMoney().add(data.getSaleMoney());
                vo.getRefundMoney().add(data.getRefundMoney());
                vo.getOrderNum().add(data.getOrderNum());
            }
            vo.getTime().add(date);
        }

        return vo;
    }

    @Override
    public void decStat(StatParam param) {
        String date = DateUtils.timestampToString(param.getTime(), "yyyy-MM-dd");
        Long dateTime = DateUtils.StringToTimestamp(date);
        ShopStat shopStat = shopStatMapper.selectOne(new LambdaQueryWrapper<ShopStat>()
                .eq(ShopStat::getSiteId, param.getSiteId())
                .eq(ShopStat::getDate, date)
                .eq(ShopStat::getDateTime, dateTime));
        if (shopStat != null){
            shopStat.setOrderNum(Math.max(shopStat.getOrderNum() - param.getOrderNum(), 0));
            shopStatMapper.updateById(shopStat);
        }
    }
}
