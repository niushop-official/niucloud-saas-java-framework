package com.niu.shop.service.admin.marketing.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.active.ShopActive;
import com.niu.shop.mapper.active.ShopActiveMapper;
import com.niu.shop.service.admin.marketing.param.ShopActiveParam;
import com.niu.shop.service.admin.marketing.param.ShopDiscountSearchParam;
import com.niu.shop.service.admin.marketing.IShopActiveService;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopDiscountListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 店铺营销活动（整体活动）实现
 */
@Service
public class ShopActiveServiceImpl implements IShopActiveService {
        
    @Resource
    ShopActiveMapper shopActiveMapper;

    /**
     * 店铺营销活动（整体活动）列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    @Override
    public PageResult<ShopDiscountListVo> list(PageParam pageParam, ShopDiscountSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopActive> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("active_id");

        IPage<ShopActive> iPage = shopActiveMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDiscountListVo> list = new LinkedList<>();
        for (ShopActive item : iPage.getRecords()) {
            ShopDiscountListVo vo = new ShopDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 店铺营销活动（整体活动）详情
     * @param id 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public ShopDiscountInfoVo info(Integer id) {
        ShopActive model = shopActiveMapper.selectOne(
                new QueryWrapper<ShopActive>()
                    .eq("active_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopDiscountInfoVo vo = new ShopDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 店铺营销活动（整体活动）添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopActiveParam addParam) {
        ShopActive model = new ShopActive();
        model.setSiteId(addParam.getSiteId());
        model.setActiveName(addParam.getActiveName());
        model.setActiveDesc(addParam.getActiveDesc());
        model.setActiveType(addParam.getActiveType());
        model.setActiveGoodsType(addParam.getActiveGoodsType());
        model.setActiveGoodsInfo(addParam.getActiveGoodsInfo());
        model.setActiveClass(addParam.getActiveClass());
        model.setActiveClassCategory(addParam.getActiveClassCategory());
        model.setRelateMember(addParam.getRelateMember());
        model.setActiveValue(addParam.getActiveValue());
        model.setStartTime(System.currentTimeMillis() / 1000);
        model.setEndTime(System.currentTimeMillis() / 1000);
        model.setActiveStatus(addParam.getActiveStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setActiveOrderMoney(addParam.getActiveOrderMoney());
        model.setActiveOrderNum(addParam.getActiveOrderNum());
        model.setActiveMemberNum(addParam.getActiveMemberNum());
        model.setActiveSuccessNum(addParam.getActiveSuccessNum());
        shopActiveMapper.insert(model);
    }

    /**
     * 店铺营销活动（整体活动）编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopActiveParam editParam) {
        ShopActive model = shopActiveMapper.selectOne(
                new QueryWrapper<ShopActive>()
                    .eq("active_id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setActiveId(id);
        model.setActiveId(editParam.getActiveId());
        model.setSiteId(editParam.getSiteId());
        model.setActiveName(editParam.getActiveName());
        model.setActiveDesc(editParam.getActiveDesc());
        model.setActiveType(editParam.getActiveType());
        model.setActiveGoodsType(editParam.getActiveGoodsType());
        model.setActiveGoodsInfo(editParam.getActiveGoodsInfo());
        model.setActiveClass(editParam.getActiveClass());
        model.setActiveClassCategory(editParam.getActiveClassCategory());
        model.setRelateMember(editParam.getRelateMember());
        model.setActiveValue(editParam.getActiveValue());
        model.setActiveStatus(editParam.getActiveStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setActiveOrderMoney(editParam.getActiveOrderMoney());
        model.setActiveOrderNum(editParam.getActiveOrderNum());
        model.setActiveMemberNum(editParam.getActiveMemberNum());
        model.setActiveSuccessNum(editParam.getActiveSuccessNum());
        shopActiveMapper.updateById(model);
    }

    /**
     * 店铺营销活动（整体活动）删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopActive model = shopActiveMapper.selectOne(
                new QueryWrapper<ShopActive>()
                    .eq("active_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopActiveMapper.delete(new QueryWrapper<ShopActive>().eq("active_id", id));
    }

}
