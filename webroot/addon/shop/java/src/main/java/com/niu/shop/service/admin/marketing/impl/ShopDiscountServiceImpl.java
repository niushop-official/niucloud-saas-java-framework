package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.shop.entity.discount.ShopDiscount;
import com.niu.shop.entity.discount.ShopDiscountGoods;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.goods.ShopGoodsSku;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDiscount;
import com.niu.shop.enums.active.ActiveClassEnum;
import com.niu.shop.enums.active.ActiveStatusEnum;
import com.niu.shop.enums.goods.GoodsTypeEnum;
import com.niu.shop.mapper.discount.ShopDiscountGoodsMapper;
import com.niu.shop.mapper.discount.ShopDiscountMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.goods.ShopGoodsSkuMapper;
import com.niu.shop.mapper.order.ShopOrderDiscountMapper;
import com.niu.shop.service.admin.marketing.IShopDiscountService;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 限时折扣实现
 */
@Slf4j
@Service
public class ShopDiscountServiceImpl implements IShopDiscountService {

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopGoodsSkuMapper shopGoodsSkuMapper;

    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private ShopOrderDiscountMapper shopOrderDiscountMapper;

    @Resource
    private ShopDiscountMapper shopDiscountMapper;

    @Resource
    private ShopDiscountGoodsMapper shopDiscountGoodsMapper;

    @Resource
    private ICoreUploadService coreUploadService;

