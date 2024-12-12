package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.event.goods.AfterGoodsEditEvent;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.service.admin.goods.IShopGoodsService;
import com.niu.shop.service.admin.goods.IShopGoodsSkuService;
import com.niu.shop.service.admin.goods.IShopGoodsSpecService;
import com.niu.shop.service.admin.goods.IShopVirtualGoodsService;
import com.niu.shop.service.admin.goods.param.ShopVirtualGoodsParam;
import com.niu.shop.service.admin.goods.vo.ShopVirtualGoodsInitVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShopVirtualGoodsServiceImpl implements IShopVirtualGoodsService {

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    ShopGoodsSpecMapper shopGoodsSpecMapper;

    @Resource
    IShopGoodsSkuService shopGoodsSkuService;

    @Resource
    IShopGoodsSpecService shopGoodsSpecService;

    @Resource
    IShopGoodsService shopGoodsService;

    /**
     * 商品添加
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(ShopVirtualGoodsParam addParam) {
        Integer siteId = RequestUtils.siteId();

        // 添加goods表
        ShopGoods model = new ShopGoods();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(siteId);
        model.setGoodsCover(addParam.getGoodsImage().split(",")[0]);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(addParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(addParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(addParam.getServiceIds())).toString());
        shopGoodsMapper.insert(model);

        // 规格数据
        List<ShopGoodsSku> skuData = new LinkedList<>();
        if (addParam.getSpecType().equals("single")) {
            ShopGoodsSku sku = new ShopGoodsSku();
            BeanUtils.copyProperties(addParam, sku);
            sku.setSiteId(siteId);
            sku.setGoodsId(model.getGoodsId());
            sku.setSkuImage(model.getGoodsCover());
            sku.setSalePrice(addParam.getPrice());
            sku.setIsDefault(1);
            sku.setSkuSpecFormat("");
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key: addParam.getGoodsSkuData().keySet()) {
                ShopVirtualGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);

                List<String> skuSpecFormat = new LinkedList<>();
                for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                    JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                    skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                }

                ShopGoodsSku sku = new ShopGoodsSku();
                BeanUtils.copyProperties(skuParam, sku);
                sku.setSiteId(siteId);
                sku.setSkuName(skuParam.getSpecName());
                sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                sku.setSalePrice(skuParam.getPrice());
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                if (skuParam.getIsDefault().equals(1)) {
                    defaultCount++;
                    if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                }
                skuData.add(sku);
            }
            if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

            // 添加商品规格值
            List<ShopGoodsSpec> specData = new LinkedList<>();
            for (int i = 0; i < addParam.getGoodsSpecFormat().size(); i++) {
                JSONObject goodsSpec = addParam.getGoodsSpecFormat().getJSONObject(i);

                ShopGoodsSpec spec = new ShopGoodsSpec();
                spec.setGoodsId(model.getGoodsId());
                spec.setSiteId(siteId);
                spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                List<String> specValues = new LinkedList<>();
                for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                    JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                    specValues.add(value.getStr("spec_value_name", ""));
                }

                spec.setSpecValues(String.join(",", specValues));
                specData.add(spec);
            }
            shopGoodsSpecService.insertAll(specData);
        }

        shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("shop");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * int 数组转 str 数组
     * @param arr
     * @return
     */
    private String[] intArrToStrArr(Integer[] arr) {
        String[] strArr = new String[arr.length];

        for (int i = 0; i < arr.length; i++) {
            strArr[i] = String.valueOf(arr[i]);
        }
        return strArr;
    }

    /**
     * int 数组转 str 数组
     * @param arr
     * @return
     */
    private Integer[] strArrToIntArr(String[] arr) {
        Integer[] intArr = new Integer[arr.length];

        for (int i = 0; i < arr.length; i++) {
            intArr[i] = Integer.valueOf(arr[i]);
        }
        return intArr;
    }

    /**
     * 商品编辑
     *
     * @param editParam 参数
     */
    @Override
    @Transactional
    public void edit(Integer id, ShopVirtualGoodsParam editParam) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = shopGoodsService.getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0) throw new CommonException("商品正在参与营销活动，禁止修改");

        BeanUtils.copyProperties(editParam, model);
        model.setGoodsCover(editParam.getGoodsImage().split(",")[0]);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(editParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(editParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(editParam.getServiceIds())).toString());
        shopGoodsMapper.updateById(model);

        // 规格数据
        List<ShopGoodsSku> skuData = new LinkedList<>();
        if (editParam.getSpecType().equals("single")) {
            List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", id));
            if (skuList.size() > 1) {
                // 删除原来的多规格
                shopGoodsSkuMapper.delete(new QueryWrapper<ShopGoodsSku>().eq("goods_id", id));

                ShopGoodsSku sku = new ShopGoodsSku();
                BeanUtils.copyProperties(editParam, sku);
                sku.setSiteId(siteId);
                sku.setGoodsId(model.getGoodsId());
                sku.setSkuImage(model.getGoodsCover());
                sku.setSalePrice(editParam.getPrice());
                sku.setSkuSpecFormat("");
                sku.setIsDefault(1);
                skuData.add(sku);
            } else {
                ShopGoodsSku sku = skuList.get(0);
                sku.setSkuImage(model.getGoodsCover());
                sku.setSkuNo(editParam.getSkuNo());
                sku.setSkuName("");
                sku.setSkuSpecFormat("");
                sku.setCostPrice(editParam.getCostPrice());
                sku.setMarketPrice(editParam.getMarketPrice());
                sku.setIsDefault(1);
                if (activeGoodsCount == 0) {
                    sku.setPrice(editParam.getPrice());
                    sku.setSalePrice(editParam.getPrice());
                    sku.setStock(editParam.getStock());
                }
                shopGoodsSkuMapper.updateById(sku);
            }
            // 删除原规格值
            shopGoodsSpecMapper.delete(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", id));
        } else {
            if (editParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            ShopVirtualGoodsParam.SkuData firstSkuData = editParam.getGoodsSkuData().get(editParam.getGoodsSkuData().keySet().iterator().next());
            if (activeGoodsCount > 0 && firstSkuData.getSkuId() == null) {
                throw new CommonException("商品正在参与营销活动，禁止修改");
            }

            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key: editParam.getGoodsSkuData().keySet()) {
                    ShopVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    ShopGoodsSku sku = new ShopGoodsSku();
                    if (skuParam.getSkuId() != null) {
                        sku.setSkuId(skuParam.getSkuId());
                        skuIdArr.add(skuParam.getSkuId());
                    }
                    sku.setSiteId(siteId);
                    sku.setGoodsId(id);
                    sku.setSkuNo(skuParam.getSkuNo());
                    sku.setSkuName(skuParam.getSpecName());
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    sku.setCostPrice(skuParam.getCostPrice());
                    sku.setMarketPrice(skuParam.getMarketPrice());
                    sku.setIsDefault(skuParam.getIsDefault());
                    if (activeGoodsCount == 0) {
                        sku.setPrice(skuParam.getPrice());
                        sku.setSalePrice(skuParam.getPrice());
                        sku.setStock(skuParam.getStock());
                    }

                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 查询移除的规格
                Integer[] removeSkuIdArr = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", id)
                        .notIn("sku_id", skuIdArr).select("sku_id")
                ).stream().map(i -> i.getSkuId()).toArray(Integer[]::new);

                if (removeSkuIdArr.length > 0) {
                    if (shopGoodsService.getSkuTransactionCount(removeSkuIdArr) > 0) throw new CommonException("存在未完成的订单，不允许删除规格");
                    shopGoodsSkuMapper.delete(new QueryWrapper<ShopGoodsSku>().in("sku_id", Arrays.asList(removeSkuIdArr)));
                }

                // 添加商品规格值
                List<ShopGoodsSpec> specData = new LinkedList<>();
                List<Integer> specIdArr = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    ShopGoodsSpec spec = new ShopGoodsSpec();
                    if (goodsSpec.getInt("spec_id") != null) {
                        spec.setSpecId(goodsSpec.getInt("spec_id"));
                        specIdArr.add(goodsSpec.getInt("spec_id"));
                    }
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                // 删除移除的规格值
                if (specIdArr.size() > 0) shopGoodsSpecMapper.delete(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", id).notIn("spec_id", specIdArr));

                shopGoodsSpecService.insertAll(specData);
            } else {
                if (shopGoodsService.getGoodsTransactionCount(id) > 0) throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                shopGoodsSkuMapper.delete(new QueryWrapper<ShopGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                shopGoodsSpecMapper.delete(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key: editParam.getGoodsSkuData().keySet()) {
                    ShopVirtualGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

                    List<String> skuSpecFormat = new LinkedList<>();
                    for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                        JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                        skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                    }

                    ShopGoodsSku sku = new ShopGoodsSku();
                    BeanUtils.copyProperties(skuParam, sku);
                    sku.setSiteId(siteId);
                    sku.setSkuImage(skuParam.getSkuImage().isEmpty() ? model.getGoodsCover() : skuParam.getSkuImage());
                    sku.setSalePrice(skuParam.getPrice());
                    sku.setGoodsId(model.getGoodsId());
                    sku.setSkuSpecFormat(String.join(",", skuSpecFormat));
                    if (skuParam.getIsDefault().equals(1)) {
                        defaultCount++;
                        if (defaultCount > 1) throw new CommonException("商品不能有多个默认规格");
                    }
                    skuData.add(sku);
                }
                if (defaultCount.equals(0)) throw new CommonException("商品缺少默认规格");

                // 添加商品规格值
                List<ShopGoodsSpec> specData = new LinkedList<>();
                for (int i = 0; i < editParam.getGoodsSpecFormat().size(); i++) {
                    JSONObject goodsSpec = editParam.getGoodsSpecFormat().getJSONObject(i);

                    ShopGoodsSpec spec = new ShopGoodsSpec();
                    spec.setGoodsId(model.getGoodsId());
                    spec.setSiteId(siteId);
                    spec.setSpecName(goodsSpec.getStr("spec_name", ""));

                    List<String> specValues = new LinkedList<>();
                    for (int v = 0; v < goodsSpec.getJSONArray("values").size(); v++) {
                        JSONObject value = goodsSpec.getJSONArray("values").getJSONObject(v);
                        specValues.add(value.getStr("spec_value_name", ""));
                    }

                    spec.setSpecValues(String.join(",", specValues));
                    specData.add(spec);
                }
                shopGoodsSpecService.insertAll(specData);
            }
        }

        if (skuData.size() > 0) shopGoodsSkuService.insertAll(skuData);

        // 商品添加之后事件
        AfterGoodsEditEvent event = new AfterGoodsEditEvent();
        event.setSiteId(siteId);
        event.addAppSign("shop");
        event.setName("AfterGoodsEditEvent");
        event.setGoodsId(model.getGoodsId());
        event.setGoodsData(model);
        event.setSkuData(skuData);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    @Override
    public ShopVirtualGoodsInitVo getInit(Integer goodsId) {
        ShopVirtualGoodsInitVo vo = new ShopVirtualGoodsInitVo();
        if (goodsId > 0) {
            ShopGoods model = shopGoodsMapper.selectOne(
                    new QueryWrapper<ShopGoods>()
                            .eq("goods_id", goodsId)
                            .eq("site_id", RequestUtils.siteId())
                            .eq("delete_time", 0));
            if (model == null) return null;

            ShopVirtualGoodsInitVo.GoodsInfo goodsInfo = new ShopVirtualGoodsInitVo.GoodsInfo();
            BeanUtils.copyProperties(model, goodsInfo);
            goodsInfo.setGoodsCategory(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory()))));
            goodsInfo.setLabelIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getLabelIds()))));
            goodsInfo.setServiceIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getServiceIds()))));
            goodsInfo.setStatus(String.valueOf(model.getStatus()));

            List<ShopVirtualGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", model.getGoodsId())), ShopVirtualGoodsInitVo.SkuInfo.class);
            goodsInfo.setSkuList(skuList);

            List<ShopVirtualGoodsInitVo.SkuInfo> tempSkuList = skuList.stream().filter(i -> i.getSkuSpecFormat() != null && !i.getSkuSpecFormat().isEmpty()).collect(Collectors.toList());
            if (tempSkuList.size() > 0) {
                goodsInfo.setSpecType("multi");

                List<ShopVirtualGoodsInitVo.SpecInfo> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", model.getGoodsId())), ShopVirtualGoodsInitVo.SpecInfo.class);
                goodsInfo.setSpecList(specList);
            }

            if (model.getVirtualReceiveType().equals("verify")) {
                goodsInfo.setOrderGoodsCount(shopGoodsService.getGoodsTransactionCount(model.getGoodsId()));
            }

            if (model.getBrandId().equals(0)) goodsInfo.setBrandId("");
            if (model.getSupplierId().equals(0)) goodsInfo.setSupplierId("");
            if (model.getAttrId().equals(0)) goodsInfo.setAttrId("");
            if (model.getPosterId().equals(0)) goodsInfo.setPosterId("");

            goodsInfo.setActiveGoodsCount(shopGoodsService.getActiveGoodsCount(goodsId));

            vo.setGoodsInfo(goodsInfo);
        }
        return vo;
    }
}
