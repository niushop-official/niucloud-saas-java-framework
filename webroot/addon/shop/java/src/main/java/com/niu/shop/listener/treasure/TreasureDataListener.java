package com.niu.shop.listener.treasure;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@EventCallback("shop")
@Component
public class TreasureDataListener extends CommonEventDefiner {
    private ShopGoodsMapper shopGoodsMapper;
    private ICoreUploadService coreUploadService;
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEventDefiner.CommonEvent event) {

        if(event.getEventName().equals("RechargeGiftContent"))
        {
            CommonEventDefiner.CommonEventResult commonResult = new CommonEventDefiner.CommonEventResult();
            JSONObject data = JSONUtil.parseObj(event.getData());
        }
        return null;
    }
   /* @Override
    public TreasureDataEventResult handleCallback(TreasureDataEvent event) {
        GetTreasureSearchParam param = event.getParam();
        if (!"shop".equals(param.getRelateType())) {
            return new TreasureDataEventResult();
        }

        QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", param.getSiteId())
                .eq("status", 1)
                .eq("is_gift", 0)
                .orderByAsc("sort")
                .orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            goodsQueryWrapper.and(wrapper -> wrapper.like("goods_name", param.getKeyword()).or().like("sub_title", param.getKeyword()));
        }

        if (ObjectUtil.isNotEmpty(param.getRelateIds())) {
            goodsQueryWrapper.in("goods_id", param.getRelateIds());
        }

        if (ObjectUtil.isNotEmpty(param.getJoinIds())) {
            goodsQueryWrapper.notIn("goods_id", param.getJoinIds());
        }

        PageResult<ShopGoods> pageResult;
        PageParam pageParam = event.getPageParam();
        if (ObjectUtil.isNotEmpty(pageParam) && pageParam.getPage() != 0 && pageParam.getLimit() != 0) {
            IPage<ShopGoods> iPage = shopGoodsMapper.selectPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), goodsQueryWrapper);
            pageResult = PageResult.build(iPage);
        } else {
            List<ShopGoods> shopGoods = shopGoodsMapper.selectList(goodsQueryWrapper);
            pageResult = PageResult.build(1, 1, shopGoods.size(), shopGoods);
        }

        if (ObjectUtil.isEmpty(pageResult.getData())) {
            return new TreasureDataEventResult();
        }

        Set<Integer> goodsIds = CollStreamUtil.toSet(pageResult.getData(), ShopGoods::getGoodsId);
        QueryWrapper<ShopGoodsSku> skuQueryWrapper = new QueryWrapper<>();
        skuQueryWrapper.in("goods_id", goodsIds);
        Map<Integer, List<ShopGoodsSku>> skuMap = shopGoodsSkuMapper.selectList(skuQueryWrapper)
                .stream()
                .collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId));

        List<GetTreasureSelectListVo> list = pageResult.getData().stream()
                .map(good -> {
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), good.getGoodsCover(), UploadThumbTypeEnum.MID.getType())
                            .getDataMap()
                            .get(UploadThumbTypeEnum.MID.getType());

                    GetTreasureSelectListVo vo = new GetTreasureSelectListVo();
                    vo.setSiteId(good.getSiteId());
                    vo.setTreasureName(good.getGoodsName());
                    vo.setTreasureSubName(good.getSubTitle());
                    vo.setTreasureImage(ObjectUtil.isEmpty(mid) ? good.getGoodsCover() : mid);
                    vo.setTreasurePrice(Optional.ofNullable(skuMap.get(good.getGoodsId()))
                            .map(l -> l.get(0))
                            .map(ShopGoodsSku::getPrice)
                            .orElse(null));
                    vo.setTreasureUrl("/addon/shop/pages/goods/detail?goods_id=" + good.getGoodsId());
                    vo.setRelateId(good.getGoodsId());
                    vo.setRelateType("shop");

                    return vo;
                })
                .toList();

        TreasureDataEventResult result = new TreasureDataEventResult();
        result.setResult(PageResult.build(pageResult.getCurrentPage(), pageResult.getPerPage(), pageResult.getTotal(), list));
        return result;
    }

    @Autowired
    public void setShopGoodsMapper(ShopGoodsMapper shopGoodsMapper) {
        this.shopGoodsMapper = shopGoodsMapper;
    }

    @Autowired
    public void setCoreUploadService(ICoreUploadService coreUploadService) {
        this.coreUploadService = coreUploadService;
    }

    @Autowired
    public void setShopGoodsSkuMapper(ShopGoodsSkuMapper shopGoodsSkuMapper) {
        this.shopGoodsSkuMapper = shopGoodsSkuMapper;
    }*/


}
