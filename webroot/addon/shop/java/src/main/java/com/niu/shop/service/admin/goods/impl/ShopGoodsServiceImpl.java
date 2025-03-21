package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
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
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.vo.MemberLevelInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.active.ShopActiveGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.goods.ShopGoodsSpec;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.active.ActiveGoodsTypeEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.event.goods.AfterGoodsEditEvent;
import com.niu.shop.mapper.active.ShopActiveGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.goods.ShopGoodsSpecMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.admin.goods.IShopGoodsService;
import com.niu.shop.service.admin.goods.IShopGoodsSkuService;
import com.niu.shop.service.admin.goods.IShopGoodsSpecService;
import com.niu.shop.service.admin.goods.param.*;
import com.niu.shop.service.admin.goods.vo.*;
import com.niu.shop.service.api.goods.IGoodsService;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.param.GetManjianInfoParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品实现
 */
@Service
public class ShopGoodsServiceImpl implements IShopGoodsService {

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
    ShopActiveGoodsMapper shopActiveGoodsMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreUploadService coreUploadService;

    @Resource
    ICoreManjianService coreManjianService;

    @Resource
    IMemberService memberService;

    @Resource
    IGoodsService goodsService;

    @Resource
    IMemberLevelService memberLevelService;

    @Resource
    ICoreGoodsLimitBuyService coreGoodsLimitBuyService;

