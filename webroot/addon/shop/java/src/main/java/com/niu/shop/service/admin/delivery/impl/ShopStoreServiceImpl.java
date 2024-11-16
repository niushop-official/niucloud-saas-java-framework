package com.niu.shop.service.admin.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.niu.shop.service.admin.delivery.vo.ShopStoreInfoVo;
import com.niu.shop.service.admin.delivery.vo.ShopStoreListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


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
