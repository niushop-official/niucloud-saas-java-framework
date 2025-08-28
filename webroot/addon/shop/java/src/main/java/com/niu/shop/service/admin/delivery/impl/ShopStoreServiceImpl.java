package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.service.admin.delivery.param.ShopStoreParam;
import com.niu.shop.service.admin.delivery.param.ShopStoreSearchParam;
import com.niu.shop.service.admin.delivery.IShopStoreService;
import com.niu.shop.service.admin.delivery.param.TradeTimeItemParam;
import com.niu.shop.service.admin.delivery.vo.ShopStoreInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopStoreListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;

import java.util.*;


/**
 * 自提门店实现
 */
@Service
public class ShopStoreServiceImpl implements IShopStoreService {
        
    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    ISysAreaService areaService;

    /**
     * 获取展示用信息
     */
    @Override
    public HashMap initInfo(){
        HashMap hashMap = new HashMap();
        //获取时段列表
        hashMap.put("time_interval_list",getTimeIntervalList());
        //获取周列表
        hashMap.put("week_list",getWeekList());
        return hashMap;
    }

    // 内部类
    public class IntervalOption {
        private int type;
        private String name;

        public IntervalOption(int type, String name) {
            this.type = type;
            this.name = name;
        }

        // getter方法
        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

    // 获取时间间隔列表
    public Map<Integer, IntervalOption> getTimeIntervalList() {
        Map<Integer, IntervalOption> list = new HashMap<>();

        list.put(30, new IntervalOption(
                30,
                "30分钟"
        ));

        list.put(60, new IntervalOption(
                60,
                "1小时"
        ));

        list.put(90, new IntervalOption(
                90,
                "90分钟"
        ));

        list.put(120, new IntervalOption(
                120,
                "2小时"
        ));

        return list;
    }

    public Map<Integer, IntervalOption> getWeekList(){
        Map<Integer, IntervalOption> list = new HashMap<>();
        list.put(0,new IntervalOption(
                0,
                "周日"
        ));
        list.put(1,new IntervalOption(
                1,
                "周一"
        ));
        list.put(2,new IntervalOption(
                2,
                "周二"
        ));
        list.put(3,new IntervalOption(
                3,
                "周三"
        ));
        list.put(4,new IntervalOption(
                4,
                "周四"
        ));
        list.put(5,new IntervalOption(
                5,
                "周五"
        ));
        list.put(6,new IntervalOption(
                6,
                "周六"
        ));
        return list;
    }

    /**
     * 自提门店分页列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopStoreListVo>
     */
    @Override
    public PageResult<ShopStoreListVo> page(PageParam pageParam, ShopStoreSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getStoreName())) queryWrapper.like("store_name", searchParam.getStoreName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("store_id");

        IPage<ShopStore> iPage = shopStoreMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopStoreListVo> list = new LinkedList<>();
        for (ShopStore item : iPage.getRecords()) {
            ShopStoreListVo vo = new ShopStoreListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 自提门店列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopStoreListVo>
     */
    @Override
    public List<ShopStoreListVo> list(ShopStoreSearchParam searchParam) {
        QueryWrapper<ShopStore> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotEmpty(searchParam.getStoreName())) queryWrapper.like("store_name", searchParam.getStoreName());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("store_id");

        List<ShopStore> record = shopStoreMapper.selectList(queryWrapper);
        List<ShopStoreListVo> list = new LinkedList<>();
        for (ShopStore item : record) {
            ShopStoreListVo vo = new ShopStoreListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 自提门店详情
     * @param id 主键
     * @return ShopStoreInfoVo
     */
    @Override
    public ShopStoreInfoVo info(Integer id) {
        ShopStore model = shopStoreMapper.selectOne(
                new QueryWrapper<ShopStore>()
                    .eq("store_id", id)
                    .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "门店不存在");

        ShopStoreInfoVo vo = new ShopStoreInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setProvinceName(areaService.getAreaName(model.getProvinceId()));
        vo.setCityName(areaService.getAreaName(model.getCityId()));
        vo.setDistrictName(areaService.getAreaName(model.getDistrictId()));
        vo.setTimeWeek(JSONUtil.parseArray(model.getTimeWeek()).toList(String.class));
        vo.setTradeTimeJson(JSONUtil.parseArray(model.getTradeTimeJson()).toList(TradeTimeItemParam.class));
        vo.setTimeInterval(model.getTimeInterval());
        return vo;
    }

    /**
     * 自提门店添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopStoreParam addParam) {
        ShopStore model = new ShopStore();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.siteId());
        model.setProvinceId(areaService.getAreaId(addParam.getProvinceName(), 1));
        model.setCityId(areaService.getAreaId(addParam.getCityName(), 2));
        model.setDistrictId(areaService.getAreaId(addParam.getDistrictName(), 3));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setTimeWeek(JSONUtil.toJsonStr(addParam.getTimeWeek()));
        model.setTimeInterval(addParam.getTimeInterval());
        model.setTradeTimeJson(JSONUtil.toJsonStr(addParam.getTradeTimeJson()));
        shopStoreMapper.insert(model);
    }

    /**
     * 自提门店编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopStoreParam editParam) {
        ShopStore model = shopStoreMapper.selectOne(
                new QueryWrapper<ShopStore>()
                        .eq("store_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "门店不存在");

        BeanUtils.copyProperties(editParam, model);
        model.setProvinceId(areaService.getAreaId(editParam.getProvinceName(), 1));
        model.setCityId(areaService.getAreaId(editParam.getCityName(), 2));
        model.setDistrictId(areaService.getAreaId(editParam.getDistrictName(), 3));
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setTimeWeek(JSONUtil.toJsonStr(editParam.getTimeWeek()));
        model.setTimeInterval(editParam.getTimeInterval());
        model.setTradeTimeJson(JSONUtil.toJsonStr(editParam.getTradeTimeJson()));
        shopStoreMapper.updateById(model);
    }

    /**
     * 自提门店删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        shopStoreMapper.delete(new QueryWrapper<ShopStore>().eq("store_id", id).eq("site_id", RequestUtils.siteId()));
    }

}
