package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.goods.ShopGoodsCollect;
import com.niu.shop.mapper.goods.ShopGoodsCollectMapper;
import com.niu.shop.service.api.goods.IGoodsCollectService;
import com.niu.shop.service.api.goods.vo.ShopGoodsCollectVo;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IGoodsCollectServiceImpl implements IGoodsCollectService {

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    ShopGoodsCollectMapper shopGoodsCollectMapper;

    @Override
    public PageResult<ShopGoodsCollectVo> getMemberGoodsCollectList(PageParam pageParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoodsCollect> queryWrapper = new MPJQueryWrapper<>();

        queryWrapper.setAlias("gc")
                .leftJoin("?_shop_goods ng ON gc.goods_id = ng.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_shop_goods_sku ngs ON gc.goods_id = ngs.goods_id and ngs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("gc.member_id", RequestUtils.memberId());
        queryWrapper.eq("gc.site_id", RequestUtils.siteId());
        queryWrapper.select("gc.*", "ng.goods_name", "ng.goods_cover", "ngs.sku_name", "ngs.price");
        queryWrapper.orderByDesc("gc.id");

        IPage<ShopGoodsCollectVo> iPage = shopGoodsCollectMapper.selectJoinPage(new Page<>(page, limit), ShopGoodsCollectVo.class, queryWrapper);

        return PageResult.build(iPage);
    }

    @Override
    public void addGoodsCollect(Integer goodsId) {
        ShopGoodsCollect model = shopGoodsCollectMapper.selectOne(new QueryWrapper<ShopGoodsCollect>().eq("goods_id", goodsId).eq("member_id", RequestUtils.memberId()).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isNotEmpty(model)) throw new CommonException("商品已收藏");

        model = new ShopGoodsCollect();
        model.setGoodsId(goodsId);
        model.setMemberId(RequestUtils.memberId());
        model.setSiteId(RequestUtils.siteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopGoodsCollectMapper.insert(model);

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setGoodsId(goodsId);
        param.setSiteId(model.getSiteId());
        param.setCollectNum(1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void cancelGoodsCollect(Integer goodsId) {
        shopGoodsCollectMapper.delete(new QueryWrapper<ShopGoodsCollect>().eq("goods_id", goodsId).eq("member_id", RequestUtils.memberId()).eq("site_id", RequestUtils.siteId()));
        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setGoodsId(goodsId);
        param.setSiteId(RequestUtils.siteId());
        param.setCollectNum(-1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void cancelGoodsCollect(Integer[] goodsId) {
        for (Integer id : goodsId) {
            cancelGoodsCollect(id);
        }
    }
}
