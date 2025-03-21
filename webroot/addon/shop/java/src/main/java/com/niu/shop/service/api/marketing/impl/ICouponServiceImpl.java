package com.niu.shop.service.api.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.enums.coupon.*;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.service.api.marketing.ICouponService;
import com.niu.shop.service.api.marketing.param.ShopCouponReceiveParam;
import com.niu.shop.service.api.marketing.param.ShopCouponSearchParam;
import com.niu.shop.service.api.marketing.vo.ShopCouponInfoVo;
import com.niu.shop.service.api.marketing.vo.ShopCouponListVo;
import com.niu.shop.service.api.marketing.vo.ShopCouponMemberListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ICouponServiceImpl implements ICouponService {

    @Resource
    ShopCouponMapper shopCouponMapper;

    @Resource
    ShopCouponGoodsMapper shopCouponGoodsMapper;

    @Resource
    ShopCouponMemberMapper shopCouponMemberMapper;


    @Override
    public PageResult<ShopCouponListVo> getPage(PageParam pageParam, ShopCouponSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        queryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());

        //根据时间查询
        Long nowTime = System.currentTimeMillis() / 1000;
        queryWrapper.and(i -> i.and(j -> j.le("start_time", nowTime).ge("end_time", nowTime))
                .or(j -> j.eq("start_time", 0).eq("end_time", 0)));

        //根据类型查询
        List<ShopCouponGoods> couponGoodsList = new ArrayList<>();
        List<ShopCouponGoods> couponGoodsCategoryList = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(searchParam.getGoodsId())) {
            couponGoodsList = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                    .eq("site_id", RequestUtils.siteId())
                    .eq("goods_id", searchParam.getGoodsId()));
        }

        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            couponGoodsCategoryList = shopCouponGoodsMapper.selectList(new QueryWrapper<ShopCouponGoods>()
                    .eq("site_id", RequestUtils.siteId())
                    .in("category_id", searchParam.getCategoryId()));

        }

        //商品券
        if (couponGoodsList.size() > 0 && couponGoodsCategoryList.size() == 0) {
            List<Integer> goodsIds = couponGoodsList.stream().map(ShopCouponGoods::getCouponId).collect(Collectors.toList());
            queryWrapper.and(i -> i.and(j -> j.in("id", goodsIds).eq("type", CouponTypeEnum.GOODS.getType()))
                    .or(j -> j.eq("type", CouponTypeEnum.ALL.getType())));
        }

        //分类券
        if (couponGoodsCategoryList.size() > 0 && couponGoodsList.size() == 0) {
            List<Integer> categoryIds = couponGoodsCategoryList.stream().map(ShopCouponGoods::getCouponId).collect(Collectors.toList());
            queryWrapper.and(i -> i.and(j -> j.in("id", categoryIds).eq("type", CouponTypeEnum.CATEGORY.getType()))
                    .or(j -> j.eq("type", CouponTypeEnum.ALL.getType())));
        }

        //全部券
        if (couponGoodsList.size() > 0 && couponGoodsCategoryList.size() > 0) {
            List<Integer> goodsIds = couponGoodsList.stream().map(ShopCouponGoods::getCouponId).collect(Collectors.toList());
            List<Integer> categoryIds = couponGoodsCategoryList.stream().map(ShopCouponGoods::getCouponId).collect(Collectors.toList());
            queryWrapper.and(i -> i.and(j -> j.in("id", goodsIds).eq("type", CouponTypeEnum.GOODS.getType()))
                    .or(j -> j.eq("type", CouponTypeEnum.ALL.getType()))
                    .or(j -> j.in("id", categoryIds).eq("type", CouponTypeEnum.CATEGORY.getType())));
        }

        if (ObjectUtil.isNotEmpty(searchParam.getType())) queryWrapper.eq("type", searchParam.getType());

        //排序
        if (searchParam.getOrder().equals("create_time") || searchParam.getOrder().equals("price")) {
            if (searchParam.getSort().equals("desc")) {
                queryWrapper.orderByDesc(searchParam.getOrder());
            } else if (searchParam.getSort().equals("asc")) {
                queryWrapper.orderByAsc(searchParam.getOrder());
            }
        } else {
            queryWrapper.orderByDesc("id");
        }

        IPage<ShopCoupon> iPage = shopCouponMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<ShopCouponListVo> list = new LinkedList<>();
        for (ShopCoupon item : iPage.getRecords()) {
            ShopCouponListVo vo = new ShopCouponListVo();
            BeanUtils.copyProperties(item, vo);
            if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
                Long couponMemberCount = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("member_id", RequestUtils.memberId())
                        .eq("coupon_id", item.getId()));
                if (couponMemberCount > 0) {
                    vo.setIsReceive(1);
                    vo.setMemberReceiveCount(couponMemberCount);
                } else {
                    vo.setIsReceive(0);
                    vo.setMemberReceiveCount(0L);
                }
            } else {
                vo.setIsReceive(0);
                vo.setMemberReceiveCount(0L);
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
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
    public ShopCouponInfoVo getDetail(Integer couponId) {
        ShopCoupon model = shopCouponMapper.selectOne(
                new QueryWrapper<ShopCoupon>()
                        .eq("id", couponId)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));
        ShopCouponInfoVo vo = new ShopCouponInfoVo();
        if (ObjectUtil.isEmpty(model)) return null;
        BeanUtils.copyProperties(model, vo);

        if (vo.getRemainCount() == -1) {
            vo.setSumCount("-1");
        } else {
            vo.setSumCount(Integer.toString(vo.getRemainCount() + vo.getReceiveCount()));
        }

        if (ObjectUtil.isNotEmpty(RequestUtils.memberId())) {
            Long couponMemberCount = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                    .eq("member_id", RequestUtils.memberId()).eq("coupon_id", couponId));

            if (couponMemberCount > 0) {
                vo.setIsReceive(1);
                vo.setMemberReceiveCount(couponMemberCount.intValue());
            }
        }

        return vo;
    }

    @Override
    @Transactional
    public void receive(ShopCouponReceiveParam shopCouponReceiveParam) {

        Integer memberId = RequestUtils.memberId();
        List<String> receiveType = this.getReceiveType();
        if (!receiveType.contains(shopCouponReceiveParam.getType())) {
            throw new ApiException("优惠券领取方式不存在");
        }

        //判断是否领取过
        Long count = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                .eq("site_id", RequestUtils.siteId())
                .eq("coupon_id", shopCouponReceiveParam.getCouponId())
                .eq("member_id", memberId)
                .eq("receive_type", shopCouponReceiveParam.getType()));

        //判断优惠券是否充足
        ShopCoupon shopCoupon = shopCouponMapper.selectOne(new QueryWrapper<ShopCoupon>().eq("site_id", RequestUtils.siteId()).eq("id", shopCouponReceiveParam.getCouponId()));

        if (ObjectUtil.isEmpty(shopCoupon)) throw new ApiException("优惠券已领完");
        if (shopCoupon.getStatus() == -1) throw new ApiException("优惠券已失效");
        if (count.intValue() >= shopCoupon.getLimitCount()) throw new ApiException("领用次数已达到上限");
        if (shopCoupon.getRemainCount() == 0) throw new ApiException("优惠券已领完");
        Long time = System.currentTimeMillis() / 1000;
        if (shopCoupon.getStartTime() > 0) {
            if (shopCoupon.getStartTime() > time) throw new ApiException("优惠券未开始");
            if (shopCoupon.getEndTime() < time) throw new ApiException("优惠券已过期");
        }
        if (shopCoupon.getValidEndTime() > 0) {
            if (shopCoupon.getValidStartTime() > time) throw new ApiException("优惠券未开始");
            if (shopCoupon.getValidEndTime() < time) throw new ApiException("优惠券已过期");
        }
        ShopCoupon updateCoupon = new ShopCoupon();

        if (shopCoupon.getRemainCount() != -1) {
            updateCoupon.setRemainCount(shopCoupon.getRemainCount() - shopCouponReceiveParam.getNumber());
            updateCoupon.setReceiveCount(shopCoupon.getReceiveCount() + shopCouponReceiveParam.getNumber());
        } else {
            updateCoupon.setReceiveCount(shopCoupon.getReceiveCount() + shopCouponReceiveParam.getNumber());
        }
        shopCouponMapper.update(updateCoupon, new QueryWrapper<ShopCoupon>().eq("id", shopCoupon.getId()));

        Long expireTime;
        if (shopCoupon.getValidType() == 1) {
            expireTime = time + Long.valueOf(86400 * shopCoupon.getLength());
        } else {
            expireTime = shopCoupon.getValidEndTime();
        }
        ShopCouponMember shopCouponMember = new ShopCouponMember();
        shopCouponMember.setSiteId(RequestUtils.siteId());
        shopCouponMember.setCouponId(shopCoupon.getId());
        shopCouponMember.setMemberId(memberId);
        shopCouponMember.setCreateTime(time);
        shopCouponMember.setExpireTime(expireTime);
        shopCouponMember.setReceiveType(shopCouponReceiveParam.getType());
        shopCouponMember.setType(shopCoupon.getType().toString());
        shopCouponMember.setTitle(shopCoupon.getTitle());
        shopCouponMember.setPrice(shopCoupon.getPrice());
        shopCouponMember.setStatus(CouponMemberStatusEnum.WAIT_USE.getStatus());
        shopCouponMember.setMinConditionMoney(shopCoupon.getMinConditionMoney());
        shopCouponMemberMapper.insert(shopCouponMember);
    }

    @Override
    public PageResult<ShopCouponMemberListVo> getMemberPage(PageParam pageParam, Integer status, String type) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<ShopCouponMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (type != null && !type.equals("all")) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderByDesc("id");

        IPage<ShopCouponMember> iPage = shopCouponMemberMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopCouponMemberListVo> list = new LinkedList<>();
        for (ShopCouponMember item : iPage.getRecords()) {
            ShopCouponMemberListVo vo = new ShopCouponMemberListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<JSONObject> getCouponCountByStatus() {

        List<JSONObject> list = new ArrayList<>();

        Map<Integer, String> statusMap = CouponMemberStatusEnum.getMap();

        for (Map.Entry<Integer, String> entry : statusMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            Long count = shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                    .eq("member_id", RequestUtils.memberId())
                    .eq("status", entry.getKey()));
            if (count == null) {
                count = 0L;
            }
            jsonObject.put("status", entry.getKey());
            jsonObject.put("status_name", entry.getValue());
            jsonObject.put("count", count);
            list.add(jsonObject);
        }

        return list;
    }

    @Override
    public Long getMemberCount() {

        return shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                .eq("member_id", RequestUtils.memberId()));
    }

    @Override
    public Long getMemberCount(Integer status) {

        return shopCouponMemberMapper.selectCount(new QueryWrapper<ShopCouponMember>()
                .eq("member_id", RequestUtils.memberId())
                .eq("status", status));
    }

    @Override
    public List<String> getReceiveType() {
        JSONArray jsonArray = CouponMemberReceiveTypeEnum.getReceiveType();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.getJSONObject(i).getStr("name"));
        }
        return list;

    }

    @Override
    public JSONArray getCouponType() {

        Map<Integer, String> couponTypeMap = CouponTypeEnum.getMap();
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, String> entry : couponTypeMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label", entry.getValue());
            jsonObject.put("value", entry.getKey());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @Override
    public List<ShopCouponListVo> getCouponComponents(String couponIds, Integer num) {

        QueryWrapper<ShopCoupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", CouponStatusEnum.NORMAL.getStatus());
        queryWrapper.eq("receive_type", CouponReceiveTypeEnum.USER.getType());
        if (!couponIds.equals("")) {
            queryWrapper.in("coupon_id", Arrays.asList(couponIds.split(",")));
        }

        //根据时间查询
        Long nowTime = System.currentTimeMillis() / 1000;
        queryWrapper.and(i -> i.and(j -> j.le("start_time", nowTime).ge("end_time", nowTime))
                .or(j -> j.eq("start_time", 0).eq("end_time", 0)));
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit " + num);
        List<ShopCoupon> shopCouponList = shopCouponMapper.selectList(queryWrapper);
        List<ShopCouponListVo> list = new LinkedList<>();
        for (ShopCoupon shopCoupon : shopCouponList) {
            ShopCouponListVo vo = new ShopCouponListVo();
            BeanUtils.copyProperties(shopCoupon, vo);
            list.add(vo);
        }
        return list;
    }

    @Override
    public String getQrcode(Integer couponId) {
        return null;
    }


}
