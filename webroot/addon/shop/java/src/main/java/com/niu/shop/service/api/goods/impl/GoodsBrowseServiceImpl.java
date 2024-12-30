package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsBrowse;
import com.niu.shop.entity.goods.ShopGoodsCollect;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsBrowseMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.service.api.goods.IGoodsBrowseService;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.api.goods.param.GoodsBrowseDeleteParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseParam;
import com.niu.shop.service.api.goods.param.GoodsBrowseSearchParam;
import com.niu.shop.service.api.goods.vo.GoodsBrowseListVo;
import com.niu.shop.service.core.goods.ICoreGoodsStatService;
import com.niu.shop.service.core.goods.param.GoodsAddStatParam;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;

@Service
public class GoodsBrowseServiceImpl implements IGoodsBrowseService {

    @Resource
    ShopGoodsBrowseMapper shopGoodsBrowseMapper;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    IGoodsService goodsService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Override
    public PageResult<GoodsBrowseListVo> page(PageParam pageParam, GoodsBrowseSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());

        MPJQueryWrapper<ShopGoodsCollect> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("b")
                .leftJoin("?_shop_goods g ON b.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_shop_goods_sku gs ON b.sku_id = gs.sku_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("b.*,gs.price,gs.price,gs.market_price,gs.member_price,g.member_discount");
        queryWrapper.eq("b.site_id", RequestUtils.siteId());
        queryWrapper.eq("b.member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getDate()))
            QueryMapperUtils.buildByTime(queryWrapper, "b.browse_time", searchParam.getDate());
        queryWrapper.orderByDesc("b.browse_time");
        IPage<GoodsBrowseListVo> iPage = shopGoodsBrowseMapper.selectJoinPage(new Page<>(page, limit), GoodsBrowseListVo.class, queryWrapper);

        for (GoodsBrowseListVo vo : iPage.getRecords()) {
            if (memberLevel != null) {
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(vo.getMemberPrice()) ? JSONUtil.parseObj(vo.getMemberPrice()) : new JSONObject();
                vo.setMemberPrice(goodsService.calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, vo.getPrice()));
            }
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public void add(GoodsBrowseParam addParam) {
        ShopGoods goods = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().select("goods_name,goods_cover").eq("site_id", RequestUtils.siteId()).eq("goods_id", addParam.getGoodsId()));
        Assert.notNull(goods, "商品不存在");

        ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("goods_id", addParam.getGoodsId()).eq("is_default", 1));

        ShopGoodsBrowse browse = shopGoodsBrowseMapper.selectOne(new QueryWrapper<ShopGoodsBrowse>()
                .eq("member_id", RequestUtils.memberId())
                .eq("site_id", RequestUtils.siteId())
                .eq("goods_id", addParam.getGoodsId())
        );

        ShopGoodsBrowse model = new ShopGoodsBrowse();
        model.setGoodsId(addParam.getGoodsId());
        model.setBrowseTime(System.currentTimeMillis() / 1000);
        model.setSiteId(RequestUtils.siteId());
        model.setMemberId(RequestUtils.memberId());
        model.setGoodsName(goods.getGoodsName());
        model.setGoodsCover(goods.getGoodsCover());
        model.setSkuId(sku.getSkuId());

        if (browse == null) {
            shopGoodsBrowseMapper.insert(model);

            GoodsAddStatParam param = new GoodsAddStatParam();
            param.setSiteId(model.getSiteId());
            param.setGoodsId(addParam.getGoodsId());
            param.setGoodsVisitMemberCount(1);
            coreGoodsStatService.addStat(param);
        } else {
            model.setId(browse.getId());
            shopGoodsBrowseMapper.updateById(model);
        }
    }

    @Override
    public void del(GoodsBrowseDeleteParam param) {
        QueryWrapper queryWrapper = new QueryWrapper<ShopGoodsBrowse>()
                .eq("site_id", RequestUtils.siteId())
                .eq("member_id", RequestUtils.memberId());

        if (param.getGoodsIds() instanceof Integer) {
            queryWrapper.eq("goods_id", param.getGoodsIds());
        } else if (param.getGoodsIds() instanceof ArrayList) {
            Integer[] goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
            queryWrapper.in("goods_id", goodsIds);
        }

        shopGoodsBrowseMapper.delete(queryWrapper);
    }
}
