package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.service.admin.goods.IGoodsCategoryService;
import com.niu.shop.service.admin.marketing.ICouponService;
import com.niu.shop.service.admin.marketing.IShopCouponGoodsService;
import com.niu.shop.service.admin.marketing.param.ShopCouponParam;
import com.niu.shop.service.admin.marketing.param.ShopCouponSearchParam;
import com.niu.shop.service.admin.marketing.param.ShopCouponSelectParam;
import com.niu.shop.service.admin.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponMemberListVo;
import com.niu.shop.service.admin.marketing.vo.ShopCouponSelectVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class
CouponServiceImpl implements ICouponService {

    @Resource
    private IGoodsCategoryService goodsCategoryService;

    @Resource
    ShopCouponMapper shopCouponMapper;

    @Resource
    ShopCouponGoodsMapper shopCouponGoodsMapper;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;

    @Resource
    IShopCouponGoodsService shopCouponGoodsService;


    @Override
    public JSONObject getInit() {
        JSONArray tree = goodsCategoryService.tree();
        JSONArray categoryList = new JSONArray();
        tree.forEach(json -> {
                    JSONObject vo = new JSONObject();
                    JSONObject jsonObj = (JSONObject) json;

                    vo.put("value", jsonObj.getInt("category_id"));
                    vo.put("label", jsonObj.getStr("category_name"));
                    if (jsonObj.getJSONArray("children") != null) {
                        JSONArray children = new JSONArray();
                        jsonObj.getJSONArray("children").forEach(child -> {

                            JSONObject childJsonObj = (JSONObject) child;
                            JSONObject childVo = new JSONObject();
                            childVo.put("value", childJsonObj.getInt("category_id"));
                            childVo.put("label", childJsonObj.getStr("category_name"));
                            children.add(childVo);
                        });
                        vo.put("children", children);
                    }
                    categoryList.add(vo);
                }
        );
        JSONObject result = new JSONObject();
        result.put("goods_category_tree", categoryList);
        return result;

    }

    @Override
    public PageResult<ShopCouponListVo> getPage(PageParam pageParam, ShopCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        queryWrapper.orderByDesc("id");

        IPage<ShopCoupon> iPage = shopCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopCouponListVo> list = new LinkedList<>();
        for (ShopCoupon item : iPage.getRecords()) {
            ShopCouponListVo vo = new ShopCouponListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setReceiveUseCount(shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>().eq("coupon_id", item.getId()).gt("use_time", 1)));
            vo.setStartTime(item.getStartTime() == null || item.getStartTime() == 0 ? "0" : DateUtils.timestampToString(item.getStartTime()));
            vo.setEndTime(item.getEndTime() == null || item.getEndTime() == 0 ? "0" : DateUtils.timestampToString(item.getEndTime()));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public ShopCouponSelectVo getSelectPage(PageParam pageParam, ShopCouponSelectParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) queryWrapper.like("title", searchParam.getTitle());
        //去除重复
        ShopCouponSelectVo voRes = new ShopCouponSelectVo();
        if (ObjectUtil.isNotEmpty(searchParam.getVerifyCouponIds())) {
            List<Integer> couponIds = new ArrayList<>();
            List<ShopCoupon> couponList = shopCouponMapper.selectList(queryWrapper);
            for (ShopCoupon item : couponList) {
                if (searchParam.getVerifyCouponIds().contains(item.getId())) {
                    couponIds.add(item.getId());
                }
            }
            voRes.setVerifyCouponIds(couponIds);

        } else {
            voRes.setVerifyCouponIds(new ArrayList<>());
        }

        if (ObjectUtil.isNotEmpty(searchParam.getVerifyCouponIds()))
            queryWrapper.in("id", searchParam.getVerifyCouponIds());
        queryWrapper.orderByDesc("id");

        IPage<ShopCoupon> iPage = shopCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopCouponSelectVo.ShopCouponListVo> list = new LinkedList<>();
        for (ShopCoupon item : iPage.getRecords()) {
            ShopCouponSelectVo.ShopCouponListVo vo = new ShopCouponSelectVo.ShopCouponListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setReceiveUseCount(shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>().eq("coupon_id", item.getId()).gt("use_time", 1)));
            list.add(vo);
        }

        voRes.setPerPage(limit);
        voRes.setTotal(iPage.getTotal());
        voRes.setData(list);
        voRes.setCurrentPage(page);

        return voRes;

    }

    @Override
    public List<ShopCouponListVo> getSelectedList(String couponIds) {

        QueryWrapper<ShopCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (!couponIds.equals("")) queryWrapper.in("id", couponIds);

        queryWrapper.orderByDesc("id");
        List<ShopCoupon> list = shopCouponMapper.selectList(queryWrapper);
        List<ShopCouponListVo> voList = new LinkedList<>();
        for (ShopCoupon item : list) {
            ShopCouponListVo vo = new ShopCouponListVo();
            BeanUtils.copyProperties(item, vo);
            voList.add(vo);
        }
        return voList;

    }

    @Override
    public ShopCouponInfoVo getInfo(Integer couponId) {
        ShopCoupon model = shopCouponMapper.selectOne(
                new QueryWrapper<ShopCoupon>()
                        .eq("id", couponId)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));
        ShopCouponInfoVo vo = new ShopCouponInfoVo();
        if (ObjectUtil.isEmpty(model)) return null;
        BeanUtils.copyProperties(model, vo);
        vo.setStartTime(model.getStartTime() == null || model.getStartTime() == 0 ? "0" : DateUtils.timestampToString(model.getStartTime()));
        vo.setEndTime(model.getEndTime() == null || model.getEndTime() == 0 ? "0" : DateUtils.timestampToString(model.getEndTime()));

        if (vo.getRemainCount() == -1) {
            vo.setLimit(2);
        } else {
            vo.setLimit(1);
        }

        if (vo.getMinConditionMoney().compareTo(new BigDecimal("0.00")) == 0) {
            vo.setThreshold(2);
        } else {
            vo.setThreshold(1);
        }

        if (model.getLimitCount() == 0) {
            vo.setLimitCount("");
        } else {
            vo.setLimitCount(model.getLimitCount().toString());
        }

        if (vo.getType() == 2) {
            List<ShopCouponGoods> list = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>().select("category_id").eq("coupon_id", couponId));
            List<Integer> newList = list.stream().map(ShopCouponGoods::getCategoryId).collect(Collectors.toList());
            vo.setGoodsCategoryIds(newList);
        } else {
            vo.setGoodsCategoryIds(new ArrayList<>());
        }

        if (vo.getType() == 3) {
            List<ShopCouponGoods> list = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>().select("goods_id").eq("coupon_id", couponId));
            List<Integer> newList = list.stream().map(ShopCouponGoods::getGoodsId).collect(Collectors.toList());
            vo.setGoodsIds(newList);
        } else {
            vo.setGoodsIds(new ArrayList<>());
        }

        if (vo.getRemainCount() == -1) {
            vo.setRemainCount(1000);
            vo.setSumCount("不限量");
        } else {
            vo.setSumCount(Integer.toString(vo.getRemainCount() + vo.getReceiveCount()));
        }

        Long receiveCount = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId).gt("use_time", 1));
        if (ObjectUtil.isNotEmpty(receiveCount)) {
            vo.setReceiveUseCount(receiveCount);
        } else {
            vo.setReceiveUseCount(0L);
        }
        Long expireCount = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId).eq("use_time", 0).le("expire_time", System.currentTimeMillis() / 1000));
        if (ObjectUtil.isNotEmpty(expireCount)) {
            vo.setReceiveExpireCount(expireCount);
        } else {
            vo.setReceiveExpireCount(0L);
        }

        return vo;
    }

    @Transactional
    @Override
    public void add(ShopCouponParam addParam) {

        Integer siteId = RequestUtils.siteId();
        ShopCoupon model = new ShopCoupon();
        model.setSiteId(siteId);

        if (addParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(addParam.getMinConditionMoney());
        }

        if (addParam.getReceiveType() != 2) {
            if (addParam.getReceiveTypeTime() == 1) {
                if (ObjectUtil.isNotEmpty(addParam.getReceiveTime())) {
                    String[] receiveTime = addParam.getReceiveTime();
                    Long startTime = (receiveTime[0] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[0]);
                    Long endTime = (receiveTime[1] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[1]);
                    model.setStartTime(startTime);
                    model.setEndTime(endTime);
                    Long nowTime = System.currentTimeMillis() / 1000;
                    if (nowTime < startTime) {
                        model.setStatus(0);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(addParam.getValidTime())) {
            Long validStartTime = System.currentTimeMillis() / 1000;
            Long validEndTime = DateUtils.StringToTimestamp(addParam.getValidTime());
            if (validStartTime > validEndTime) throw new AdminException("发放时间不能小于当前时间");
            model.setValidStartTime(validStartTime);
            model.setValidEndTime(validEndTime);
        }

        if (addParam.getLimit() == 2) {
            model.setRemainCount(-1);
        } else {
            model.setRemainCount(addParam.getRemainCount());
        }

        model.setTitle(addParam.getTitle());
        model.setLimitCount(addParam.getLimitCount());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setPrice(addParam.getPrice());
        model.setType(addParam.getType());
        model.setReceiveType(addParam.getReceiveType());
        model.setValidType(addParam.getValidType());
        model.setLength(addParam.getLength());
        shopCouponMapper.insert(model);
        if (ObjectUtil.isNotEmpty(addParam.getGoodsIds()) || ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {

            if (ObjectUtil.isNotEmpty(addParam.getGoodsIds())) {
                Integer[] goodsIds = addParam.getGoodsIds();
                List<ShopCouponGoods> list = new ArrayList<>();
                for (Integer goodsId : goodsIds) {
                    ShopCouponGoods shopCouponGoods = new ShopCouponGoods();
                    shopCouponGoods.setSiteId(siteId);
                    shopCouponGoods.setCouponId(model.getId());
                    shopCouponGoods.setGoodsId(goodsId);
                    list.add(shopCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }
            if (ObjectUtil.isNotEmpty(addParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = addParam.getGoodsCategoryIds();
                List<ShopCouponGoods> list = new ArrayList<>();
                for (Object goodsCategoryId : goodsCategoryIds) {
                    ShopCouponGoods shopCouponGoods = new ShopCouponGoods();
                    shopCouponGoods.setSiteId(siteId);
                    shopCouponGoods.setCouponId(model.getId());
                    JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                    shopCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));

                    list.add(shopCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }

        }
    }

    @Override
    public void edit(Integer couponId, ShopCouponParam shopCouponParam) {
        ShopCoupon coupon = shopCouponMapper.selectById(shopCouponParam.getId());
        if (!coupon.getStatus().equals(CouponStatusEnum.WAIT_START.getStatus())) {
            throw new RuntimeException("只能编辑未开始状态的优惠券");
        }

        Integer siteId = RequestUtils.siteId();
        ShopCoupon model = new ShopCoupon();
        model.setSiteId(siteId);
        model.setId(shopCouponParam.getId());
        if (shopCouponParam.getThreshold() == 2) {
            model.setMinConditionMoney(new BigDecimal("0.00"));
        } else {
            model.setMinConditionMoney(shopCouponParam.getMinConditionMoney());
        }

        if (shopCouponParam.getReceiveType() != 2) {
            if (shopCouponParam.getReceiveTypeTime() == 1) {
                if (ObjectUtil.isNotEmpty(shopCouponParam.getReceiveTime())) {
                    String[] receiveTime = shopCouponParam.getReceiveTime();
                    Long startTime = (receiveTime[0] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[0]);
                    Long endTime = (receiveTime[1] == null) ? 0 : DateUtils.StringToTimestamp(receiveTime[1]);
                    model.setStartTime(startTime);
                    model.setEndTime(endTime);
                    Long nowTime = System.currentTimeMillis() / 1000;
                    if (nowTime < startTime) {
                        model.setStatus(0);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(shopCouponParam.getValidTime())) {
            Long validStartTime = System.currentTimeMillis() / 1000;
            Long validEndTime = DateUtils.StringToTimestamp(shopCouponParam.getValidTime());
            if (validStartTime > validEndTime) throw new AdminException("发放时间不能小于当前时间");
            model.setValidStartTime(validStartTime);
            model.setValidEndTime(validEndTime);
        }


        if (shopCouponParam.getLimit() == 2) {
            model.setRemainCount(-1);
        } else {
            model.setRemainCount(shopCouponParam.getRemainCount());
        }

        model.setTitle(shopCouponParam.getTitle());
        model.setLimitCount(shopCouponParam.getLimitCount());
        model.setPrice(shopCouponParam.getPrice());
        model.setType(shopCouponParam.getType());
        model.setReceiveType(shopCouponParam.getReceiveType());
        model.setValidType(shopCouponParam.getValidType());
        model.setLength(shopCouponParam.getLength());
        shopCouponGoodsMapper.delete(new QueryWrapper<ShopCouponGoods>().eq("coupon_id", model.getId()));
        shopCouponMapper.updateById(model);

        if (ObjectUtil.isNotEmpty(shopCouponParam.getGoodsIds()) || ObjectUtil.isNotEmpty(shopCouponParam.getGoodsCategoryIds())) {
            if (ObjectUtil.isNotEmpty(shopCouponParam.getGoodsIds())) {
                Integer[] goodsIds = shopCouponParam.getGoodsIds();
                List<ShopCouponGoods> list = new ArrayList<>();
                for (Integer goodsId : goodsIds) {
                    ShopCouponGoods shopCouponGoods = new ShopCouponGoods();
                    shopCouponGoods.setSiteId(siteId);
                    shopCouponGoods.setCouponId(model.getId());
                    shopCouponGoods.setGoodsId(goodsId);
                    list.add(shopCouponGoods);
                }

                shopCouponGoodsService.insertAll(list);
            }
            if (ObjectUtil.isNotEmpty(shopCouponParam.getGoodsCategoryIds())) {
                JSONArray goodsCategoryIds = shopCouponParam.getGoodsCategoryIds();
                List<ShopCouponGoods> list = new ArrayList<>();
                for (Object goodsCategoryId : goodsCategoryIds) {
                    ShopCouponGoods shopCouponGoods = new ShopCouponGoods();
                    shopCouponGoods.setSiteId(siteId);
                    shopCouponGoods.setCouponId(model.getId());
                    JSONArray goodsCategoryArr = (JSONArray) goodsCategoryId;
                    shopCouponGoods.setCategoryId(goodsCategoryArr.getInt(goodsCategoryArr.toArray().length - 1));

                    list.add(shopCouponGoods);
                }
                shopCouponGoodsService.insertAll(list);
            }
        }
    }

    @Override
    public void delete(Integer couponId) {
        ShopCoupon shopCoupon = shopCouponMapper.selectById(couponId);
        if (ObjectUtil.isEmpty(shopCoupon)) throw new AdminException("优惠券不存在");
        if (shopCoupon.getStatus() == CouponStatusEnum.NORMAL.getStatus()) {
            ShopCouponMember shopCouponMember = shopCouponMemberMapper.selectOne(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId));
            if (ObjectUtil.isNotEmpty(shopCouponMember)) {
                throw new AdminException("优惠券已被领取，无法删除");
            }
        }
        shopCouponMapper.deleteById(couponId);
        shopCouponGoodsMapper.delete(new QueryWrapper<ShopCouponGoods>().eq("coupon_id", couponId));
        shopCouponMemberMapper.delete(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId).eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
    }

    @Override
    public PageResult<ShopCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopCouponMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("c")
                .innerJoin("?_member m ON c.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("c.*,m.username,m.mobile,m.nickname,m.member_no");
        queryWrapper.eq("c.site_id", RequestUtils.siteId());
        queryWrapper.eq("c.coupon_id", id);

        if (!keywords.isEmpty()) {
            queryWrapper.like("m.username", keywords).or()
                    .like("m.nickname", keywords).or()
                    .like("m.mobile", keywords);
        }

        IPage<ShopCouponMemberListVo> iPage = shopCouponMemberMapper.selectJoinPage(new Page<>(page, limit), ShopCouponMemberListVo.class, queryWrapper);
        for (ShopCouponMemberListVo vo : iPage.getRecords()) {
            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            BeanUtils.copyProperties(vo, memberVo);
            vo.setMember(memberVo);
        }

        return PageResult.build(iPage);
    }

    @Override
    public void setStatus(Integer couponId, Integer status) {

        ShopCoupon shopCoupon = new ShopCoupon();
        shopCoupon.setReceiveStatus(status);
        shopCouponMapper.update(shopCoupon, new QueryWrapper<ShopCoupon>().eq("id", couponId).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void couponInvalid(Integer couponId) {

        ShopCoupon shopCoupon = new ShopCoupon();
        shopCoupon.setStatus(CouponStatusEnum.INVALID.getStatus());
        shopCouponMapper.update(shopCoupon, new QueryWrapper<ShopCoupon>().eq("id", couponId).eq("site_id", RequestUtils.siteId()));
    }

}
