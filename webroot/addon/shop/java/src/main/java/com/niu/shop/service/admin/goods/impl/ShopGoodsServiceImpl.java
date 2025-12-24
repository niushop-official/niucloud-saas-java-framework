package com.niu.shop.service.admin.goods.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.shop.enums.goods.GoodsBatchSetEnum;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
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
import com.niu.shop.enums.goods.GoodsBatchSetEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.event.goods.AfterGoodsEditEvent;
import com.niu.shop.event.marketing.GoodsIsConnectedCardEventDefiner;
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
import com.niu.shop.service.core.goods.ICoreGoodsConfigService;
import com.niu.shop.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.marketing.param.GetManjianInfoParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;

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

    @Resource
    SysConfigMapper sysConfigMapper;

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

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
            goodsInfo.setAttrIds(strArrToIntArr(Convert.toStrArray(JSONUtil.parseArray(model.getAttrIds()))));
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
            if (model.getPosterId().equals(0)) goodsInfo.setPosterId("");
            if (model.getDiyDetailId().equals(0)) goodsInfo.setDiyDetailId("");
            if (model.getFormId().equals(0)) goodsInfo.setFormId("");

            goodsInfo.setActiveGoodsCount(getActiveGoodsCount(goodsId));

            vo.setGoodsInfo(goodsInfo);
        }
        vo.setDefaultSort(coreGoodsConfigService.getSortConfig().getDefaultSort());
        return vo;
    }
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
        queryWrapper.select("g.goods_id,g.site_id,goods_name,sub_title,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount,is_gift");
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
            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), vo.getGoodsCover()));
            list.add(vo);
        }
        list = formatGoodsJoinActive(list);
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 处理商品参与的活动数据
     * @param list
     * @return
     */
    private List<ShopGoodsListVo> formatGoodsJoinActive(List<ShopGoodsListVo> list) {
        List<Integer> goodsIds = list.stream()
                .map(ShopGoodsListVo::getGoodsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (goodsIds.isEmpty()) {
            return list;
        }
        CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("goods_ids", goodsIds);
        eventMap.put("site_id", RequestUtils.siteId());
        event.setEventName("GetGoodsJoinInfo");
        event.setData(eventMap);
        event.setSiteId(RequestUtils.siteId());
        List<CommonEventDefiner.CommonEventResult> joinList = EventAndSubscribeOfPublisher.publishAndCallback(event);

        Map<Integer, List<Map<String, Object>>> goodsJoin = new HashMap<>();
        if (joinList != null) {
            for (CommonEventDefiner.CommonEventResult item : joinList) {
                if (item == null || item.getResult() == null) {
                    continue;
                }
                Map<Integer, Map<String, Map<String, Object>>> result =
                        (Map<Integer, Map<String, Map<String, Object>>>) item.getResult();
                // 合并每个商品的活动信息
                for (Map.Entry<Integer, Map<String, Map<String, Object>>> entry : result.entrySet()) {
                    Integer goodsId = entry.getKey();
                    Map<String, Map<String, Object>> activities = entry.getValue();

                    List<Map<String, Object>> activityList = new ArrayList<>(activities.values());

                    if (goodsJoin.containsKey(goodsId)) {
                        List<Map<String, Object>> existingList = goodsJoin.get(goodsId);
                        existingList.addAll(activityList);
                    } else {
                        goodsJoin.put(goodsId, activityList);
                    }
                }
            }
        }

        Map<Integer, Map<String, Map<String, Object>>> activeTips = new HashMap<>();
        if (!goodsJoin.isEmpty()) {
            for (Map.Entry<Integer, List<Map<String, Object>>> entry : goodsJoin.entrySet()) {
                Integer goodsId = entry.getKey();
                List<Map<String, Object>> joinActive = entry.getValue();

                for (Map<String, Object> activity : joinActive) {
                    String joinType = activity.get("join_type").toString();
                    if (joinType == null) {
                        continue;
                    }

                    // 初始化当前商品-活动类型的存储结构
                    activeTips.computeIfAbsent(goodsId, k -> new HashMap<>())
                            .computeIfAbsent(joinType, k -> {
                                Map<String, Object> typeMap = new HashMap<>();
                                typeMap.put("name", "");
                                typeMap.put("short", null);
                                typeMap.put("list", new ArrayList<>());
                                return typeMap;
                            });

                    Map<String, Object> typeMap = activeTips.get(goodsId).get(joinType);

                    // 处理short字段
                    if (activity.containsKey("short")) {
                        typeMap.put("short", activity.get("short"));

                        // 移除short后添加到list
                        Map<String, Object> activityCopy = new HashMap<>(activity);
                        activityCopy.remove("short");
                        ((List<Map<String, Object>>) typeMap.get("list")).add(activityCopy);

                        // 拼接name（保留原PHP的换行逻辑）
                        String name = activity.get("name") != null ? activity.get("name").toString() : "";
                        String currentName = typeMap.get("name") != null ? typeMap.get("name").toString() : "";
                        typeMap.put("name", currentName + name + System.lineSeparator());
                    } else {
                        // 没有short字段则移除该活动类型
                        activeTips.get(goodsId).remove(joinType);
                    }
                }
            }
        }

        for (ShopGoodsListVo goods : list) {
            Integer goodsId = goods.getGoodsId();
            Map<String, Map<String, Object>> goodsActive = activeTips.get(goodsId);
            goods.setActive(goodsActive != null ? goodsActive : new HashMap<>());
        }
        return list;
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
        if (addParam.getGoodsImage().contains(",")) {
            model.setGoodsCover(addParam.getGoodsImage().split(",")[0]);
        } else {
            model.setGoodsCover(addParam.getGoodsImage());
        }
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setGoodsCategory(JSONUtil.parseArray(intArrToStrArr(addParam.getGoodsCategory())).toString());
        model.setLabelIds(JSONUtil.parseArray(intArrToStrArr(addParam.getLabelIds())).toString());
        model.setServiceIds(JSONUtil.parseArray(intArrToStrArr(addParam.getServiceIds())).toString());
        model.setDeliveryType(JSONUtil.parseArray(addParam.getDeliveryType()).toString());
        model.setAttrFormat(JSONUtil.parseArray(addParam.getAttrFormat()).toString());
        model.setAttrIds(addParam.getAttrIds().toString());
        shopGoodsMapper.insert(model);

        // 规格数据
        ShopGoodsVerifySkuNoParam verifySkuNoParam = new ShopGoodsVerifySkuNoParam();
        List<ShopGoodsSku> skuData = new LinkedList<>();
        if (addParam.getSpecType().equals("single")) {
            if(ObjectUtil.isNotEmpty(addParam.getSkuNo()))
            {
                verifySkuNoParam.setSkuNo(addParam.getSkuNo());
                verifySkuNoParam.setGoodsId(model.getGoodsId());
                verifySkuNo(verifySkuNoParam);
            }
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
            List<String> skuNoArr = new ArrayList<>();
            for (String key : addParam.getGoodsSkuData().keySet()) {
                ShopGoodsParam.SkuData skuParam = addParam.getGoodsSkuData().get(key);
                if(!skuParam.getSkuNo().isEmpty()) skuNoArr.add(skuParam.getSkuNo());
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

            //验证sku编码
            if(ObjectUtil.isNotEmpty(skuNoArr))
            {
                verifySkuNoParam.setSkuNo(String.join(",", skuNoArr));
                 verifySkuNoParam.setGoodsId(model.getGoodsId());
                 verifySkuNo(verifySkuNoParam);
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
        model.setAttrFormat(JSONUtil.parseArray(editParam.getAttrFormat()).toString());
        model.setAttrIds(editParam.getAttrIds().toString());
        shopGoodsMapper.updateById(model);

        // 规格数据
        ShopGoodsVerifySkuNoParam verifySkuNoParam = new ShopGoodsVerifySkuNoParam();
        List<ShopGoodsSku> skuData = new LinkedList<>();
        if (editParam.getSpecType().equals("single")) {
            if(ObjectUtil.isNotEmpty(editParam.getSkuNo()))
            {
                verifySkuNoParam.setSkuNo(editParam.getSkuNo());
                verifySkuNoParam.setGoodsId(model.getGoodsId());
                verifySkuNo(verifySkuNoParam);
            }
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
            List<String> skuNoArr = new ArrayList<>();
            // 检测规格项是否发生变化
            if (firstSkuData.getSkuId() != null) {
                List<Integer> skuIdArr = new LinkedList<>();

                Integer defaultCount = 0;
                for (String key : editParam.getGoodsSkuData().keySet()) {
                    ShopGoodsParam.SkuData skuParam = editParam.getGoodsSkuData().get(key);
                    if(!skuParam.getSkuNo().isEmpty()) skuNoArr.add(skuParam.getSkuNo());
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
                    if(!skuParam.getSkuNo().isEmpty()) skuNoArr.add(skuParam.getSkuNo());
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
            //验证sku编码
            if(ObjectUtil.isNotEmpty(skuNoArr))
            {
                verifySkuNoParam.setSkuNo(String.join(",", skuNoArr));
                verifySkuNoParam.setGoodsId(model.getGoodsId());
                verifySkuNo(verifySkuNoParam);
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

        Long activeCount = getActiveGoodsCount(goodsIds, param.getIsAll(), param.getWhere());
        if (activeCount > 0) throw new CommonException("商品正在参与营销活动，禁止删除");

        GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEvent event = new GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEvent();
        event.setSiteId(RequestUtils.siteId());
        event.setGoodsIds(goodsIds);
        event.setIsAll(param.getIsAll());
        event.setWhere(param.getWhere());
        List<GoodsIsConnectedCardEventDefiner.GoodsIsConnectedCardEventResult> list = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if (!CollectionUtils.isEmpty(list)){
            if (list.get(0).getIsConnected()){
                throw new CommonException("商品正在参与礼品卡活动，禁止删除");
            }
        }

        ShopGoods model = new ShopGoods();
        model.setStatus(0);
        model.setDeleteTime(System.currentTimeMillis() / 1000);

        QueryWrapper queryWrapper = new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId());
        if (param.getIsAll() == 1) {
            queryWrapper = getBatchAllQuery(queryWrapper, param.getWhere());
        } else {
            queryWrapper.in("goods_id", goodsIds);
        }

        shopGoodsMapper.update(model, queryWrapper);
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
            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), item.getGoodsCover()));

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

        List<Integer> explodeGoodsIds = new LinkedList<>();
        if (param.getStatus().equals(0)) {
            explodeGoodsIds = getActiveGoodsIds(goodsIds, param.getIsAll(), param.getWhere());
        }

        ShopGoods model = new ShopGoods();
        model.setStatus(param.getStatus());

        QueryWrapper queryWrapper = new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId());
        if (param.getIsAll() == 1) {
            queryWrapper = getBatchAllQuery(queryWrapper, param.getWhere());
        } else {
            queryWrapper.in("goods_id", goodsIds);
        }
        if (ObjectUtil.isNotEmpty(explodeGoodsIds)) queryWrapper.notIn("goods_id", explodeGoodsIds);

        shopGoodsMapper.update(model, queryWrapper);
    }

    public QueryWrapper getBatchAllQuery(QueryWrapper queryWrapper, Map<String, Object> where) {
        JSONObject whereJson = JSONUtil.parseObj(where);

        String goodsName = whereJson.getStr("goods_name", "");
        Integer brandId = whereJson.getInt("brand_id", 0);
        String goodsType = whereJson.getStr("goods_type", "");
        Integer goodsCategory = whereJson.getInt("goods_category", 0);
        Integer labelIds = whereJson.getInt("label_ids", 0);
        Integer startSaleNum = whereJson.getInt("start_sale_num", 0);
        Integer endSaleNum = whereJson.getInt("end_sale_num", 0);
        BigDecimal startPrice = whereJson.getBigDecimal("start_price", BigDecimal.ZERO);
        BigDecimal endPrice = whereJson.getBigDecimal("end_price", BigDecimal.ZERO);

        if (ObjectUtil.isNotEmpty(goodsName)) queryWrapper.like("goods_name", goodsName);
        if (brandId != 0) queryWrapper.eq("brand_id", brandId);
        if (ObjectUtil.isNotEmpty(goodsType)) queryWrapper.eq("goods_type", goodsType);
        if (goodsCategory != 0) queryWrapper.like("goods_category", "\"" + goodsCategory + "\"");
        if (labelIds != 0) queryWrapper.like("label_ids", "\"" + labelIds + "\"");
        QueryMapperUtils.buildByBetween(queryWrapper, "sale_num", new Integer[]{startSaleNum, endSaleNum});
        QueryMapperUtils.buildByBetween(queryWrapper, "price", new BigDecimal[]{startPrice, endPrice});

        return queryWrapper;
    }

    /**
     * 查询参与营销活动的商品
     * @param goodsIds
     * @param isAll
     * @param where
     * @return
     */
    private List<Integer> getActiveGoodsIds(Integer[] goodsIds, Integer isAll, Map<String, Object> where) {
        Map<String, Object> data = new HashMap<>();
        data.put("goods_ids", Arrays.asList(goodsIds));
        data.put("site_id", RequestUtils.siteId());
        data.put("is_all", isAll);
        data.put("where", where);

        CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
        event.setEventName("GetGoodsJoinInfo");
        event.setSiteId(RequestUtils.siteId());
        event.setData(data);
        List<CommonEventDefiner.CommonEventResult> dataList = EventAndSubscribeOfPublisher.publishAndCallback(event);

        List<Integer> activeGoodsIds = new ArrayList<>();

        for (CommonEventDefiner.CommonEventResult item: dataList) {
            if (item == null || item.getResult() == null || ObjectUtil.isEmpty(item.getResult())) {
                continue;
            }
            Map<Integer, Map<String, Map<String, Object>>> result =
                (Map<Integer, Map<String, Map<String, Object>>>) item.getResult();
            List<Integer> ids = new ArrayList<>(Arrays.asList(result.keySet().toArray(Integer[]::new)));
            if (ObjectUtil.isNotEmpty(ids)) {
                activeGoodsIds.addAll(ids);
            }
        }
        return activeGoodsIds;
    }

    /**
     * 修改商品上下架状态（单商品）
     *
     * @param param
     */
    @Override
    public void editSimpleStatus(EditGoodsSimpleStatusParam param) {
        if (param.getStatus().equals(0) && getActiveGoodsCount(param.getGoodsId()) > 0) {
            throw new CommonException("商品正在参与营销活动，禁止下架");
        }
        ShopGoods model = new ShopGoods();
        model.setStatus(param.getStatus());
        shopGoodsMapper.update(model, new QueryWrapper<ShopGoods>().eq("site_id", RequestUtils.siteId()).eq("goods_id", param.getGoodsId()));
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
        return getActiveGoodsCount(new Integer[]{goodsId}, 0, new HashMap<>());
    }

    /**
     * 查询商品参与营销活动的数量
     *
     * @return
     */
    public Long getActiveGoodsCount(Integer[] goodsIds, Integer isAll, Map<String, Object> where) {
        Map<String, Object> data = new HashMap<>();
        data.put("goods_ids", Arrays.asList(goodsIds));
        data.put("site_id", RequestUtils.siteId());
        data.put("is_get_count", 1);
        data.put("is_all", isAll);
        data.put("where", where);

        CommonEventDefiner.CommonEvent event = new CommonEventDefiner.CommonEvent();
        event.setEventName("GetGoodsJoinInfo");
        event.setSiteId(RequestUtils.siteId());
        event.setData(data);
        List<CommonEventDefiner.CommonEventResult> dataList = EventAndSubscribeOfPublisher.publishAndCallback(event);

        Long count = 0L;

        for (CommonEventDefiner.CommonEventResult item: dataList) {
            if (item == null || item.getResult() == null) {
                continue;
            }
            count+= Long.valueOf(item.getResult().toString());
        }
        return count;

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
            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), vo.getGoodsCover()));
            vo.setGoodsCoverThumbMid(CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover()));
            list.add(vo);
        }
        PageResult pageResutl = PageResult.build(page, limit, iPage.getTotal()).setData(list);

        ShopSelectGoodsListVo vo = new ShopSelectGoodsListVo();
        BeanUtils.copyProperties(pageResutl, vo);

        vo.setVerifyGoodsIds(verifyGoodsIds);
        vo.setVerifySkuIds(verifySkuIds);

        return vo;
    }

