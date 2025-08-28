package com.niu.shop.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.entity.goods.ShopGoodsEvaluate;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.config.ShopOrderEvaluate;
import com.niu.shop.enums.goods.GoodsEvaluateEnum;
import com.niu.shop.mapper.goods.ShopGoodsEvaluateMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.service.api.goods.IGoodsEvaluateService;
import com.niu.shop.service.api.goods.param.ShopGoodsEvaluateParam;
import com.niu.shop.service.api.goods.param.ShopGoodsEvaluateSearchParam;
import com.niu.shop.service.api.goods.vo.*;
import com.niu.shop.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.shop.service.core.order.ICoreOrderConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class IGoodsEvaluateServiceImpl implements IGoodsEvaluateService {

    @Resource
    ShopGoodsEvaluateMapper shopGoodsEvaluateMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    ICoreGoodsEvaluateService coreGoodsEvaluateService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;



    @Override
    public PageResult<ShopGoodsEvaluateListVo> getPages(PageParam pageParam, ShopGoodsEvaluateSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus()));
        if(ObjectUtil.isNotEmpty(searchParam.getGoodsId())) queryWrapper.eq("goods_id", searchParam.getGoodsId());
        if(ObjectUtil.isNotEmpty(searchParam.getScores())) queryWrapper.in("scores", searchParam.getScores());
        queryWrapper.orderByDesc("topping");
        queryWrapper.orderByDesc("update_time");
        queryWrapper.orderByDesc("create_time");

        IPage<ShopGoodsEvaluate> iPage = shopGoodsEvaluateMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopGoodsEvaluateListVo> list = new LinkedList<>();

        for (ShopGoodsEvaluate item : iPage.getRecords()) {
            ShopGoodsEvaluateListVo vo = new ShopGoodsEvaluateListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setImages(JSONUtil.parseArray(item.getImages()).stream().toArray(String[]::new));
            if(ObjectUtil.isNotEmpty(vo.getImages()))
            {
                List<String> listImageMid = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(CommonUtils.thumbImageMiddle(item.getSiteId(), image));
                }
                vo.setImageMid(listImageMid);
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);

    }

    @Override
    public ShopGoodsEvaluateInfoVo getInfo(Integer id) {
        ShopGoodsEvaluate model = shopGoodsEvaluateMapper.selectOne(
                new QueryWrapper<ShopGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopGoodsEvaluateInfoVo vo = new ShopGoodsEvaluateInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setImages(JSONUtil.parseArray(model.getImages()).stream().toArray(String[]::new));
        if(ObjectUtil.isNotEmpty(vo.getImages()))
        {
            List<String> listImageMid = new ArrayList<>();
            List<String> listImageBig = new ArrayList<>();
            for (String image : vo.getImages()) {
                listImageMid.add(CommonUtils.thumbImageMiddle(vo.getSiteId(), image));
                listImageBig.add(CommonUtils.thumbImageBig(vo.getSiteId(), image));
            }
            vo.setImageMid(listImageMid);
            vo.setImageBig(listImageBig);
        }

        return vo;
    }

    @Override
    public void add(ShopGoodsEvaluateParam shopGoodsEvaluateParam) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", RequestUtils.memberId()).select("nickname, headimg"));

        ShopOrderEvaluate config = coreOrderConfigService.getEvaluateConfig(RequestUtils.siteId());

        for (ShopGoodsEvaluateParam.Evaluate item : shopGoodsEvaluateParam.getEvaluateArray()) {
            ShopGoodsEvaluate evaluate = new ShopGoodsEvaluate();
            BeanUtils.copyProperties(item, evaluate);
            evaluate.setSiteId(RequestUtils.siteId());
            evaluate.setMemberId(RequestUtils.memberId());
            evaluate.setMemberHead(member.getHeadimg());
            evaluate.setMemberName(member.getNickname());
            evaluate.setIsAudit(config.getEvaluateIsToExamine());
            evaluate.setImages(JSONUtil.parseArray(item.getImages()).toString());
            evaluate.setCreateTime(System.currentTimeMillis() / 1000);
            coreGoodsEvaluateService.addEvaluate(evaluate);
        }
    }

    @Override
    public Map<String, Long> getCount(Integer goodsId) {

        Long goodsEvaluate = shopGoodsEvaluateMapper.selectCount(new QueryWrapper<ShopGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(4, 5)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Long centreEvaluate = shopGoodsEvaluateMapper.selectCount(new QueryWrapper<ShopGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(2, 3)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Long wantingCentreEvaluate = shopGoodsEvaluateMapper.selectCount(new QueryWrapper<ShopGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(0, 1)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Map<String, Long> map = new HashMap<>();
        map.put("good_evaluate", goodsEvaluate);
        map.put("centre_evaluate", centreEvaluate);
        map.put("wanting_centre_evaluate", wantingCentreEvaluate);
        return map;
    }

    @Override
    public ShopGoodsEvaluateInGoodsVo getList(Integer goodsId) {

        QueryWrapper<ShopGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus()));
        queryWrapper.eq("goods_id", goodsId);

        Long count = shopGoodsEvaluateMapper.selectCount(queryWrapper);

        queryWrapper.orderByDesc("topping");
        queryWrapper.orderByDesc("update_time");
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 3");

        List<ShopGoodsEvaluate> list = shopGoodsEvaluateMapper.selectList(queryWrapper);

        ShopGoodsEvaluateInGoodsVo listVo = new ShopGoodsEvaluateInGoodsVo();
        List<ShopGoodsEvaluateListVo> voList = new ArrayList<>();
        for (ShopGoodsEvaluate item : list) {

            ShopGoodsEvaluateListVo vo = new ShopGoodsEvaluateListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setImages(JSONUtil.parseArray(item.getImages()).stream().toArray(String[]::new));
            if(ObjectUtil.isNotEmpty(vo.getImages()))
            {
                List<String> listImageMid = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(CommonUtils.thumbImageMiddle(item.getSiteId(), image));
                }
                vo.setImageMid(listImageMid);
            }
            voList.add(vo);
        }

        listVo.setList(voList);
        listVo.setCount(count);
        return listVo;
    }

    @Override
    public List<ShopGoodsEvaluateDetailVo> getDetail(Integer orderId) {

        QueryWrapper<ShopGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("order_id", orderId);

        List<ShopGoodsEvaluate> list = shopGoodsEvaluateMapper.selectList(queryWrapper);

        List<ShopGoodsEvaluateDetailVo> voList = new ArrayList<>();
        for (ShopGoodsEvaluate item : list) {

            ShopGoodsEvaluateDetailVo vo = new ShopGoodsEvaluateDetailVo();
            BeanUtils.copyProperties(item, vo);
            vo.setImages(JSONUtil.parseArray(item.getImages()).stream().toArray(String[]::new));
            if(ObjectUtil.isNotEmpty(vo.getImages()))
            {
                List<String> listImageMid = new ArrayList<>();
                List<String> listImageBig = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(CommonUtils.thumbImageMiddle(vo.getSiteId(), image));
                    listImageBig.add(CommonUtils.thumbImageBig(vo.getSiteId(), image));
                }
                vo.setImageMid(listImageMid);
                vo.setImageBig(listImageBig);
            }
            ShopOrderGoods shopOrderGoods = shopOrderGoodsMapper.selectOne(new QueryWrapper<ShopOrderGoods>().eq("order_goods_id", item.getOrderGoodsId()));
            if(ObjectUtil.isNotEmpty(shopOrderGoods)) {
                ShopGoodsEvaluateDetailVo.OrderGoods orderGoods = new ShopGoodsEvaluateDetailVo.OrderGoods();
                BeanUtils.copyProperties(shopOrderGoods, orderGoods);
                vo.setOrderGoods(orderGoods);
            }
            voList.add(vo);
        }

        return voList;
    }
}
