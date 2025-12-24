package com.niu.shop.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysConfigMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.shop.entity.coupon.ShopCoupon;
import com.niu.shop.entity.coupon.ShopCouponGoods;
import com.niu.shop.entity.coupon.ShopCouponMember;
import com.niu.shop.entity.coupon.ShopCouponSendRecords;
import com.niu.shop.enums.coupon.CouponMemberStatusEnum;
import com.niu.shop.enums.coupon.CouponStatusEnum;
import com.niu.shop.enums.coupon.SendCouponRangeTypeEnum;
import com.niu.shop.event.conpon.SendCouponEvent;
import com.niu.shop.mapper.coupon.ShopCouponGoodsMapper;
import com.niu.shop.mapper.coupon.ShopCouponMapper;
import com.niu.shop.mapper.coupon.ShopCouponMemberMapper;
import com.niu.shop.mapper.coupon.ShopCouponSendRecordsMapper;
import com.niu.shop.service.admin.goods.IGoodsCategoryService;
import com.niu.shop.service.admin.marketing.ICouponService;
import com.niu.shop.service.admin.marketing.IShopCouponGoodsService;
import com.niu.shop.service.admin.marketing.param.*;
import com.niu.shop.service.admin.marketing.vo.*;
import com.niu.shop.service.core.marketing.ICoreCouponMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    @Resource
    private ShopCouponSendRecordsMapper shopCouponSendRecordsMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberLabelMapper memberLabelMapper;

    @Resource
    private MemberLevelMapper memberLevelMapper;

    @Resource
    private ISysUserService sysUserService;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private ICoreCouponMemberService coreCouponMemberService;

    @Override
    public JSONObject getInit() {
        JSONArray tree = goodsCategoryService.tree();
        JSONArray categoryList = new JSONArray();
        tree.forEach(json -> {
                    JSONObject vo = new JSONObject();
                    JSONObject jsonObj = (JSONObject) json;

                    vo.set("value", jsonObj.getInt("category_id"));
                    vo.set("label", jsonObj.getStr("category_name"));
                    if (jsonObj.getJSONArray("children") != null) {
                        JSONArray children = new JSONArray();
                        jsonObj.getJSONArray("children").forEach(child -> {

                            JSONObject childJsonObj = (JSONObject) child;
                            JSONObject childVo = new JSONObject();
                            childVo.set("value", childJsonObj.getInt("category_id"));
                            childVo.set("label", childJsonObj.getStr("category_name"));
                            children.add(childVo);
                        });
                        vo.set("children", children);
                    }
                    categoryList.add(vo);
                }
        );
        JSONObject result = new JSONObject();
        result.set("goods_category_tree", categoryList);
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
        // 检查优惠券是否正在参与活动
        this.checkCouponInUse(couponId);

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
        coreCouponMemberService.refeshMemberCouponExpireTime(couponId, siteId);
    }

    /**
     * 检查正在使用的优惠券
     * @return 优惠券ID列表
     */
    public void checkCouponInUse(Integer couponId) {
        boolean found = false;
        List<Integer> couponIds = new ArrayList<>();
        Integer targetCouponId = couponId;

        //查询配置数据
        QueryWrapper<SysConfig> sysConfigQueryWrapper = new QueryWrapper<>();
        sysConfigQueryWrapper.eq("site_id", RequestUtils.siteId());
        sysConfigQueryWrapper.eq("config_key","SIGN_CONFIG");
        SysConfig sysConfigInfo =  sysConfigMapper.selectOne(sysConfigQueryWrapper);

        //如果配置了商品编码唯一，则校验
        if(ObjectUtil.isNotNull(sysConfigInfo) && ObjectUtil.isNotNull(sysConfigInfo.getValue()) && ObjectUtil.isNotEmpty(sysConfigInfo.getValue())){
            JSONObject signConfig = JSONUtil.parseObj(sysConfigInfo.getValue());

            if (signConfig != null && !signConfig.isEmpty()) {
                // 检查每日奖励中的优惠券
                if (!signConfig.getJSONObject("dayAward").isEmpty() && signConfig.getJSONObject("dayAward") != null) {
                    JSONObject dayAward = signConfig.getJSONObject("dayAward");
                    if (dayAward != null && dayAward.containsKey("shop_coupon")) {
                        JSONObject shopCoupon = dayAward.getJSONObject("shop_coupon");
                        if (shopCoupon != null && shopCoupon.containsKey("coupon_id")) {
                            Object couponIdObj = shopCoupon.get("coupon_id");
                            if (couponIdObj instanceof Integer) {
                                couponIds.add((Integer) couponIdObj);
                            } else if (couponIdObj instanceof List) {
                                couponIds.addAll((List<Integer>) couponIdObj);
                            }
                        }
                    }
                }

                // 检查连续签到奖励中的优惠券
                JSONArray continueAward = signConfig.getJSONArray("continueAward");
                if (continueAward != null) {
                    for (int i = 0; i < continueAward.size(); i++) {
                        JSONObject item = continueAward.getJSONObject(i);
                        if (item != null && item.containsKey("shop_coupon")) {
                            JSONObject shopCoupon = item.getJSONObject("shop_coupon");
                            if (shopCoupon != null && shopCoupon.containsKey("coupon_id")) {
                                Object couponIdObj = shopCoupon.get("coupon_id");
                                if (couponIdObj instanceof Integer) {
                                    couponIds.add((Integer) couponIdObj);
                                } else if (couponIdObj instanceof List) {
                                    couponIds.addAll((List<Integer>) couponIdObj);
                                }
                            }
                        }
                    }
                }

                // 去重并重新索引
                couponIds = couponIds.stream().distinct().collect(Collectors.toList());
            }
        }

        for (int i=0; i<couponIds.size(); i++) {
            if( couponIds.get(i) == targetCouponId) {
                found = true;
                break;
            }
        }

        if (found) {
            throw new AdminException("优惠券正在参与营销活动，禁止修改");
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
    public void deleteAll(ShopCouponDelParam shopCouponDelParam) {
        if(shopCouponDelParam.getIds().size()>0){
            for (Integer couponId : shopCouponDelParam.getIds()){
                ShopCoupon shopCoupon = shopCouponMapper.selectById(couponId);
                if (ObjectUtil.isEmpty(shopCoupon)) throw new AdminException("优惠券不存在");
                if (shopCoupon.getStatus() == CouponStatusEnum.NORMAL.getStatus()) {
                    ShopCouponMember shopCouponMember = shopCouponMemberMapper.selectOne(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId));
                    if (ObjectUtil.isNotEmpty(shopCouponMember)) {
                        throw new AdminException(shopCoupon.getTitle()+"优惠券已被领取，无法删除");
                    }
                }
                shopCouponMapper.deleteById(couponId);
                shopCouponGoodsMapper.delete(new QueryWrapper<ShopCouponGoods>().eq("coupon_id", couponId));
                shopCouponMemberMapper.delete(new QueryWrapper<ShopCouponMember>().eq("coupon_id", couponId).eq("status", CouponMemberStatusEnum.WAIT_USE.getStatus()));
            }
        }
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

    /**
     * 批量更新关闭状态
     * @param shopCouponDelParam
     */
    @Override
    public void couponInvalidAll(ShopCouponDelParam shopCouponDelParam) {
        if(shopCouponDelParam.getIds().size()>0){
            ShopCoupon shopCoupon = new ShopCoupon();
            shopCoupon.setStatus(CouponStatusEnum.INVALID.getStatus());
            shopCouponMapper.update(shopCoupon, new QueryWrapper<ShopCoupon>().in("id", shopCouponDelParam.getIds()).eq("site_id", RequestUtils.siteId()));
        }
    }

    @Override
    public PageResult<ShopCouponSendRecordsVo> getSendPages(PageParam pageParam, Integer couponId, ShopCouponSendPageParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        LambdaQueryWrapper<ShopCouponSendRecords> qw = new LambdaQueryWrapper<>();
        qw.eq(ShopCouponSendRecords::getCouponId, couponId).eq(ShopCouponSendRecords::getSiteId, RequestUtils.siteId());
        if (StringUtils.isNotEmpty(param.getRangeType())){
            qw.eq(ShopCouponSendRecords::getRangeType, param.getRangeType());
        }
        if (ObjectUtil.isNotEmpty(param.getCreateTime())){
            qw.between(ShopCouponSendRecords::getCreateTime, param.getCreateTime()[0], param.getCreateTime()[1]);
            QueryMapperUtils.buildByTime(qw, ShopCouponSendRecords::getCreateTime, param.getCreateTime());
        }
        qw.orderByDesc(ShopCouponSendRecords::getCreateTime);
        Page<ShopCouponSendRecords> shopCouponSendRecordsPage = shopCouponSendRecordsMapper.selectPage(new Page<>(page, limit), qw);
        List<ShopCouponSendRecordsVo> list = new ArrayList<>();
        for (ShopCouponSendRecords item : shopCouponSendRecordsPage.getRecords()) {
            ShopCouponSendRecordsVo vo = new ShopCouponSendRecordsVo();
            BeanUtils.copyProperties(item, vo);
            ShopCoupon shopCoupon = shopCouponMapper.selectOne(new QueryWrapper<ShopCoupon>().eq("id", item.getCouponId()));
            vo.setCoupon(shopCoupon);
            list.add(vo);
        }
        return PageResult.build(page, limit, shopCouponSendRecordsPage.getTotal(), list);
    }

    @Override
    public void sendCoupon(Integer couponId, ShopCouponSendParam param) {
        ShopCoupon coupon = shopCouponMapper.selectOne(new QueryWrapper<ShopCoupon>().eq("id", couponId).eq("site_id", RequestUtils.siteId()));
        if (ObjectUtil.isEmpty(coupon)) {
            throw new CommonException("优惠券不存在");
        }
        if (!CouponStatusEnum.NORMAL.getStatus().equals(coupon.getStatus())){
            throw new CommonException("优惠券已失效");
        }
        Object[] result = getSendMemberNum(param.getRangeType(), param.getRangeParam());
        Integer memberNum = Integer.parseInt(result[0].toString());
        Map<String, Object> rangeParam = (Map<String, Object>) result[1];

        ShopCouponSendRecords record = new ShopCouponSendRecords();
        record.setSiteId(RequestUtils.siteId());
        record.setCouponId(couponId);
        record.setSendNum(param.getSendNum());
        record.setRangeType(param.getRangeType());
        record.setRangeParam(JSONUtil.toJsonStr(rangeParam));
        record.setMemberNum(memberNum);
        record.setStatus("wait");
        Integer uid = RequestUtils.uid();
        SysUser sysUser = sysUserService.find(RequestUtils.uid());
        record.setAdminUid(uid);
        record.setAdminUsername(sysUser.getUsername());
        record.setCreateTime(DateUtils.currTime());

        shopCouponSendRecordsMapper.insert(record);

        // 4. 异步发送优惠券
        SendCouponEvent event = new SendCouponEvent();
        event.setRecordId(record.getId());
        event.addAppSign("shop");
        event.setName("SendCouponEvent");
        event.setSiteId(record.getSiteId());
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    public Object[] getSendMemberNum(String rangeType, JSONObject rangeParam) {
        Long memberNum = 0L;
        Integer siteId = RequestUtils.siteId();

        if (rangeType.equals(SendCouponRangeTypeEnum.ALL.getCode())) {
            // 全部会员
            memberNum = memberMapper.selectCount(new QueryWrapper<Member>()
                    .eq("site_id", siteId));
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER.getCode())) {
            // 指定会员
            JSONArray memberIds = rangeParam.getJSONArray("member_ids");
            memberNum = memberIds != null ? memberIds.size() : 0L;
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER_LEVEL.getCode())) {
            // 会员等级
            JSONArray memberLevels = rangeParam.getJSONArray("member_level");
            if (memberLevels != null && !memberLevels.isEmpty()) {
                // 查询符合条件的会员数量
                memberNum = memberMapper.selectCount(new QueryWrapper<Member>()
                        .in("member_level", memberLevels)
                        .eq("site_id", siteId));

                // 查询会员等级名称
                List<MemberLevel> levels = memberLevelMapper.selectList(new QueryWrapper<MemberLevel>()
                        .in("level_id", memberLevels)
                        .eq("site_id", siteId));

                if (levels != null && !levels.isEmpty()) {
                    String levelNames = levels.stream()
                            .map(MemberLevel::getLevelName)
                            .collect(Collectors.joining("、"));
                    rangeParam.put("level_name", levelNames);
                }
            }
        } else if (rangeType.equals(SendCouponRangeTypeEnum.MEMBER_LABEL.getCode())) {
            // 会员标签
            List<Integer> memberLabels = (List<Integer>) rangeParam.get("member_label");
            if (memberLabels != null && !memberLabels.isEmpty()) {
                // 查询符合条件的会员数量
                List<Member> members = memberMapper.selectList(new QueryWrapper<Member>().eq("site_id", siteId));
                if (members != null && !members.isEmpty()){
                    memberNum = members.stream()
                            .filter(member -> {
                                // 解析 member_label JSON 数组
                                JSONArray labels = new JSONArray();
                                if(ObjectUtil.isNotEmpty(member.getMemberLabel())){
                                     labels = JSONUtil.parseArray(member.getMemberLabel());
                                }
                                // 判断是否有交集
                                return !labels.isEmpty() &&
                                        labels.stream().anyMatch(memberLabels::contains);
                            })
                            .count();
                }

                // 查询标签名称
                List<MemberLabel> labels = memberLabelMapper.selectList(new QueryWrapper<MemberLabel>()
                        .in("label_id", memberLabels)
                        .eq("site_id", siteId));

                if (labels != null && !labels.isEmpty()) {
                    String labelNames = labels.stream()
                            .map(MemberLabel::getLabelName)
                            .collect(Collectors.joining("、"));
                    rangeParam.put("label_name", labelNames);
                }
            }
        }

        return new Object[]{memberNum, rangeParam};
    }

}
