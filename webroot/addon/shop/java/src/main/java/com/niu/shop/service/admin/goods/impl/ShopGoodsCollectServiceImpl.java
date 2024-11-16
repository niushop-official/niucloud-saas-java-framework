package com.niu.shop.service.admin.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.shop.entity.goods.ShopGoodsCollect;
import com.niu.shop.mapper.goods.ShopGoodsCollectMapper;
import com.niu.shop.service.admin.goods.param.ShopGoodsCollectParam;
import com.niu.shop.service.admin.goods.param.ShopGoodsCollectSearchParam;
import com.niu.shop.service.admin.goods.IShopGoodsCollectService;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCollectInfoVo;
import com.niu.shop.service.admin.goods.vo.ShopGoodsCollectListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品收藏记录实现
 */
@Service
public class ShopGoodsCollectServiceImpl implements IShopGoodsCollectService {
        
    @Resource
    ShopGoodsCollectMapper shopGoodsCollectMapper;

    /**
     * 商品收藏记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsCollectListVo>
     */
    @Override
    public PageResult<ShopGoodsCollectListVo> list(PageParam pageParam, ShopGoodsCollectSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        IPage<ShopGoodsCollect> iPage = shopGoodsCollectMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsCollectListVo> list = new LinkedList<>();
        for (ShopGoodsCollect item : iPage.getRecords()) {
            ShopGoodsCollectListVo vo = new ShopGoodsCollectListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品收藏记录详情
     * @param id 主键
     * @return ShopGoodsCollectInfoVo
     */
    @Override
    public ShopGoodsCollectInfoVo info(Integer id) {
        ShopGoodsCollect model = shopGoodsCollectMapper.selectOne(
                new QueryWrapper<ShopGoodsCollect>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopGoodsCollectInfoVo vo = new ShopGoodsCollectInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品收藏记录添加
     * @param addParam 参数
     */
    @Override
    public void add(ShopGoodsCollectParam addParam) {
        ShopGoodsCollect model = new ShopGoodsCollect();
        model.setSiteId(addParam.getSiteId());
        model.setMemberId(addParam.getMemberId());
        model.setGoodsId(addParam.getGoodsId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopGoodsCollectMapper.insert(model);
    }

    /**
     * 商品收藏记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, ShopGoodsCollectParam editParam) {
        ShopGoodsCollect model = shopGoodsCollectMapper.selectOne(
                new QueryWrapper<ShopGoodsCollect>()
                    .eq("id",  id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(id);
        
        model.setSiteId(editParam.getSiteId());
        model.setMemberId(editParam.getMemberId());
        model.setGoodsId(editParam.getGoodsId());
        shopGoodsCollectMapper.updateById(model);
    }

    /**
     * 商品收藏记录删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        ShopGoodsCollect model = shopGoodsCollectMapper.selectOne(
                new QueryWrapper<ShopGoodsCollect>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        shopGoodsCollectMapper.delete(new QueryWrapper<ShopGoodsCollect>().eq("id", id));
    }

}
