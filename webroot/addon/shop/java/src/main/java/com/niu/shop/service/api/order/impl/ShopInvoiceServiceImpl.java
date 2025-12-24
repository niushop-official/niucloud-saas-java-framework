package com.niu.shop.service.api.order.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.active.MarketingActivityTypeEnum;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.enums.order.InvoicedEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.api.order.IShopInvoiceService;
import com.niu.shop.service.api.order.param.ShopInvoiceAddParam;
import com.niu.shop.service.api.order.param.ShopInvoiceEditParam;
import com.niu.shop.service.api.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.api.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.api.order.vo.ShopInvoiceListVo;
import com.niu.shop.service.core.order.ICoreInvoiceService;
import com.niu.shop.service.core.order.ICoreOrderService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShopInvoiceServiceImpl implements IShopInvoiceService {


    @Resource
    private ShopInvoiceMapper shopInvoiceMapper;

    @Resource
    private ShopOrderMapper shopOrderMapper;

    @Resource
    private ICoreInvoiceService coreInvoiceService;

    @Resource
    private ICoreOrderService coreOrderService;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private ShopOrderGoodsMapper orderGoodsMapper;

    @Resource
    private ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    @Resource
    private ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    @Override
    public PageResult<ShopInvoiceListVo> getPage(PageParam pageParam, ShopInvoiceSearchParam searchParam) {
        Integer memberId = RequestUtils.memberId();
        Integer siteId = RequestUtils.siteId();
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        LambdaQueryWrapper<ShopInvoice> queryWrapper = new LambdaQueryWrapper<ShopInvoice>()
                .eq(ShopInvoice::getMemberId, memberId).eq(ShopInvoice::getSiteId, siteId)
                .orderByDesc(ShopInvoice::getCreateTime);
        if (ObjectUtils.isNotEmpty(searchParam.getStatus())) {
            if (Objects.equals(searchParam.getStatus(), InvoicedEnum.WAIT_INVOICE.getStatus())) {
                queryWrapper.in(ShopInvoice::getIsInvoice, InvoicedEnum.WAIT_INVOICE.getStatus(), InvoicedEnum.WAIT_AUDIT.getStatus());
            } else {
                queryWrapper.eq(ShopInvoice::getIsInvoice, searchParam.getStatus());
            }
        }
        Page<ShopInvoice> shopInvoicePage = shopInvoiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        if (CollectionUtils.isEmpty(shopInvoicePage.getRecords())) {
            return PageResult.build(page, limit);
        }
        List<ShopInvoice> list = shopInvoicePage.getRecords();
        Set<Integer> memberIds = list.stream().map(ShopInvoice::getMemberId).collect(Collectors.toSet());
        List<Member> members = memberMapper.selectByIds(memberIds);
        Map<Integer, Member> memberMap = members.stream().collect(Collectors.toMap(Member::getMemberId, Function.identity()));
        List<ShopInvoiceListVo> result = list.stream().map(invoice -> {
            ShopInvoiceListVo shopInvoiceListVo = new ShopInvoiceListVo();
            BeanUtils.copyProperties(invoice, shopInvoiceListVo);
            shopInvoiceListVo.setMember(memberMap.get(invoice.getMemberId()));
            return shopInvoiceListVo;
        }).toList();
        return PageResult.build(page, limit, shopInvoicePage.getTotal(), result);
    }

    @Override
    public ShopInvoiceInfoVo getInfo(Integer id) {
        Integer memberId = RequestUtils.memberId();
        Integer siteId = RequestUtils.siteId();

        LambdaQueryWrapper<ShopInvoice> queryWrapper = new LambdaQueryWrapper<ShopInvoice>()
                .eq(ShopInvoice::getMemberId, memberId)
                .eq(ShopInvoice::getSiteId, siteId)
                .eq(ShopInvoice::getId, id);

        ShopInvoice shopInvoice = shopInvoiceMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(shopInvoice)) {
            throw new ApiException("发票信息不存在");
        }

        ShopInvoiceInfoVo info = new ShopInvoiceInfoVo();
        BeanUtils.copyProperties(shopInvoice, info);

        info.setMember(memberMapper.selectById(info.getMemberId()));

        List<ShopOrder> invoiceOrders = shopOrderMapper.selectList(new LambdaQueryWrapper<ShopOrder>().eq(ShopOrder::getInvoiceId, id));

        if (CollectionUtils.isEmpty(invoiceOrders)) {
            return info;
        }

        Set<Integer> orderIds = invoiceOrders.stream().map(ShopOrder::getOrderId).collect(Collectors.toSet());

        //批量查询订单商品
        Map<Integer, List<ShopOrderGoods>> goodsByOrderId = orderGoodsMapper.selectList(new LambdaQueryWrapper<ShopOrderGoods>()
                 .in(ShopOrderGoods::getOrderId, orderIds)).stream().collect(Collectors.groupingBy(ShopOrderGoods::getOrderId));

        Set<Integer> deliveryIds = goodsByOrderId.values().stream()
                .flatMap(List::stream)
                .map(ShopOrderGoods::getDeliveryId)
                .filter(Objects::nonNull)
                .filter(deliveryId -> deliveryId > 0)
                .collect(Collectors.toSet());

        //批量查询物流信息
        Map<Integer, ShopOrderDelivery> deliveryMap = CollectionUtils.isNotEmpty(deliveryIds) ?
                shopOrderDeliveryMapper.selectByIds(deliveryIds)
                .stream().collect(Collectors.toMap(ShopOrderDelivery::getId, Function.identity())) : Map.of();

        Set<Integer> companyIds = deliveryMap.values().stream()
                .map(ShopOrderDelivery::getExpressCompanyId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        //批量查询快递公司信息
        Map<Integer, ShopDeliveryCompany> companyMap = CollectionUtils.isNotEmpty(companyIds) ?
                shopDeliveryCompanyMapper.selectByIds(companyIds)
                .stream().collect(Collectors.toMap(ShopDeliveryCompany::getCompanyId, Function.identity())) : Map.of();

        List<ShopInvoiceInfoVo.OrderList> orderVoList = invoiceOrders.stream().map(order -> {
            ShopInvoiceInfoVo.OrderList orderVo = new ShopInvoiceInfoVo.OrderList();
            BeanUtils.copyProperties(order, orderVo);

            List<ShopOrderGoods> orderGoodsList = goodsByOrderId.get(order.getOrderId());
            if (CollectionUtils.isNotEmpty(orderGoodsList)) {
                List<ShopInvoiceInfoVo.OrderGoods> goodsVoList = orderGoodsList.stream().map(goods -> {
                    ShopInvoiceInfoVo.OrderGoods goodsVo = new ShopInvoiceInfoVo.OrderGoods();
                    BeanUtils.copyProperties(goods, goodsVo);

                    if (ObjectUtils.isNotEmpty(goods.getDeliveryId())) {
                        ShopOrderDelivery delivery = deliveryMap.get(goods.getDeliveryId());
                        if (ObjectUtils.isNotEmpty(delivery)) {
                            ShopInvoiceInfoVo.OrderDelivery deliveryVo = new ShopInvoiceInfoVo.OrderDelivery();
                            BeanUtils.copyProperties(delivery, deliveryVo);

                            if (ObjectUtils.isNotEmpty(delivery.getExpressCompanyId())) {
                                deliveryVo.setCompany(companyMap.get(delivery.getExpressCompanyId()));
                            }

                            goodsVo.setOrderDelivery(deliveryVo);
                        }
                    }

                    return goodsVo;
                }).toList();

                orderVo.setOrderGoods(goodsVoList);
            }

            return orderVo;
        }).toList();

        info.setOrderList(orderVoList);

        return info;
    }

    @Override
    public void apply(ShopInvoiceAddParam param) {
        Integer siteId = RequestUtils.siteId();
        Integer memberId = RequestUtils.memberId();
        List<String> tradeIds = param.getTradeIds();
        boolean isApply = shopInvoiceMapper.exists(new LambdaQueryWrapper<ShopInvoice>()
                .eq(ShopInvoice::getSiteId, siteId)
                .eq(ShopInvoice::getMemberId, memberId)
                .eq(ShopInvoice::getTradeType, MarketingActivityTypeEnum.SHOP.getType())
                .in(ShopInvoice::getTradeId, tradeIds));
        if (isApply){
            throw new ApiException("当前选择订单中已有订单开具发票");
        }
        ShopInvoice invoice = ShopInvoice.builder()
                .siteId(siteId)
                .memberId(memberId)
                .tradeType(MarketingActivityTypeEnum.SHOP.getType())
                .tradeId(JSONUtil.parseArray(param.getTradeIds()).toString())
                .headerType(param.getHeaderType())
                .headerName(param.getHeaderName())
                .type(param.getType())
                .name(param.getName())
                .taxNumber(param.getTaxNumber())
                .mobile(param.getMobile())
                .email(param.getEmail())
                .telephone(param.getTelephone())
                .address(param.getAddress())
                .bankName(param.getBankName())
                .bankCardNumber(param.getBankCardNumber())
                .money(param.getMoney())
                .createTime(DateUtils.currTime())
                .status(InvoiceStatusEnum.OPEN.getStatus())
                .payVoucher(param.getPayVoucher())
                .isInvoice(InvoicedEnum.WAIT_AUDIT.getStatus())
                .build();
        shopInvoiceMapper.insert(invoice);
        coreOrderService.setInvoiceIdByOrderId(tradeIds, invoice.getId());
    }

    @Override
    public void cancel(Integer id) {
        getInvoice(id);
        shopInvoiceMapper.deleteById(id);
        coreOrderService.cancelInvoiceId(id);
    }

    @Override
    public void edit(ShopInvoiceEditParam param) {
        ShopInvoice shopInvoice = getInvoice(param.getId());
        BeanUtils.copyProperties(shopInvoice, param);
        shopInvoice.setTradeType(MarketingActivityTypeEnum.SHOP.getType());
        shopInvoiceMapper.updateById(shopInvoice);
    }

    private ShopInvoice getInvoice(Integer id) {
        ShopInvoice shopInvoice = shopInvoiceMapper.selectById(id);
        if (shopInvoice == null){
            throw new ApiException("发票不存在");
        }
        if (Objects.equals(shopInvoice.getIsInvoice(), InvoicedEnum.INVOICED.getStatus())){
            throw new ApiException("已开票");
        }
        return shopInvoice;
    }
}