    /**
     * 限时折扣列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    @Override
    public PageResult<ShopDiscountListVo> getPage(PageParam pageParam, ShopDiscountSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        LambdaQueryWrapper<ShopDiscount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopDiscount::getSiteId, RequestUtils.siteId());

        if (StringUtils.isNotEmpty(searchParam.getName())) {
            queryWrapper.like(ShopDiscount::getName, searchParam.getName());
        }
        if (StringUtils.isNotEmpty(searchParam.getStatus())) {
            queryWrapper.eq(ShopDiscount::getStatus, searchParam.getStatus());
        }

        IPage<ShopDiscount> iPage = shopDiscountMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopDiscountListVo> list = new LinkedList<>();
        for (ShopDiscount item : iPage.getRecords()) {
            ShopDiscountListVo vo = new ShopDiscountListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 限时折扣详情
     *
     * @param id 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public ShopDiscountInfoVo info(Integer id) {
        ShopDiscount model = shopDiscountMapper.selectById(id);

        Assert.notNull(model, "数据不存在");

        ShopDiscountInfoVo vo = new ShopDiscountInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 限时折扣详情
     *
     * @param discountId 主键
     * @return ShopActiveInfoVo
     */
    @Override
    public ShopDiscountDetailVo detail(Integer discountId) {
        ShopDiscount shopDiscount = shopDiscountMapper.selectById(discountId);
        if (shopDiscount == null) {
            throw new AdminException("数据不存在");
        }
        Integer siteId = RequestUtils.siteId();
        ShopDiscountDetailVo vo = new ShopDiscountDetailVo();
        BeanUtils.copyProperties(shopDiscount, vo);

        // 查询折扣商品关联信息
        List<ShopDiscountGoods> discountGoodsList = shopDiscountGoodsMapper.selectList(
                new LambdaQueryWrapper<ShopDiscountGoods>()
                        .eq(ShopDiscountGoods::getSiteId, siteId)
                        .eq(ShopDiscountGoods::getDiscountId, discountId)
        );

        // 获取商品ID和SKU ID列表
        List<Integer> goodsIds = discountGoodsList.stream()
                .map(ShopDiscountGoods::getGoodsId)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> skuIds = discountGoodsList.stream()
                .map(ShopDiscountGoods::getSkuId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询商品和SKU信息
        Map<Integer, ShopGoods> goodsMap = shopGoodsMapper.selectBatchIds(goodsIds).stream()
                .collect(Collectors.toMap(ShopGoods::getGoodsId, Function.identity()));

        Map<Integer, ShopGoodsSku> skuMap = shopGoodsSkuMapper.selectBatchIds(skuIds).stream()
                .collect(Collectors.toMap(ShopGoodsSku::getSkuId, Function.identity()));

        // 按商品ID分组
        Map<Integer, List<ShopDiscountGoods>> goodsGroup = discountGoodsList.stream()
                .collect(Collectors.groupingBy(ShopDiscountGoods::getGoodsId));

        //构建商品详情信息
        List<ShopDiscountDetailVo.GoodsInfoVO> goodsInfoVo = goodsGroup.values().stream()
                .map(items -> convertToGoodsInfoVO(items, goodsMap, skuMap))
                .collect(Collectors.toList());
        vo.setGoodsInfo(goodsInfoVo);
        return vo;
    }

    /**
     * 构建商品详情信息
     * @param items
     * @param goodsMap
     * @param skuMap
     * @return
     */
    private ShopDiscountDetailVo.GoodsInfoVO convertToGoodsInfoVO(List<ShopDiscountGoods> items,
                                                                  Map<Integer, ShopGoods> goodsMap,
                                                                  Map<Integer, ShopGoodsSku> skuMap) {
        Integer goodsId = items.get(0).getGoodsId();
        ShopGoods goods = goodsMap.get(goodsId);

        ShopDiscountDetailVo.GoodsInfoVO goodsInfo = new ShopDiscountDetailVo.GoodsInfoVO();
        goodsInfo.setGoodsId(goodsId);
        goodsInfo.setGoodsName(goods.getGoodsName());
        String goodsCoverThumbSmall = CommonUtils.thumbImageSmall(RequestUtils.siteId(), goods.getGoodsCover());
        goodsInfo.setGoodsCoverThumbSmall(goodsCoverThumbSmall);
        goodsInfo.setGoodsType(goods.getGoodsType());
        goodsInfo.setGoodsTypeName(GoodsTypeEnum.getNameByType(goods.getGoodsType()));

        // 转换SKU列表
        List<ShopDiscountDetailVo.GoodsSkuVO> skuList = items.stream()
                .map(item -> convertToGoodsSkuVO(item, goods, skuMap.get(item.getSkuId())))
                .collect(Collectors.toList());

        goodsInfo.setGoodsSkuList(skuList);

        // 设置商品级别信息（取第一个SKU的信息）
        ShopDiscountGoods firstItem = items.get(0);
        goodsInfo.setSkuId(firstItem.getSkuId());
        goodsInfo.setType(firstItem.getType());
        goodsInfo.setPrice(skuMap.get(firstItem.getSkuId()).getPrice());

        if (items.size() > 1) {
            // 多规格商品
            goodsInfo.setSpecType("multi");
            setMultiSpecInfo(goodsInfo, items, skuMap);
        } else {
            // 单规格商品
            goodsInfo.setSpecType("single");
            goodsInfo.setRate(firstItem.getRate());
            goodsInfo.setReduceMoney(firstItem.getReduceMoney());
            goodsInfo.setDiscountPrice(firstItem.getDiscountPrice());
        }

        return goodsInfo;
    }

    /**
     * 构建商品SKU信息
     * @param item
     * @param goods
     * @param sku
     * @return
     */
    private ShopDiscountDetailVo.GoodsSkuVO convertToGoodsSkuVO(ShopDiscountGoods item, ShopGoods goods, ShopGoodsSku sku) {
        ShopDiscountDetailVo.GoodsSkuVO skuVO = new ShopDiscountDetailVo.GoodsSkuVO();
        BeanUtils.copyProperties(item, skuVO);
        skuVO.setDiscountGoodsId(item.getDiscountGoodsId());
        skuVO.setSiteId(item.getSiteId());
        skuVO.setDiscountId(item.getDiscountId());
        skuVO.setGoodsId(item.getGoodsId());
        skuVO.setSkuId(item.getSkuId());
        skuVO.setType(item.getType());
        skuVO.setRate(item.getRate());
        skuVO.setReduceMoney(item.getReduceMoney());
        skuVO.setDiscountPrice(item.getDiscountPrice());
        skuVO.setIsEnabled(item.getIsEnabled());
        skuVO.setGoodsName(goods.getGoodsName());
        skuVO.setGoodsCover(goods.getGoodsCover());
        String goodsCoverThumbSmall = CommonUtils.thumbImageSmall(RequestUtils.siteId(), goods.getGoodsCover());
        skuVO.setGoodsCoverThumbSmall(goodsCoverThumbSmall);
        skuVO.setGoodsType(goods.getGoodsType());
        skuVO.setPrice(sku.getPrice());
        skuVO.setSkuName(sku.getSkuName());
        skuVO.setSkuImage(sku.getSkuImage());
        return skuVO;
    }

    /**
     * 构建多规格商品信息
     * @param goodsInfo
     * @param items
     * @param skuMap
     */
    private void setMultiSpecInfo(ShopDiscountDetailVo.GoodsInfoVO goodsInfo, List<ShopDiscountGoods> items, Map<Integer, ShopGoodsSku> skuMap) {
        List<BigDecimal> rates = items.stream()
                .filter(item -> item.getIsEnabled() == 1)
                .map(ShopDiscountGoods::getRate)
                .collect(Collectors.toList());

        List<BigDecimal> reduceMoneys = items.stream()
                .filter(item -> item.getIsEnabled() == 1)
                .map(ShopDiscountGoods::getReduceMoney)
                .collect(Collectors.toList());

        List<BigDecimal> discountPrices = items.stream()
                .filter(item -> item.getIsEnabled() == 1)
                .map(ShopDiscountGoods::getDiscountPrice)
                .collect(Collectors.toList());

        if (!rates.isEmpty()) {
            goodsInfo.setMinRate(Collections.min(rates));
            goodsInfo.setMaxRate(Collections.max(rates));
        }
        if (!reduceMoneys.isEmpty()) {
            goodsInfo.setMinReduceMoney(Collections.min(reduceMoneys));
            goodsInfo.setMaxReduceMoney(Collections.max(reduceMoneys));
        }
        if (!discountPrices.isEmpty()) {
            goodsInfo.setMinDiscountPrice(Collections.min(discountPrices));
            goodsInfo.setMaxDiscountPrice(Collections.max(discountPrices));
        }
    }


    @Override
    @Transactional
    public void add(ShopDiscountParam shopDiscountParam) {
        List<ShopDiscountParam.GoodsItemParam> goodsList = shopDiscountParam.getGoodsList();
        List<Integer> goodsIds = goodsList.stream().map(item -> item.getGoodsId()).collect(Collectors.toList());
        long startTime = DateUtils.StringToTimestamp(shopDiscountParam.getStartTime());
        long endTime = DateUtils.StringToTimestamp(shopDiscountParam.getEndTime());

        //数据校验
        checkGoods(shopDiscountParam.getGoodsList());

        //检验商品是否存在冲突
        checkGoodsConflict(0, goodsIds, startTime, endTime);

        String status = ActiveStatusEnum.NOT_ACTIVE.getStatus();
        Integer siteId = RequestUtils.siteId();
        Long time = DateUtils.currTime();

        if (startTime <= time) {
            status = ActiveStatusEnum.ACTIVE.getStatus();
        } else if (endTime <= time) {
            status = ActiveStatusEnum.END.getStatus();
        }

        //活动添加
        ShopDiscount shopDiscount = ShopDiscount.builder()
                .siteId(siteId)
                .name(shopDiscountParam.getName())
                .remark(shopDiscountParam.getRemark())
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .createTime(time).build();
        shopDiscountMapper.insert(shopDiscount);

        //商品添加
        List<ShopDiscountGoods> shopDiscountGoodsList = new ArrayList<>();
        for (ShopDiscountParam.GoodsItemParam goods : goodsList) {
            for (ShopDiscountParam.SkuItemParam sku : goods.getSkuList()) {
                ShopDiscountGoods shopDiscountGoods = ShopDiscountGoods.builder()
                        .siteId(siteId)
                        .discountId(shopDiscount.getDiscountId())
                        .goodsId(goods.getGoodsId())
                        .skuId(sku.getSkuId())
                        .status(status)
                        .type(sku.getDiscountType())
                        .rate(sku.getDiscountRate())
                        .reduceMoney(sku.getReduceMoney())
                        .discountPrice(sku.getDiscountPrice())
                        .isEnabled(sku.getIsEnabled()).build();
                shopDiscountGoodsList.add(shopDiscountGoods);
            }
        }
        shopDiscountGoodsMapper.insert(shopDiscountGoodsList);
        if (startTime <= time) {
            discountStartAfter(shopDiscount.getDiscountId());
        } else if (endTime <= time) {
            discountEndAfter(shopDiscount.getDiscountId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(Integer id, ShopDiscountParam shopDiscountParam) {
        List<ShopDiscountParam.GoodsItemParam> goodsList = shopDiscountParam.getGoodsList();
        List<Integer> goodsIds = goodsList.stream().map(item -> item.getGoodsId()).collect(Collectors.toList());

        long startTime = DateUtils.StringToTimestamp(shopDiscountParam.getStartTime());
        long endTime = DateUtils.StringToTimestamp(shopDiscountParam.getEndTime());

        checkGoods(shopDiscountParam.getGoodsList());

        checkGoodsConflict(id, goodsIds, startTime, endTime);

        shopDiscountMapper.update(new LambdaUpdateWrapper<ShopDiscount>()
                .eq(ShopDiscount::getDiscountId, id)
                .set(ShopDiscount::getStatus, ActiveStatusEnum.NOT_ACTIVE.getStatus())
                .set(ShopDiscount::getUpdateTime, DateUtils.currTime()));
        discountEndAfter(id);

        String status = ActiveStatusEnum.NOT_ACTIVE.getStatus();
        Long time = DateUtils.currTime();

        if (startTime <= time) {
            status = ActiveStatusEnum.ACTIVE.getStatus();
        } else if (endTime <= time) {
            status = ActiveStatusEnum.END.getStatus();
        }
        //更新商品
        processDiscountGoods(id, goodsList, status);

        //更新折扣活动
        updateDiscountInfo(id, shopDiscountParam, status);

        if (startTime <= time) {
            discountStartAfter(id);
        } else if (endTime <= time) {
            discountEndAfter(id);
        }
    }


    private void checkGoodsConflict(Integer id, List<Integer> goodsIds, long startTime, long endTime) {
        Integer siteId = RequestUtils.siteId();
        // 构建查询条件
        LambdaQueryWrapper<ShopDiscountGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShopDiscountGoods::getSiteId, siteId)
                .in(ShopDiscountGoods::getGoodsId, goodsIds);
        if(id > 0){
            queryWrapper.ne(ShopDiscountGoods::getDiscountId, id);
        }

        // 查询关联的折扣活动
        List<ShopDiscountGoods> conflictGoods = shopDiscountGoodsMapper.selectList(queryWrapper);

        if (!conflictGoods.isEmpty()) {
            // 获取关联的折扣ID
            List<Integer> conflictDiscountIds = conflictGoods.stream()
                    .map(ShopDiscountGoods::getDiscountId)
                    .distinct()
                    .collect(Collectors.toList());

            // 检查时间重叠的折扣活动
            LambdaQueryWrapper<ShopDiscount> discountQuery = new LambdaQueryWrapper<>();
            discountQuery.in(ShopDiscount::getDiscountId, conflictDiscountIds)
                    .eq(ShopDiscount::getSiteId, siteId)
                    .in(ShopDiscount::getStatus, Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()))
                    .and(wrapper -> wrapper
                            .between(ShopDiscount::getStartTime, startTime, endTime)
                            .or()
                            .le(ShopDiscount::getStartTime, startTime).ge(ShopDiscount::getEndTime, endTime)
                            .or()
                            .ge(ShopDiscount::getStartTime, startTime).le(ShopDiscount::getEndTime, endTime));

            Long count = shopDiscountMapper.selectCount(discountQuery);
            if (count > 0) {
                throw new AdminException("同一商品在一个时间段内只能参加一个限时折扣活动");
            }
        }
    }

    private void processDiscountGoods(Integer discountId, List<ShopDiscountParam.GoodsItemParam> goodsList, String status) {
        Integer siteId = RequestUtils.siteId();
        // 获取当前活动的商品
        QueryWrapper<ShopDiscountGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId)
                .eq("discount_id", discountId);

        List<ShopDiscountGoods> activeGoods = shopDiscountGoodsMapper.selectList(queryWrapper);
        Set<Integer> activeGoodsIds = activeGoods.stream()
                .map(ShopDiscountGoods::getGoodsId)
                .collect(Collectors.toSet());

        // 需要删除的商品
        Set<Integer> newGoodsIds = goodsList.stream()
                .map(item -> item.getGoodsId())
                .collect(Collectors.toSet());
        Set<Integer> deleteGoodsIds = activeGoodsIds.stream()
                .filter(id -> !newGoodsIds.contains(id))
                .collect(Collectors.toSet());

        // 删除不再使用的商品
        if (!deleteGoodsIds.isEmpty()) {
            QueryWrapper<ShopDiscountGoods> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq("site_id", siteId)
                    .eq("discount_id", discountId)
                    .in("goods_id", deleteGoodsIds);
            shopDiscountGoodsMapper.delete(deleteWrapper);
        }

        // 添加/更新商品
        List<ShopDiscountGoods> discountGoodsList = new ArrayList<>();
        for (ShopDiscountParam.GoodsItemParam goods : goodsList) {
            for (ShopDiscountParam.SkuItemParam sku : goods.getSkuList()) {
                ShopDiscountGoods discountGoods = new ShopDiscountGoods();
                discountGoods.setDiscountId(discountId);
                discountGoods.setSiteId(siteId);
                discountGoods.setGoodsId(goods.getGoodsId());
                discountGoods.setSkuId(sku.getSkuId());
                discountGoods.setStatus(status);
                discountGoods.setType(sku.getDiscountType());
                discountGoods.setRate(sku.getDiscountRate());
                discountGoods.setReduceMoney(sku.getReduceMoney());
                discountGoods.setDiscountPrice(!ObjectUtil.isEmpty(sku.getDiscountPrice()) ? sku.getSpecifyPrice() : sku.getReduceMoney());
                discountGoods.setIsEnabled(sku.getIsEnabled());
                discountGoodsList.add(discountGoods);
            }
        }
        // 批量插入或更新
        for (ShopDiscountGoods goods : discountGoodsList) {
            LambdaQueryWrapper<ShopDiscountGoods> existQuery = new LambdaQueryWrapper<>();
            existQuery.eq(ShopDiscountGoods::getSiteId, siteId)
                    .eq(ShopDiscountGoods::getDiscountId, discountId)
                    .eq(ShopDiscountGoods::getGoodsId, goods.getGoodsId())
                    .eq(ShopDiscountGoods::getSkuId, goods.getSkuId());

            if (shopDiscountGoodsMapper.selectCount(existQuery) > 0) {
                shopDiscountGoodsMapper.update(goods, existQuery);
            } else {
                shopDiscountGoodsMapper.insert(goods);
            }
        }
    }

    private void updateDiscountInfo(Integer id, ShopDiscountParam shopDiscountParam, String status) {
        ShopDiscount shopDiscount = shopDiscountMapper.selectById(id);
        shopDiscount.setName(shopDiscountParam.getName());
        shopDiscount.setRemark(shopDiscountParam.getRemark());
        shopDiscount.setStartTime(DateUtils.StringToTimestamp(shopDiscountParam.getStartTime()));
        shopDiscount.setEndTime(DateUtils.StringToTimestamp(shopDiscountParam.getEndTime()));
        shopDiscount.setStatus(status);
        shopDiscount.setUpdateTime(DateUtils.currTime());
        shopDiscountMapper.updateById(shopDiscount);
    }


    @Override
    public void checkGoods(List<ShopDiscountParam.GoodsItemParam> goodsList) {

        if (ObjectUtil.isEmpty(goodsList)) {
            throw new AdminException("活动商品不能为空");
        }
        for (ShopDiscountParam.GoodsItemParam goods : goodsList) {
            if (CollectionUtils.isEmpty(goods.getSkuList())) {
                throw new AdminException("活动商品不能为空");
            }
            for (ShopDiscountParam.SkuItemParam sku : goods.getSkuList()) {
                if (ObjectUtil.isEmpty(sku.getIsEnabled())) {
                    throw new AdminException("is_enabled必传");
                }
                if (sku.getIsEnabled() == 1) {
                    if (ObjectUtil.isEmpty(sku.getDiscountType())) {
                        throw new AdminException("折扣类型不能为空");
                    }
                    String[] discountTypeArray = {"discount", "reduce", "specify"};
                    if (!Arrays.asList(discountTypeArray).contains(sku.getDiscountType())) {
                        throw new AdminException("折扣类型错误");
                    }
                    if (ObjectUtil.isEmpty(sku.getDiscountPrice())) {
                        throw new AdminException("折扣价格discount_price不能为空");
                    }
                    if (ObjectUtil.isEmpty(sku.getSpecifyPrice())) {
                        throw new AdminException("促销价不能为空");
                    }
                    if (ObjectUtil.isEmpty(sku.getDiscountRate())) {
                        throw new AdminException("打折折扣不能为空");
                    }
                    if (ObjectUtil.isEmpty(sku.getReduceMoney())) {
                        throw new AdminException("减钱金额不能为空");
                    }
                }
            }
        }
    }


    /**
     * 限时折扣删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.siteId();
        ShopDiscount shopDiscount = shopDiscountMapper.selectById(id);
        if (ObjectUtil.isEmpty(shopDiscount)) {
            throw new AdminException("活动未找到");
        }
        if (shopDiscount.getStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) {
            throw new AdminException("进行中活动不能直接删除");
        }
        shopDiscountMapper.deleteById(id);
        shopDiscountGoodsMapper.delete(new QueryWrapper<ShopDiscountGoods>().eq("discount_id", id).eq("site_id", siteId));
    }


    @Override
    public boolean setGoodsDiscount(Integer activeId) {
        ShopDiscount shopDiscount = shopDiscountMapper.selectById(activeId);
        if (ObjectUtil.isEmpty(shopDiscount)) {
            return true;
        }
        if (!shopDiscount.getStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) {
            return true;
        }

        List<ShopDiscountGoods> shopActiveGoodsList = shopDiscountGoodsMapper.selectList(new LambdaQueryWrapper<ShopDiscountGoods>().eq(ShopDiscountGoods::getDiscountId, activeId));
        List<Integer> activeGoodsIds = shopActiveGoodsList.stream().map(ShopDiscountGoods::getGoodsId).collect(Collectors.toList());

        ShopGoods discountShopGoods = new ShopGoods();
        discountShopGoods.setIsDiscount(1);
        shopGoodsMapper.update(discountShopGoods, new LambdaQueryWrapper<ShopGoods>().in(ShopGoods::getGoodsId, activeGoodsIds));

        for (ShopDiscountGoods shopDiscountGoods : shopActiveGoodsList) {
            if (shopDiscountGoods.getIsEnabled() == 1) {
                LambdaUpdateWrapper<ShopGoodsSku> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(ShopGoodsSku::getSalePrice, shopDiscountGoods.getDiscountPrice());
                updateWrapper.eq(ShopGoodsSku::getSkuId, shopDiscountGoods.getSkuId());
                shopGoodsSkuMapper.update(updateWrapper);
            }
        }

        return true;
    }

    @Override
    public boolean cancelGoodsDiscount(Integer activeId) {
        ShopDiscount shopDiscount = shopDiscountMapper.selectById(activeId);
        if (ObjectUtil.isEmpty(shopDiscount)) {
            return true;
        }
        if (shopDiscount.getStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) {
            return true;
        }

        List<ShopDiscountGoods> shopActiveGoodsList = shopDiscountGoodsMapper.selectList(new LambdaQueryWrapper<ShopDiscountGoods>().eq(ShopDiscountGoods::getDiscountId, activeId));
        List<Integer> activeGoodsIds = shopActiveGoodsList.stream().map(ShopDiscountGoods::getGoodsId).collect(Collectors.toList());

        shopGoodsMapper.update(new LambdaUpdateWrapper<ShopGoods>().set(ShopGoods::getIsDiscount, 0).in(ShopGoods::getGoodsId, activeGoodsIds));
        shopGoodsSkuMapper.update(new LambdaUpdateWrapper<ShopGoodsSku>().in(ShopGoodsSku::getGoodsId, activeGoodsIds).setSql("sale_price = price"));
        return true;
    }

    @Override
    public void discountClose(Integer activeId) {
        shopDiscountMapper.update(new LambdaUpdateWrapper<ShopDiscount>()
                .eq(ShopDiscount::getDiscountId, activeId)
                .set(ShopDiscount::getStatus, ActiveStatusEnum.CLOSE.getStatus()));
        shopDiscountGoodsMapper.update(new LambdaUpdateWrapper<ShopDiscountGoods>()
                .eq(ShopDiscountGoods::getDiscountId, activeId)
                .eq(ShopDiscountGoods::getSiteId, RequestUtils.siteId())
                .set(ShopDiscountGoods::getStatus, ActiveStatusEnum.CLOSE.getStatus()));
        cancelGoodsDiscount(activeId);
    }

    @Override
    public void batchClose(List<Integer> ids) {
        if(ObjectUtil.isNotNull(ids) && ids.size()>0){
            for(int i=0; i<ids.size(); i++){
                discountClose(ids.get(i));
            }
        }
    }

    @Override
    public void batchDelete(List<Integer> ids) {
        if(ObjectUtil.isNotNull(ids) && ids.size()>0){
            Boolean check = true;
            for(int i=0; i<ids.size(); i++){
                Integer siteId = RequestUtils.siteId();
                ShopDiscount shopDiscount = shopDiscountMapper.selectById(ids.get(i));
                if (ObjectUtil.isEmpty(shopDiscount)) {
                    check = false;
                    throw new AdminException("活动未找到");
                }
                if (shopDiscount.getStatus().equals(ActiveStatusEnum.ACTIVE.getStatus())) {
                    check = false;
                    throw new AdminException("进行中活动不能直接删除");
                }
            }
            if(check){
                shopDiscountMapper.delete(new QueryWrapper<ShopDiscount>().in("discount_id", ids).eq("site_id", RequestUtils.siteId()));
                shopDiscountGoodsMapper.delete(new QueryWrapper<ShopDiscountGoods>().in("discount_id", ids).eq("site_id", RequestUtils.siteId()));
            }
        }
    }

    @Override
    public void discountStartAfter(Integer activeId) {
        setGoodsDiscount(activeId);
    }

    @Override
    public void discountEndAfter(Integer activeId) {
        cancelGoodsDiscount(activeId);
    }

    @Override
    public PageResult<ShopDiscountOrderListVo> order(Integer activeId, PageParam pageParam, ShopDiscountOrderSearchParam searchParam) {
        MPJQueryWrapper<ShopOrderDiscount> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("od")
                .innerJoin("?_shop_order o ON o.order_id = od.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("o.*,m.nickname,p.type");
        wrapper.eq("o.site_id", RequestUtils.siteId());
        wrapper.gt("o.pay_time", 0);
        wrapper.eq("od.discount_type", "discount");
        wrapper.eq("od.discount_type_id", activeId);
        if (ObjectUtil.isNotEmpty(searchParam.getSearchName())) {
            wrapper.like("order_no", searchParam.getSearchName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(wrapper, "o.create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) {
            QueryMapperUtils.buildByTime(wrapper, "o.pay_time", searchParam.getPayTime());
        }

        IPage<ShopDiscountOrderListVo> iPage = shopOrderDiscountMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountOrderListVo.class, wrapper);
        for (ShopDiscountOrderListVo vo : iPage.getRecords()) {
            ShopDiscountOrderListVo.Member memberVo = new ShopDiscountOrderListVo.Member();
            BeanUtils.copyProperties(vo, memberVo);

            ShopDiscountOrderListVo.Pay payVo = new ShopDiscountOrderListVo.Pay();
            BeanUtils.copyProperties(vo, payVo);

            vo.setMember(memberVo);
            vo.setPay(payVo);
        }
        return PageResult.build(iPage);
    }

    @Override
    public PageResult<ShopDiscountMemberListVo> member(PageParam pageParam, Integer discountId, ShopDiscountMemberSearchParam shopDiscountMemberSearchParam) {

        MPJQueryWrapper<ShopOrderDiscount> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("od")
                .innerJoin("?_shop_order o ON o.order_id = od.order_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("count(o.order_id) as member_count, sum(order_money) as active_order_money, MAX(o.pay_time) as create_time, m.member_id, m.nickname, m.mobile, m.headimg");
        wrapper.eq("od.discount_type", "discount");
        wrapper.eq("od.discount_type_id", discountId);
        wrapper.eq("od.site_id", RequestUtils.siteId());
        wrapper.gt("o.pay_time", 0);
        wrapper.groupBy("o.member_id");
        wrapper.orderByDesc("create_time");

        IPage<ShopDiscountMemberListVo> iPage = shopOrderDiscountMapper.selectJoinPage(new Page<>(pageParam.getPage(), pageParam.getLimit()), ShopDiscountMemberListVo.class, wrapper);
        for (ShopDiscountMemberListVo vo : iPage.getRecords()) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
        }
        return PageResult.build(iPage);
    }

    @Override
    public PageResult<ShopDiscountGoodsListVo> goods(PageParam pageParam, Integer activeId, ShopDiscountGoodsSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopDiscountGoods> wrapper = new MPJQueryWrapper<>();
        wrapper.setAlias("discount_goods")
                .innerJoin("?_shop_goods goods ON discount_goods.goods_id = goods.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_shop_goods_sku goods_sku ON discount_goods.sku_id = goods_sku.sku_id".replace("?_", GlobalConfig.tablePrefix));
        wrapper.select("goods.goods_name,\n" +
                "                goods.goods_cover,\n" +
                "                discount_goods_id,\n" +
                "                discount_id,\n" +
                "                discount_goods.goods_id,\n" +
                "                discount_goods.sku_id,\n" +
                "                discount_goods.site_id,\n" +
                "                goods_sku.price,\n" +
                "                SUM(discount_goods.order_money) as order_money,\n" +
                "                SUM(discount_goods.order_num) as order_num,\n" +
                "                SUM(discount_goods.member_num) as member_num,\n" +
                "                SUM(discount_goods.success_num) as success_num");
        wrapper.eq("discount_goods.site_id", RequestUtils.siteId());
        wrapper.eq("discount_goods.discount_id", activeId);
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            wrapper.like("goods.goods_name", searchParam.getKeyword());
        }
        wrapper.groupBy("discount_goods.goods_id");
        IPage<ShopDiscountGoodsListVo> iPage = shopDiscountGoodsMapper.selectJoinPage(new Page<>(page, limit), ShopDiscountGoodsListVo.class, wrapper);

        return PageResult.build(iPage);
    }

    @Override
    public void setConfig(String key, JSONArray data) {
        coreConfigService.setConfig(RequestUtils.siteId(), key, data);
    }

    @Override
    public JSONArray getDiscountBannerConfig() {
        return coreConfigService.getConfigArrayValue(RequestUtils.siteId(), "SHOP_DISCOUNT_BANNER_CONFIG");
    }


    @Override
    @Transactional
    public void orderPayAfter(ShopOrder order) {
        Integer siteId = order.getSiteId();
        //查询订单使用的所有折扣
        List<ShopOrderDiscount> orderDiscounts = shopOrderDiscountMapper.selectList(
                new LambdaQueryWrapper<ShopOrderDiscount>()
                        .eq(ShopOrderDiscount::getOrderId, order.getOrderId())
                        .eq(ShopOrderDiscount::getDiscountType, ActiveClassEnum.DISCOUNT.getType()));
        if (ObjectUtil.isEmpty(orderDiscounts)) {
            return;
        }
        for (ShopOrderDiscount orderDiscount : orderDiscounts) {
            //检查会员是否首次参与该折扣活动
            boolean isFirstParticipation = shopOrderDiscountMapper.selectCount(
                    new LambdaQueryWrapper<ShopOrderDiscount>()
                            .eq(ShopOrderDiscount::getDiscountType, ActiveClassEnum.DISCOUNT.getType())
                            .eq(ShopOrderDiscount::getDiscountTypeId, orderDiscount.getDiscountTypeId())
                            .ne(ShopOrderDiscount::getOrderId, orderDiscount.getOrderId())
                            .eq(ShopOrderDiscount::getMemberId, orderDiscount.getMemberId())
            ) == 0;

            LambdaUpdateWrapper<ShopDiscountGoods> goodsUpdateWrapper = new LambdaUpdateWrapper<ShopDiscountGoods>()
                    .eq(ShopDiscountGoods::getSiteId, siteId)
                    .eq(ShopDiscountGoods::getDiscountId, orderDiscount.getDiscountTypeId())
                    .eq(ShopDiscountGoods::getGoodsId, orderDiscount.getGoodsId())
                    .eq(ShopDiscountGoods::getSkuId, orderDiscount.getSkuId())
                    .setSql("order_money = order_money + " + orderDiscount.getMoney())
                    .setSql("order_num = order_num + 1")
                    .setSql("success_num = success_num + 1")
                    .setSql(isFirstParticipation, "member_num = member_num + 1");
            shopDiscountGoodsMapper.update(goodsUpdateWrapper);


            LambdaUpdateWrapper<ShopDiscount> updateWrapper = new LambdaUpdateWrapper<ShopDiscount>()
                    .eq(ShopDiscount::getSiteId, siteId)
                    .eq(ShopDiscount::getDiscountId, orderDiscount.getDiscountTypeId())
                    .setSql("order_money = order_money + " + orderDiscount.getMoney())
                    .setSql("order_num = order_num + 1")
                    .setSql("success_num = success_num + 1")
                    .setSql(isFirstParticipation, "member_num = member_num + 1");

            shopDiscountMapper.update(updateWrapper);
        }
    }

    @Override
    public Map<String, Object> checkGoodsData(ShopDiscountCheckGoodsParam body) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 1);
        error.put("data", new ArrayList<>());
        Integer siteId = RequestUtils.siteId();

        // 获取时间参数
        long startTime = DateUtils.StringToTimestamp(body.getStartTime());
        long endTime = DateUtils.StringToTimestamp(body.getEndTime());

        // 查询在指定时间段内有活动的商品ID
        LambdaQueryWrapper<ShopDiscount> qw = new LambdaQueryWrapper<ShopDiscount>().select(ShopDiscount::getDiscountId)
                .eq(ShopDiscount::getSiteId, siteId)
                .in(ShopDiscount::getStatus, Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()))
                .and(wrapper -> wrapper
                        .between(ShopDiscount::getStartTime, startTime, endTime)
                        .or()
                        .between(ShopDiscount::getEndTime, startTime, endTime)
                        .or()
                        .le(ShopDiscount::getStartTime, startTime).ge(ShopDiscount::getEndTime, endTime)
                        .or()
                        .ge(ShopDiscount::getStartTime, startTime).le(ShopDiscount::getEndTime, endTime));
        List<ShopDiscount> shopDiscounts = shopDiscountMapper.selectList(qw);
        if (CollectionUtils.isEmpty(shopDiscounts)) {
            return error;
        }
        List<Integer> discountIds = shopDiscounts.stream().map(ShopDiscount::getDiscountId).distinct().collect(Collectors.toList());
        LambdaQueryWrapper<ShopDiscountGoods> queryWrapper = new LambdaQueryWrapper<ShopDiscountGoods>().select(ShopDiscountGoods::getGoodsId)
                .eq(ShopDiscountGoods::getSiteId, siteId)
                .in(ShopDiscountGoods::getStatus, Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()))
                .in(ShopDiscountGoods::getDiscountId, discountIds);
        List<ShopDiscountGoods> activeGoodsList = shopDiscountGoodsMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(activeGoodsList)) {
            return error;
        }
        List<Integer> goodsIds = activeGoodsList.stream().map(ShopDiscountGoods::getGoodsId).collect(Collectors.toList());

        List<Integer> goodsIdsToCheck = new ArrayList<>(body.getGoodsIds());
        if (body.getDiscountId() != null) {
            List<Integer> currentDiscountGoodsIds = shopDiscountGoodsMapper.selectList(
                    new LambdaQueryWrapper<ShopDiscountGoods>()
                            .select(ShopDiscountGoods::getGoodsId)
                            .eq(ShopDiscountGoods::getSiteId, siteId)
                            .eq(ShopDiscountGoods::getDiscountId, body.getDiscountId())
            ).stream().map(ShopDiscountGoods::getGoodsId).collect(Collectors.toList());
            goodsIdsToCheck.removeAll(currentDiscountGoodsIds);
        }

        //找出真正冲突的商品
        List<Integer> intersectGoodsIds = goodsIdsToCheck.stream()
                .filter(goodsIds::contains)
                .collect(Collectors.toList());

        //获取冲突商品的详细信息
        if (!intersectGoodsIds.isEmpty()) {
            List<Map<String, Object>> errorList = new ArrayList<>();

            for (Integer goodsId : intersectGoodsIds) {
                ShopDiscountGoods discountGoods = shopDiscountGoodsMapper.selectOne(
                        new LambdaQueryWrapper<ShopDiscountGoods>()
                                .eq(ShopDiscountGoods::getSiteId, siteId)
                                .eq(ShopDiscountGoods::getGoodsId, goodsId)
                                .in(ShopDiscountGoods::getStatus, Arrays.asList(ActiveStatusEnum.NOT_ACTIVE.getStatus(), ActiveStatusEnum.ACTIVE.getStatus()))
                                .last("LIMIT 1")
                );

                if (discountGoods != null) {
                    ShopDiscount discount = shopDiscountMapper.selectById(discountGoods.getDiscountId());
                    if (discount != null) {
                        Map<String, Object> errorItem = new HashMap<>();
                        errorItem.put("goods_id", goodsId);
                        errorItem.put("error_msg", "该商品已参加【" + discount.getName() + "】活动");
                        errorList.add(errorItem);
                    }
                }
            }

            if (!errorList.isEmpty()) {
                error.put("code", -1);
                error.put("data", errorList);
            }
        }
        return error;
    }
}