//    @Override
//    public ShopSelectGoodsListVo getZhongcaoSelectPage(PageParam pageParam, ShopGoodsSelectSearchParam searchParam) {
//        Integer page = pageParam.getPage();
//        Integer limit = pageParam.getLimit();
//
//        MPJQueryWrapper<ShopGoods> queryWrapper = new MPJQueryWrapper<>();
//        queryWrapper.setAlias("g")
//                .innerJoin("?_shop_goods_sku gs ON g.goods_id = gs.goods_id AND gs.is_default = 1".replace("?_", GlobalConfig.tablePrefix));
//        queryWrapper.select("g.goods_id,g.site_id,g.goods_image,goods_name,goods_type,goods_cover,g.stock,g.sale_num,status,sort,create_time,member_discount");
//        queryWrapper.eq("g.site_id", RequestUtils.siteId());
//        queryWrapper.eq("delete_time", 0);
//
//        if (ObjectUtil.isNotEmpty(searchParam.getIsGift()) && searchParam.getIsGift() == 1) {
//            queryWrapper.ge("g.is_gift", 0);
//        } else {
//            queryWrapper.eq("g.is_gift", 0);
//        }
//
//        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
//            queryWrapper.and(i -> i.like("goods_name", searchParam.getKeyword()).or().like("sub_title", searchParam.getKeyword()));
//        }
//
//        if (ObjectUtil.isNotEmpty(searchParam.getSkuIds())) {
//            List<ShopGoodsSku> selectGoodsList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().select("goods_id").in("sku_id", searchParam.getSkuIds()).groupBy("goods_id"));
//            if (ObjectUtil.isNotEmpty(selectGoodsList)) {
//                List<Integer> goodsIds = selectGoodsList.stream().map(i -> i.getGoodsId()).toList();
//                if (ObjectUtil.isEmpty(searchParam.getGoodsIds())) searchParam.setGoodsIds(goodsIds);
//            }
//        }
//
//        if (searchParam.getSelectType().equals("all")) {
//            queryWrapper.gt("g.stock", 0);
//            queryWrapper.eq("g.status", 1);
//        }
//
//        if (searchParam.getSelectType().equals("all")) {
//            if(ObjectUtil.isNotEmpty(searchParam.getGoodsIds()) && searchParam.getGoodsIds().size() > 0){
//                queryWrapper.notIn("g.goods_id", searchParam.getGoodsIds());
//            }
//        }else if(searchParam.getSelectType().equals("selected")){
//            queryWrapper.in("g.goods_id", searchParam.getGoodsIds());
//        }
//
//        if (ObjectUtil.isNotEmpty(searchParam.getGoodsType()))
//            queryWrapper.eq("goods_type", searchParam.getGoodsType());
//
//        if (ObjectUtil.isNotEmpty(searchParam.getGoodsCategory()))
//            queryWrapper.like("goods_category", "\"" + searchParam.getGoodsCategory() + "\"");
//
//        List<Integer> verifyGoodsIds = new LinkedList<>();
//        List<Integer> verifySkuIds = new LinkedList<>();
//
//        // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
//        if (ObjectUtil.isNotEmpty(searchParam.getVerifyGoodsIds())) {
//            List<ShopGoods> verifyGoodsList = shopGoodsMapper.selectList(new QueryWrapper<ShopGoods>().select("goods_id").in("goods_id", searchParam.getVerifyGoodsIds()).eq("site_id", RequestUtils.siteId()).eq("delete_time", 0).eq("status", 1));
//            if (ObjectUtil.isNotEmpty(verifyGoodsList)) {
//                verifyGoodsIds = verifyGoodsList.stream().map(i -> i.getGoodsId()).collect(Collectors.toList());
//            }
//        }
//
//        if (ObjectUtil.isNotEmpty(searchParam.getVerifySkuIds())) {
//            List<ShopGoodsSku> verifySkuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().in("sku_id", searchParam.getVerifySkuIds()).select("sku_id"));
//            if (ObjectUtil.isNotEmpty(verifySkuList)) {
//                verifySkuIds = verifySkuList.stream().map(i -> i.getSkuId()).collect(Collectors.toList());
//            }
//        }
//
//        queryWrapper.orderByDesc("g.goods_id");
//
//        IPage<ShopSelectGoodsListVo.ListVo> iPage = shopGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopSelectGoodsListVo.ListVo.class, queryWrapper);
//        List<ShopSelectGoodsListVo.ListVo> list = new LinkedList<>();
//        for (ShopSelectGoodsListVo.ListVo vo : iPage.getRecords()) {
//            vo.setGoodsImage(vo.getGoodsImage());
//
//            List<ShopGoodsSku> skuList = shopGoodsSkuMapper.selectList(new QueryWrapper<ShopGoodsSku>().eq("goods_id", vo.getGoodsId()));
//            vo.setSkuList(skuList);
//
//            for (ShopGoodsSku item : skuList) {
//                if (item.getIsDefault().equals(1)) {
//                    ShopSelectGoodsListVo.GoodsSku goodsSkuVo = new ShopSelectGoodsListVo.GoodsSku();
//                    BeanUtils.copyProperties(item, goodsSkuVo);
//                    vo.setGoodsSku(goodsSkuVo);
//                }
//            }
//            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), vo.getGoodsCover()));
//            vo.setGoodsCoverThumbMid(CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover()));
//            list.add(vo);
//        }
//        PageResult pageResutl = PageResult.build(page, limit, iPage.getTotal()).setData(list);
//
//        ShopSelectGoodsListVo vo = new ShopSelectGoodsListVo();
//        BeanUtils.copyProperties(pageResutl, vo);
//
//        vo.setVerifyGoodsIds(verifyGoodsIds);
//        vo.setVerifySkuIds(verifySkuIds);
//
//        return vo;
//    }


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
            vo.setGoodsCoverThumbSmall(CommonUtils.thumbImageSmall(RequestUtils.siteId(), vo.getGoodsCover()));
            vo.setGoodsCoverThumbMid(CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover()));
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
            String mid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), vo.getGoodsCover());
            vo.setGoodsCoverThumbMid(mid);
            String small = CommonUtils.thumbImageSmall(RequestUtils.siteId(), vo.getGoodsCover());
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
        String small = CommonUtils.thumbImageSmall(RequestUtils.siteId(), shopGoodsSku.getSkuImage());
        String mid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), shopGoodsSku.getSkuImage());
        String big = CommonUtils.thumbImageBig(RequestUtils.siteId(), shopGoodsSku.getSkuImage());

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
                    String small = CommonUtils.thumbImageSmall(RequestUtils.siteId(), sku.getSkuImage());
                    vo.setGoodsCoverThumbMid(small);
                    String mid = CommonUtils.thumbImageMiddle(RequestUtils.siteId(), sku.getSkuImage());
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

    @Override
    public void batchSet(ShopGoodsBatchSetParam param) {

        if (StringUtils.isEmpty(param.getSetType())) {
            throw new AdminException("未获取到设置类型");
        }
        String type = param.getSetType();
        JSONObject value = param.getSetValue();
        Boolean isAll = param.getIsAll();
        if (!isAll && param.getGoodsIds().isEmpty()) {
            throw new AdminException("未获取到商品信息");
        }
        JSONArray goodsIds = param.getGoodsIds();
        LambdaUpdateWrapper<ShopGoods> uw = new LambdaUpdateWrapper<>();
        if (type.equals(GoodsBatchSetEnum.LABEL.getKey())) {
            if (value.containsKey("label_ids") && ObjectUtil.isNotEmpty(value.get("label_ids"))) {
                List<String> labelIds = value.getJSONArray("label_ids").stream().map(Object::toString).toList();
                uw.set(ShopGoods::getLabelIds, JSONUtil.parseArray(labelIds).toString());
            }
        } else if (type.equals(GoodsBatchSetEnum.SERVICE.getKey())) {
            if (value.containsKey("service_ids") && ObjectUtil.isNotEmpty(value.get("service_ids"))) {
                List<String> serviceIds = value.getJSONArray("service_ids").stream().map(Object::toString).toList();
                uw.set(ShopGoods::getServiceIds, JSONUtil.parseArray(serviceIds).toString());
            }
        } else if (type.equals(GoodsBatchSetEnum.VIRTUAL_SALE_NUM.getKey())) {
            if (value.containsKey("virtual_sale_num") && ObjectUtil.isNotEmpty(value.get("virtual_sale_num"))) {
                Integer virtualSaleNum = value.getInt("virtual_sale_num");
                uw.set(ShopGoods::getVirtualSaleNum, virtualSaleNum);
            }
        } else if (type.equals(GoodsBatchSetEnum.CATEGORY.getKey())) {
            if (value.containsKey("goods_category") && ObjectUtil.isNotEmpty(value.get("goods_category"))) {
                List<String> categoryIds = value.getJSONArray("goods_category").stream().map(Object::toString).toList();
                uw.set(ShopGoods::getGoodsCategory, JSONUtil.parseArray(categoryIds).toString());
            }
        } else if (type.equals(GoodsBatchSetEnum.BRAND.getKey())) {
            if (value.containsKey("brand_id") && ObjectUtil.isNotEmpty(value.get("brand_id"))) {
                Integer brandId = value.getInt("brand_id");
                uw.set(ShopGoods::getBrandId, brandId);
            }
        } else if (type.equals(GoodsBatchSetEnum.POSTER.getKey())) {
            if (value.containsKey("poster_id") && ObjectUtil.isNotEmpty(value.get("poster_id"))) {
                Integer posterId = value.getInt("poster_id");
                uw.set(ShopGoods::getPosterId, posterId);
            }
        } else if (type.equals(GoodsBatchSetEnum.DIY_FORM.getKey())) {
            if (value.containsKey("diy_form_ids") && ObjectUtil.isNotEmpty(value.get("diy_form_ids"))) {
                String formId = value.get("diy_form_ids").toString();
                uw.set(ShopGoods::getFormId, formId);
            }
        } else if (type.equals(GoodsBatchSetEnum.GIFT.getKey())) {
            if (value.containsKey("is_gift") && ObjectUtil.isNotEmpty(value.get("is_gift"))) {
                Integer isGift = value.getInt("is_gift");
                if (isGift == 1 || isGift == 0) {
                    uw.set(ShopGoods::getIsGift, isGift);
                }
            }
        } else if (type.equals(GoodsBatchSetEnum.DELIVERY.getKey())) {
            if (value.containsKey("delivery_type") && !value.getJSONArray("delivery_type").isEmpty()) {
                uw.set(ShopGoods::getDeliveryType, value.getJSONArray("delivery_type").toString());

                if (ObjectUtil.isNotEmpty(value.get("fee_type"))) {
                    uw.set(ShopGoods::getFeeType, value.getStr("fee_type"));
                }

                if (ObjectUtil.isNotEmpty(value.get("is_free_shipping"))) {
                    uw.set(ShopGoods::getIsFreeShipping, value.getInt("is_free_shipping"));
                }

                if (ObjectUtil.isNotEmpty(value.get("delivery_money"))) {
                    uw.set(ShopGoods::getDeliveryMoney, value.getBigDecimal("delivery_money"));
                }

                if (ObjectUtil.isNotEmpty(value.get("delivery_template_id"))) {
                    uw.set(ShopGoods::getDeliveryTemplateId, value.getInt("delivery_template_id"));
                }
            }
        } else if (type.equals(GoodsBatchSetEnum.STOCK.getKey())) {
            if (value.containsKey("stock_type") && ObjectUtil.isNotEmpty(value.get("stock_type"))
                    && value.containsKey("stock") && ObjectUtil.isNotEmpty(value.get("stock")) && value.getInt("stock") >= 0) {
                Integer stock = value.getInt("stock");
                String stockType = value.get("stock_type").toString();
                List<Integer> skuIds = getUpdStockIds(param.getWhere(), isAll, goodsIds, "skuIds");
                if (!skuIds.isEmpty()) {
                    LambdaUpdateWrapper<ShopGoodsSku> updateWrapper = new LambdaUpdateWrapper<>();
                    if (stockType.equals("inc")) {
                        updateWrapper.setSql("stock = stock + " + stock).in(ShopGoodsSku::getSkuId, skuIds);
                    } else {
                        updateWrapper.setSql("stock = CASE WHEN stock >= " + stock + " THEN stock - " + stock + " ELSE stock = 0 END").in(ShopGoodsSku::getSkuId, skuIds);
                    }
                    shopGoodsSkuMapper.update(updateWrapper);
                }
            }
            List<Integer> shopGoodsIds = getUpdStockIds(param.getWhere(), isAll, goodsIds, "goodsIds");
            if (!shopGoodsIds.isEmpty()) {
                List<Map<String, Object>> maps = shopGoodsSkuMapper.selectMaps(new QueryWrapper<ShopGoodsSku>()
                        .select("COALESCE(SUM(stock), 0) as stock, goods_id")
                        .in("goods_id", shopGoodsIds)
                        .groupBy("goods_id"));
                for (Map<String, Object> stockMap : maps) {
                    LambdaUpdateWrapper<ShopGoods> updateWrapper = new LambdaUpdateWrapper<>();
                    if (stockMap.get("stock") != null && stockMap.get("goods_id") != null) {
                        updateWrapper.set(ShopGoods::getStock, stockMap.get("stock")).eq(ShopGoods::getGoodsId, stockMap.get("goods_id"));
                        shopGoodsMapper.update(updateWrapper);
                    }
                }
            }
            return;
        }
        List<Integer> shopGoodsIds = getUpdStockIds(param.getWhere(), isAll, goodsIds, "goodsIds");
        if (shopGoodsIds.isEmpty()){
            return;
        }
        boolean hasUpdateField = uw.getSqlSet() != null && !uw.getSqlSet().isEmpty();
        if (hasUpdateField && !shopGoodsIds.isEmpty()) {
            uw.in(ShopGoods::getGoodsId, shopGoodsIds);
            shopGoodsMapper.update(uw);
        }
    }

    private List<Integer> getUpdStockIds(JSONObject where, Boolean isAll, JSONArray goodsIds, String type) {
        MPJQueryWrapper<ShopGoodsSku> mpjqw = new MPJQueryWrapper<>();
        mpjqw.setAlias("sgk").leftJoin("?_shop_goods sg on sg.goods_id = sgk.goods_id".replace("?_", GlobalConfig.tablePrefix));
        mpjqw.select("sgk.sku_id, sg.goods_id");
        mpjqw.eq("sg.site_id", RequestUtils.siteId()).eq("sgk.site_id", RequestUtils.siteId());
        if (isAll) {
            mpjqw.eq("sgk.is_default", 1);
            if (ObjectUtil.isNotEmpty(where.get("start_price")) && ObjectUtil.isNotEmpty(where.get("end_price"))) {
                mpjqw.between("sgk.price", where.get("start_price"), where.get("end_price"));
            } else if (ObjectUtil.isNotEmpty(where.get("start_price"))) {
                mpjqw.ge("sgk.price", where.get("start_price"));
            } else if (ObjectUtil.isNotEmpty(where.get("end_price"))) {
                mpjqw.le("sgk.price", where.get("end_price"));
            }
            if(ObjectUtil.isNotEmpty(goodsIds)){
                mpjqw.notIn("sg.goods_id", goodsIds);
            }
        } else {
            mpjqw.in("sg.goods_id", goodsIds);
        }
        List<ShopGoodsSku> shopGoodsSkus = shopGoodsSkuMapper.selectJoinList(ShopGoodsSku.class, mpjqw);
        List<Integer> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(shopGoodsSkus)) {
            if (type.equals("skuIds")) {
                result = shopGoodsSkus.stream().map(ShopGoodsSku::getSkuId).distinct().collect(Collectors.toList());
            } else {
                result = shopGoodsSkus.stream().map(ShopGoodsSku::getGoodsId).distinct().collect(Collectors.toList());
            }
        }
        return result;
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

    @Override
    public Boolean verifySkuNo(ShopGoodsVerifySkuNoParam param) {
        return coreGoodsConfigService.verifySkuNo(param);
    }


}