    /**
     * 商品列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<ShopGoodsListVo> page(PageParam pageParam, ShopGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("delete_time", 0);
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getBrandId())) queryWrapper.eq("brand_id", searchParam.getBrandId());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getLabelIds()))
            queryWrapper.like("label_ids", "\"" + searchParam.getLabelIds() + "\"");
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        QueryMapperUtils.buildByBetween(queryWrapper, "g.sale_num", new Integer[]{searchParam.getStartSaleNum(), searchParam.getEndSaleNum()});
        QueryMapperUtils.buildByBetween(queryWrapper, "gs.price", new Double[]{searchParam.getStartPrice(), searchParam.getEndPrice()});

        if (ObjectUtil.isNotEmpty(searchParam.getOrder())) {
            queryWrapper.orderBy(true, searchParam.getSort().equals("asc"), searchParam.getOrder());
        } else {
            queryWrapper.orderByDesc("g.goods_id");
        }

        IPage<ShopGoodsListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopGoodsListVo.class, queryWrapper);
        List<ShopGoodsListVo> list = new LinkedList<>();
        for (ShopGoodsListVo vo : iPage.getRecords()) {
            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price")
                    .eq("goods_id", vo.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品详情
     *
     * @param id 主键
     * @return ShopGoodsInfoVo
     */
    @Override
    public ShopGoodsInfoVo info(Integer id) {
        ShopGoods model = shopGoodsMapper.selectOne(
                new QueryWrapper<ShopGoods>()
                        .eq("goods_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("delete_time", 0));
        if (model == null) return null;

        ShopGoodsInfoVo vo = new ShopGoodsInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品添加
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(ShopGoodsParam addParam) {
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
        model.setDeliveryType(JSONUtil.parseArray(addParam.getDeliveryType()).toString());
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
            sku.setSkuSpecFormat("");
            sku.setIsDefault(1);
            skuData.add(sku);
        } else {
            if (addParam.getGoodsSkuData().keySet().size() == 0) throw new CommonException("请添加商品规格");

            Integer defaultCount = 0;
            for (String key : addParam.getGoodsSkuData().keySet()) {
                ShopGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);

                List<String> skuSpecFormat = new LinkedList<>();
                for (int i = 0; i < skuParam.getSkuSpec().size(); i++) {
                    JSONObject spec = skuParam.getSkuSpec().getJSONObject(i);
                    skuSpecFormat.add(spec.getStr("spec_value_name", ""));
                }

                ShopGoodsSku sku = new ShopGoodsSku();
                BeanUtils.copyProperties(skuParam, sku);
                sku.setSkuName(skuParam.getSpecName());
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
     *
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
     *
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
    public void edit(Integer id, ShopGoodsParam editParam) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", id).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(id);
        // 如果有正在进行的活动不允许下架
        if (editParam.getStatus().equals(0) && activeGoodsCount > 0)
            throw new CommonException("商品正在参与营销活动，禁止修改");

        BeanUtils.copyProperties(editParam, model);
        model.setGoodsCover(editParam.getGoodsImage().split(",")[0]);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(editParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(editParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(editParam.getServiceIds())).toString());
        model.setDeliveryType(JSONUtil.parseArray(editParam.getDeliveryType()).toString());
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
                sku.setIsDefault(1);
                sku.setSkuSpecFormat("");
                skuData.add(sku);
            } else {
                ShopGoodsSku sku = skuList.get(0);
                sku.setSkuImage(model.getGoodsCover());
                sku.setSkuNo(editParam.getSkuNo());
                sku.setSkuName("");
                sku.setSkuSpecFormat("");
                sku.setWeight(editParam.getWeight());
                sku.setVolume(editParam.getVolume());
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

            ShopGoodsParam.SkuData firstSkuData = editParam.getGoodsSkuData().get(editParam.getGoodsSkuData().keySet().iterator().next());
            if (activeGoodsCount > 0 && firstSkuData.getSkuId() == null) {
                throw new CommonException("商品正在参与营销活动，禁止修改");
            }

            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    ShopGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

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
                    sku.setWeight(skuParam.getWeight());
                    sku.setVolume(skuParam.getVolume());
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
                    if (getSkuTransactionCount(removeSkuIdArr) > 0)
                        throw new CommonException("存在未完成的订单，不允许删除规格");
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
                if (specIdArr.size() > 0)
                    shopGoodsSpecMapper.delete(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", id).notIn("spec_id", specIdArr));

                shopGoodsSpecService.insertAll(specData);
            } else {
                if (getGoodsTransactionCount(id) > 0) throw new CommonException("存在未完成的订单，无法编辑商品");

                // 删除原来的规格
                shopGoodsSkuMapper.delete(new QueryWrapper<ShopGoodsSku>().eq("goods_id", id));
                // 删除原规格值
                shopGoodsSpecMapper.delete(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", id));

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    ShopGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);

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
     * 商品删除
     *
     * @param param
     */
    @Override
    public void del(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }
        if (getActiveGoodsCount(goodsIds) > 0) throw new CommonException("商品正在参与营销活动，禁止删除");

        ShopGoods model = new ShopGoods();
        model.setStatus(0);
        model.setDeleteTime(System.currentTimeMillis() / 1000);
        shopGoodsMapper.update(model, new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 恢复商品
     *
     * @param param
     */
    @Override
    public void recycle(GetGoodsIdsParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }

        ShopGoods model = new ShopGoods();
        model.setDeleteTime(Long.valueOf(0));
        shopGoodsMapper.update(model, new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 商品回收站
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    @Override
    public PageResult<ShopGoodsListVo> recyclePage(PageParam pageParam, ShopGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("goods_id,site_id,goods_name,goods_type,goods_cover,stock,sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.ne("delete_time", 0);

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("goods_name", searchParam.getGoodsName());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
        queryWrapper.orderByDesc("goods_id");

        IPage<ShopGoods> iPage = shopGoodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsListVo> list = new LinkedList<>();
        for (ShopGoods item : iPage.getRecords()) {
            ShopGoodsListVo vo = new ShopGoodsListVo();
            BeanUtils.copyProperties(item, vo);

            ShopGoodsSku goodsSku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>()
                    .select("sku_id", "goods_id", "price", "member_price")
                    .eq("goods_id", item.getGoodsId()).eq("is_default", 1));
            ShopGoodsListVo.GoodsSku goodsSkuVo = new ShopGoodsListVo.GoodsSku();
            BeanUtils.copyProperties(goodsSku, goodsSkuVo);
            vo.setGoodsSku(goodsSkuVo);
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), item.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 修改商品上下架状态
     *
     * @param param
     */
    @Override
    public void editStatus(EditGoodsStatusParam param) {
        Integer[] goodsIds = null;
        if (param.getGoodsIds() instanceof Integer) {
            goodsIds = new Integer[]{(Integer) param.getGoodsIds()};
        } else if (param.getGoodsIds() instanceof ArrayList) {
            goodsIds = ((ArrayList<?>) param.getGoodsIds()).toArray(new Integer[0]);
        }

        if (param.getStatus().equals(0) && getActiveGoodsCount(goodsIds) > 0) {
            throw new CommonException("商品正在参与营销活动，禁止下架");
        }
        ShopGoods model = new ShopGoods();
        model.setStatus(param.getStatus());
        shopGoodsMapper.update(model, new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId()).in("goods_id", goodsIds));
    }

    /**
     * 修改商品排序
     *
     * @param param
     */
    @Override
    public void editSort(EditGoodsSortParam param) {
        ShopGoods model = new ShopGoods();
        model.setSort(param.getSort());
        shopGoodsMapper.update(model, new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId()).eq("goods_id", param.getGoodsId()));
    }

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    @Override
    public ShopGoodsInitVo getInit(Integer goodsId) {
        ShopGoodsInitVo vo = new ShopGoodsInitVo();
        if (goodsId > 0) {
            ShopGoods model = shopGoodsMapper.selectOne(
                    new QueryWrapper<ShopGoods>()
                            .eq("goods_id", goodsId)
                            .eq("site_id", RequestUtils.siteId())
                            .eq("delete_time", 0));
            if (model == null) return null;

            ShopGoodsInitVo.GoodsInfo goodsInfo = new ShopGoodsInitVo.GoodsInfo();
            BeanUtils.copyProperties(model, goodsInfo);
            goodsInfo.setGoodsCategory(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getGoodsCategory()))));
            goodsInfo.setLabelIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getLabelIds()))));
            goodsInfo.setServiceIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getServiceIds()))));
            goodsInfo.setStatus(String.valueOf(model.getStatus()));

            List<ShopGoodsInitVo.SkuInfo> skuList = CollectUtils.convert(shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", model.getGoodsId())), ShopGoodsInitVo.SkuInfo.class);
            goodsInfo.setSkuList(skuList);

            List<ShopGoodsInitVo.SkuInfo> tempSkuList = skuList.stream().filter(i -> i.getSkuSpecFormat() != null && !i.getSkuSpecFormat().isEmpty()).collect(Collectors.toList());
            if (tempSkuList.size() > 0) {
                goodsInfo.setSpecType("multi");

                List<ShopGoodsInitVo.SpecInfo> specList = CollectUtils.convert(shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", model.getGoodsId())), ShopGoodsInitVo.SpecInfo.class);
                goodsInfo.setSpecList(specList);
            }

            if (model.getBrandId().equals(0)) goodsInfo.setBrandId("");
            if (model.getSupplierId().equals(0)) goodsInfo.setSupplierId("");
            if (model.getAttrId().equals(0)) goodsInfo.setAttrId("");
            if (model.getPosterId().equals(0)) goodsInfo.setPosterId("");

            goodsInfo.setActiveGoodsCount(getActiveGoodsCount(goodsId));

            vo.setGoodsInfo(goodsInfo);
        }
        return vo;
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getGoodsTransactionCount(Integer goodsId) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<ShopOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_shop_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("og.goods_id", goodsId);
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return shopOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品正在交易中的数量
     *
     * @return
     */
    public Long getSkuTransactionCount(Integer[] skuIds) {
        Integer[] orderStatus = {1, 2, 3};
        MPJQueryWrapper<ShopOrderGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("og")
                .leftJoin("?_shop_order o ON og.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.in("og.sku_id", Arrays.asList(skuIds));
        queryWrapper.in("o.status", Arrays.asList(orderStatus));
        return shopOrderGoodsMapper.selectJoinCount(queryWrapper);
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer goodsId) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return shopActiveGoodsMapper.selectCount(new QueryWrapper<ShopActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .eq("goods_id", goodsId)
        );
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer[] goodsIds) {
        String[] activeType = {ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType()};
        return shopActiveGoodsMapper.selectCount(new QueryWrapper<ShopActiveGoods>()
                .eq("site_id", RequestUtils.siteId())
                .eq("active_goods_status", "active")
                .in("active_goods_type", Arrays.asList(activeType))
                .in("goods_id", Arrays.asList(goodsIds))
        );
    }

    /**
     * 复制商品
     *
     * @param goodsId
     */
    @Override
    @Transactional
    public void copy(Integer goodsId) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", goodsId).eq("site_id", siteId).eq("delete_time", 0));
        Assert.notNull(model, "商品不存在");

        model.setGoodsId(null);
        model.setGoodsName(model.getGoodsName() + "_副本");
        model.setSort(0);
        model.setStatus(0);
        model.setSaleNum(0);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        shopGoodsMapper.insert(model);

        // 复制sku
        List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", goodsId));
        for (ShopGoodsSku item : skuList) {
            item.setSkuId(null);
            item.setGoodsId(model.getGoodsId());
            item.setSaleNum(0);
        }
        shopGoodsSkuService.insertAll(skuList);

        // 复制规格
        List<ShopGoodsSpec> specList = shopGoodsSpecMapper.selectList(new QueryWrapper<ShopGoodsSpec>().eq("goods_id", goodsId));
        for (ShopGoodsSpec item : specList) {
            item.setSpecId(null);
            item.setGoodsId(model.getGoodsId());
        }
        shopGoodsSpecService.insertAll(specList);
    }

    /**
     * 商品选择分页列表
     *
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public ShopSelectGoodsListVo getSelectPage(PageParam pageParam, ShopGoodsSelectSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        // 查询已选的
        if (ObjectUtil.isNotEmpty(searchParam.getSkuIds())) {
            List<Integer> goodsIds = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().in("sku_id", searchParam.getSkuIds()).groupBy("goods_id").select("sku_id"))
                    .stream().map(i -> i.getSkuId()).collect(Collectors.toList());
            if (goodsIds.size() > 0 && ObjectUtil.isEmpty(searchParam.getGoodsIds())) searchParam.setGoodsIds(goodsIds);
        }

        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("delete_time", 0);

        if (ObjectUtil.isNotEmpty(searchParam.getIsGift()) && searchParam.getIsGift() == 1) {
            queryWrapper.ge("g.is_gift", 0);
        } else {
            queryWrapper.eq("g.is_gift", 0);
        }

        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("goods_name", searchParam.getKeyword()).or().like("sub_title", searchParam.getKeyword()));
        }

        if (ObjectUtil.isNotEmpty(searchParam.getSkuIds())) {
            List<ShopGoodsSku> selectGoodsList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().select("goods_id").in("sku_id", searchParam.getSkuIds()).groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(selectGoodsList)) {
                List<Integer> goodsIds = selectGoodsList.stream().map(i -> i.getGoodsId()).toList();
                if (ObjectUtil.isEmpty(searchParam.getGoodsIds())) searchParam.setGoodsIds(goodsIds);
            }
        }

        if (searchParam.getSelectType().equals("all")) {
            queryWrapper.gt("g.stock", 0);
            queryWrapper.eq("g.status", 1);
        }

        if (searchParam.getSelectType().equals("selected") && ObjectUtil.isNotEmpty(searchParam.getGoodsIds())) {
            queryWrapper.in("g.goods_id", searchParam.getGoodsIds());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
            queryWrapper.eq("goods_type", searchParam.getGoodsType());

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");

        List<Integer> verifyGoodsIds = new LinkedList<>();
        List<Integer> verifySkuIds = new LinkedList<>();

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<ShopGoods> verifyGoodsList = shopGoodsMapper.selectList(new QueryWrapper<ShopGoods>().select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds()).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0).eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<ShopGoodsSku> verifySkuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("sku_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                verifySkuIds = verifySkuList.stream().map(i -> i.getSkuId()).collect(Collectors.toList());
            }
        }

        queryWrapper.orderByDesc("g.goods_id");

        IPage<ShopSelectGoodsListVo.ListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopSelectGoodsListVo.ListVo.class, queryWrapper);
        List<ShopSelectGoodsListVo.ListVo> list = new LinkedList<>();
        for (ShopSelectGoodsListVo.ListVo vo : iPage.getRecords()) {
            vo.setGoodsImage(vo.getGoodsImage());

            List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", vo.getGoodsId()));
            vo.setSkuList(skuList);

            for (ShopGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    ShopSelectGoodsListVo.GoodsSku goodsSkuVo = new ShopSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            list.add(vo);
        }
        PageResult pageResutl = PageResult.build(page, limit, iPage.getTotal()).setData(list);

        ShopSelectGoodsListVo vo = new ShopSelectGoodsListVo();
        BeanUtils.copyProperties(pageResutl, vo);

        vo.setVerifyGoodsIds(verifyGoodsIds);
        vo.setVerifySkuIds(verifySkuIds);

        return vo;
    }

    /**
     * 获取商品选择列表带ku
     *
     * @param searchParam
     */
    @Override
    public List<ShopSelectGoodsListVo.ListVo> getSelectGoodsSku(ShopGoodsSelectSearchParam searchParam) {
        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("g")
                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
        queryWrapper.eq("g.site_id", RequestUtils.siteId());
        queryWrapper.eq("g.status", 1);
        queryWrapper.eq("delete_time", 0);

        if (ObjectUtil.isNotEmpty(searchParam.getIsGift()) && searchParam.getIsGift() == 1) {
            queryWrapper.ge("g.is_gift", 0);
        } else {
            queryWrapper.eq("g.is_gift", 0);
        }

        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
            List<ShopGoods> verifyGoodsList = shopGoodsMapper.selectList(new QueryWrapper<ShopGoods>().select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds()).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0).eq("status", 1));
            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
                List<Integer> verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
            List<ShopGoodsSku> verifySkuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("goods_id").groupBy("goods_id"));
            if (ObjectUtil.isNotEmpty(verifySkuList)) {
                List<Integer> verifyGoodsIds = verifySkuList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
                queryWrapper.in("g.goods_id", verifyGoodsIds);
            }
        }

        List<ShopSelectGoodsListVo.ListVo> list = shopGoodsMapper.selectJoinList(ShopSelectGoodsListVo.ListVo.class, queryWrapper);
        for (ShopSelectGoodsListVo.ListVo vo : list) {
            vo.setGoodsImage(vo.getGoodsImage());

            List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", vo.getGoodsId()));
            vo.setSkuList(skuList);

            for (ShopGoodsSku item : skuList) {
                if (item.getIsDefault().equals(1)) {
                    ShopSelectGoodsListVo.GoodsSku goodsSkuVo = new ShopSelectGoodsListVo.GoodsSku();
                    BeanUtils.copyProperties(item, goodsSkuVo);
                    vo.setGoodsSku(goodsSkuVo);
                }
            }
            vo.setGoodsCoverThumbSmall(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
            vo.setGoodsCoverThumbMid(coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
        }
        return list;
    }

    /**
     * 查询商品SKU规格列表
     *
     * @param goodsId
     * @return
     */
    @Override
    public List<ShopGoodsSkuListVo> getSkuList(Integer goodsId) {
        return CollectUtils.convert(shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", goodsId).eq("site_id", RequestUtils.siteId())), ShopGoodsSkuListVo.class);
    }

    /**
     * 编辑商品规格列表库存
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListStock(EditGoodsListStockParam param) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<ShopGoodsSku> skuList = new LinkedList<>();
        Integer stock = 0;
        for (EditGoodsListStockParam.Sku item : param.getSkuList()) {
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setStock(item.getStock());
            skuList.add(sku);
            stock += item.getStock();
        }

        shopGoodsSkuService.insertAll(skuList);

        model.setStock(stock);
        shopGoodsMapper.updateById(model);
    }

    /**
     * 编辑商品规格列表价格
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListPrice(EditGoodsListPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        Long activeGoodsCount = getActiveGoodsCount(param.getGoodsId());

        List<ShopGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListPriceParam.Sku item : param.getSkuList()) {
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setCostPrice(item.getCostPrice());
            sku.setMarketPrice(item.getMarketPrice());
            if (activeGoodsCount == 0) {
                sku.setPrice(item.getPrice());
                sku.setSalePrice(item.getPrice());
            }
            skuList.add(sku);
        }

        shopGoodsSkuService.insertAll(skuList);
    }

    /**
     * 编辑商品规格列表会员价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editGoodsListMemberPrice(EditGoodsListMemberPriceParam param) {
        Integer siteId = RequestUtils.siteId();
        ShopGoods model = shopGoodsMapper.selectOne(new QueryWrapper<ShopGoods>().eq("goods_id", param.getGoodsId()).eq("site_id", siteId).select("goods_id"));
        Assert.notNull(model, "商品不存在");

        List<ShopGoodsSku> skuList = new LinkedList<>();
        for (EditGoodsListMemberPriceParam.Sku item : param.getSkuList()) {
            ShopGoodsSku sku = shopGoodsSkuMapper.selectOne(new QueryWrapper<ShopGoodsSku>().eq("sku_id", item.getSkuId()).eq("goods_id", param.getGoodsId()).select("sku_id"));
            Assert.notNull(sku, "商品规格不存在");
            sku.setMemberPrice(item.getMemberPrice().toString());
            skuList.add(sku);
        }

        shopGoodsSkuService.insertAll(skuList);

        model.setMemberDiscount(param.getMemberDiscount());
        shopGoodsMapper.updateById(model);
    }

    @Override
    public PageResult<GetBuyGoodsSelectVo> getBuyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", RequestUtils.siteId())
                .gt("stock", 0)
                .eq("status", 1)
                .eq("is_gift", 0)
                .orderByDesc("sort", "create_time");

        if (ObjectUtil.isNotEmpty(param.getGoodsCategory())) {
            goodsQueryWrapper.like("goods_category", param.getGoodsCategory());
        }

        if (ObjectUtil.isNotEmpty(param.getGoodsType())) {
            goodsQueryWrapper.eq("goods_type", param.getGoodsType());
        }

        if (ObjectUtil.isNotEmpty(param.getKeyword())) {
            goodsQueryWrapper.and(wrapper -> wrapper
                    .like("goods_name", param.getKeyword())
                    .or()
                    .like("sub_title", param.getKeyword()));
        }

        Page<ShopGoods> iPage = shopGoodsMapper.selectPage(new Page<>(page, limit), goodsQueryWrapper);

        Set<Integer> goodsIds = CollStreamUtil.toSet(iPage.getRecords(), ShopGoods::getGoodsId);
        Map<Integer, List<ShopGoodsSku>> skuMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            QueryWrapper<ShopGoodsSku> skuQueryWrapper = new QueryWrapper<>();
            skuQueryWrapper.in("goods_id", goodsIds);
            skuMap.putAll(shopGoodsSkuMapper.selectList(skuQueryWrapper)
                    .stream().collect(Collectors.groupingBy(ShopGoodsSku::getGoodsId)));
        }

        IPage<GetBuyGoodsSelectVo> convert = iPage.convert(o -> {
            GetBuyGoodsSelectVo vo = new GetBuyGoodsSelectVo();
            BeanUtils.copyProperties(o, vo);
            vo.setGoodsTypeName(GoodsTypeEnum.getNameByType(vo.getGoodsType()));

            String mid = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
            vo.setGoodsCoverThumbMid(mid);
            String small = coreUploadService.thumb(RequestUtils.siteId(), vo.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
            vo.setGoodsCoverThumbSmall(small);

            List<ShopGoodsSku> skus = skuMap.get(vo.getGoodsId());
            if (ObjectUtil.isNotEmpty(skus)) {
                ShopGoodsSku sku = skus.get(0);
                MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
                JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                BigDecimal price = goodsService.calculateMemberPrice(memberLevel, vo.getMemberDiscount(), memberPriceConfig, sku.getPrice());
                sku.setMemberPrice(price.toString());
                vo.setGoodsSku(sku);

                GetManjianInfoParam manjianInfoParam = new GetManjianInfoParam();
                manjianInfoParam.setGoodsId(o.getGoodsId());
                manjianInfoParam.setSkuId(sku.getSkuId());
                manjianInfoParam.setMemberId(param.getMemberId());
                manjianInfoParam.setSiteId(RequestUtils.siteId());

                ShopManjianRuleContentVo manjianInfo = coreManjianService.getManjianInfo(manjianInfoParam);
                vo.setManjianInfo(manjianInfo);
            }

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

            return vo;
        });

        return PageResult.build(convert);
    }

    @Override
    public GetBuySkuSelectVo getBuySkuSelect(BuySkuSelectParam param) {
        QueryWrapper<ShopGoodsSku> goodsSkuQueryWrapper = new QueryWrapper<>();
        goodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .eq("sku_id", param.getSkuId());
        ShopGoodsSku shopGoodsSku = shopGoodsSkuMapper.selectOne(goodsSkuQueryWrapper);
        if (shopGoodsSku == null) {
            return null;
        }

        GetBuySkuSelectVo vo = new GetBuySkuSelectVo();
        BeanUtils.copyProperties(shopGoodsSku, vo);

        String small = coreUploadService.thumb(RequestUtils.siteId(), shopGoodsSku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
        String mid = coreUploadService.thumb(RequestUtils.siteId(), shopGoodsSku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
        String big = coreUploadService.thumb(RequestUtils.siteId(), shopGoodsSku.getSkuImage(), UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType());

        vo.setSkuImageThumbSmall(small);
        vo.setSkuImageThumbMid(mid);
        vo.setSkuImageThumbBig(big);

        ShopGoods goods = shopGoodsMapper.selectById(vo.getGoodsId());
        GetBuySkuSelectVo.GoodsDTO goodsDTO = new GetBuySkuSelectVo.GoodsDTO();
        vo.setGoods(goodsDTO);

        QueryWrapper<ShopGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        vo.setSkuList(shopGoodsSkus);

        QueryWrapper<ShopGoodsSpec> shopGoodsSpecQueryWrapper = new QueryWrapper<>();
        shopGoodsSpecQueryWrapper.eq("goods_id", goods.getGoodsId());
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecMapper.selectList(shopGoodsSpecQueryWrapper);
        vo.setGoodsSpec(shopGoodsSpecs);

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(RequestUtils.memberId());
            JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(shopGoodsSku.getMemberPrice()) ? JSONUtil.parseObj(shopGoodsSku.getMemberPrice()) : new JSONObject();
            BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, goods.getMemberDiscount(), memberPriceConfig, shopGoodsSku.getPrice());

            vo.setMemberPrice(memberPrice.toString());
            vo.setSkuList(getMemberPriceByList(memberLevel, goods.getMemberDiscount(), vo.getSkuList()));

            Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), vo.getGoodsId());
            vo.setHasBuy(hasBuy);

            GetManjianInfoParam manjianInfoParam = new GetManjianInfoParam();
            manjianInfoParam.setGoodsId(vo.getGoodsId());
            manjianInfoParam.setSkuId(vo.getSkuId());
            manjianInfoParam.setMemberId(param.getMemberId());
            manjianInfoParam.setSiteId(RequestUtils.siteId());
            ShopManjianRuleContentVo manjianInfo = coreManjianService.getManjianInfo(manjianInfoParam);
            vo.setManjianInfo(manjianInfo);
        }
        return vo;
    }

    @Override
    public List<GetBuyGoodsSelectedVo> getBuyGoodsSelected(BuyGoodsSelectedParam param) {
        if (ObjectUtil.isEmpty(param.getSkuIds())) {
            return Collections.emptyList();
        }
        
        QueryWrapper<ShopGoodsSku> shopGoodsSkuQueryWrapper = new QueryWrapper<>();
        shopGoodsSkuQueryWrapper.eq("site_id", RequestUtils.siteId())
                .in("sku_id", param.getSkuIds());
        List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectList(shopGoodsSkuQueryWrapper);
        if (ObjectUtil.isEmpty(shopGoodsSkus)) {
            return Collections.emptyList();
        }

        Set<Integer> goodsIds = CollStreamUtil.toSet(shopGoodsSkus, ShopGoodsSku::getGoodsId);
        Map<Integer, ShopGoods> goodsMap = new HashMap<>(goodsIds.size(), 1);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsMap.putAll(shopGoodsMapper.selectBatchIds(goodsIds).stream().collect(Collectors.toMap(ShopGoods::getGoodsId, o -> o)));
        }

        List<GetBuyGoodsSelectedVo> skuList = shopGoodsSkus.stream()
                .map(sku -> {
                    GetBuyGoodsSelectedVo vo = new GetBuyGoodsSelectedVo();
                    BeanUtils.copyProperties(sku, vo);
                    vo.setGoods(goodsMap.get(vo.getGoodsId()));

                    String small = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType());
                    vo.setGoodsCoverThumbMid(small);
                    String mid = coreUploadService.thumb(RequestUtils.siteId(), sku.getSkuImage(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType());
                    vo.setGoodsCoverThumbMid(mid);
                    return vo;
                })
                .toList();

        if (ObjectUtil.isNotEmpty(param.getMemberId())) {
            MemberLevelInfoVo memberLevel = memberLevelService.findMemberLevel(param.getMemberId());

            for (GetBuyGoodsSelectedVo sku : skuList) {
                if (ObjectUtil.isNotEmpty(sku.getGoods())) {
                    JSONObject memberPriceConfig = ObjectUtil.isNotEmpty(sku.getMemberPrice()) ? JSONUtil.parseObj(sku.getMemberPrice()) : new JSONObject();
                    BigDecimal memberPrice = goodsService.calculateMemberPrice(memberLevel, sku.getGoods().getMemberDiscount(), memberPriceConfig, sku.getPrice());
                    sku.setMemberPrice(memberPrice.toString());
                }

                Integer hasBuy = coreGoodsLimitBuyService.getGoodsHasBuyNumber(RequestUtils.siteId(), param.getMemberId(), sku.getGoodsId());
                sku.setHasBuy(hasBuy);

                GetManjianInfoParam manjianInfoParam = new GetManjianInfoParam();
                manjianInfoParam.setGoodsId(sku.getGoodsId());
                manjianInfoParam.setSkuId(sku.getSkuId());
                manjianInfoParam.setMemberId(param.getMemberId());
                manjianInfoParam.setSiteId(RequestUtils.siteId());
                ShopManjianRuleContentVo manjianInfo = coreManjianService.getManjianInfo(manjianInfoParam);
                sku.setManjianInfo(manjianInfo);
            }
        }

        return skuList;
    }

    private List<ShopGoodsSku> getMemberPriceByList(MemberLevelInfoVo memberLevelInfoVo, String memberDiscount, List<ShopGoodsSku> skuList) {
        boolean isDefault = ObjectUtil.hasEmpty(memberDiscount, memberLevelInfoVo);
        for (ShopGoodsSku sku : skuList) {
            if (isDefault) {
                sku.setMemberPrice(sku.getPrice().toString());
                continue;
            }

            BigDecimal memberPrice = switch (memberDiscount) {
                case "discount" -> {
                    if (ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits()) || ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount")) ||
                            ObjectUtil.isEmpty(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").get("is_use"))) {
                        yield sku.getPrice();
                    }

                    yield sku.getPrice().multiply(memberLevelInfoVo.getLevelBenefits().getJSONObject("discount").getBigDecimal("discount").divide(BigDecimal.TEN));
                }
                case "fixed_price" -> {
                    if (ObjectUtil.isEmpty(sku.getMemberPrice())) {
                        throw new RuntimeException("价格计算失败");
                    }

                    JSONObject mp = JSONUtil.parseObj(sku.getMemberPrice());
                    BigDecimal levelPrice = mp.getBigDecimal("level" + memberLevelInfoVo.getLevelName());
                    yield ObjectUtil.isEmpty(levelPrice) ? sku.getPrice() : levelPrice;
                }
                default -> throw new RuntimeException("价格计算失败");
            };

            sku.setMemberPrice(memberPrice.toString());
        }
        return skuList;
    }

}
