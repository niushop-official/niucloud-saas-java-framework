package com.niu.shop.listener.export;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.shop.entity.delivery.ShopDeliveryCompany;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.enums.order.InvoiceTypeEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryCompanyMapper;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Setter
@Component
@EventCallback("shop")
public class ShopOrderExportDataListener extends ExportDataEventDefiner {
    private PayMapper payMapper;
    private MemberMapper memberMapper;
    private ShopStoreMapper shopStoreMapper;
    private ShopOrderMapper shopOrderMapper;
    private ShopInvoiceMapper shopInvoiceMapper;
    private ShopOrderGoodsMapper shopOrderGoodsMapper;
    private ShopOrderDeliveryMapper shopOrderDeliveryMapper;
    private ShopDeliveryCompanyMapper shopDeliveryCompanyMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        ExportDataEventResult result = new ExportDataEventResult();
        if (!"shop_order".equals(event.getType())) {
            return result;
        }
        QueryWrapper<ShopOrder> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("site_id", event.getSiteId())
                .orderByDesc("create_time");

        List<ShopOrder> shopOrders = shopOrderMapper.selectList(orderQueryWrapper);
        Set<Integer> orderIds = CollStreamUtil.toSet(shopOrders, ShopOrder::getOrderId);
        if (ObjectUtil.isEmpty(orderIds)) {
            return result;
        }

        QueryWrapper<ShopOrderGoods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("site_id", event.getSiteId())
                .in("order_id", orderIds);

        Supplier<List<ShopOrderGoods>> supplier = () -> shopOrderGoodsMapper.selectList(goodsQueryWrapper);
        PageParam page = event.getPageParam();
        if (page.getPage() != null && page.getPage() > 0 && page.getLimit() != null && page.getLimit() > 0) {
            supplier = () -> shopOrderGoodsMapper.selectPage(new Page<>(page.getPage(), page.getLimit()), goodsQueryWrapper).getRecords();
        }

        return exportData(shopOrders, supplier);
    }

    private ExportDataEventResult exportData(List<ShopOrder> shopOrders, Supplier<List<ShopOrderGoods>> supplier) {
        QueryWrapper<Pay> payQueryWrapper = new QueryWrapper<>();
        Set<String> outTradeNos = CollStreamUtil.toSet(shopOrders, ShopOrder::getOutTradeNo);
        payQueryWrapper.in("out_trade_no", outTradeNos);
        Map<String, Pay> payMap = payMapper.selectList(payQueryWrapper).stream().collect(Collectors.toMap(Pay::getOutTradeNo, o -> o));

        Set<Integer> memberIds = CollStreamUtil.toSet(shopOrders, ShopOrder::getMemberId);
        Map<Integer, Member> memberMap = memberMapper.selectBatchIds(memberIds).stream().collect(Collectors.toMap(Member::getMemberId, o -> o));

        Set<Integer> takeStoreIds = CollStreamUtil.toSet(shopOrders, ShopOrder::getTakeStoreId);
        Map<Integer, ShopStore> storeMap = shopStoreMapper.selectBatchIds(takeStoreIds).stream().collect(Collectors.toMap(ShopStore::getStoreId, o -> o));

        Set<Integer> invoiceIds = CollStreamUtil.toSet(shopOrders, ShopOrder::getInvoiceId);
        Map<Integer, ShopInvoice> invoiceMap = Collections.emptyMap();
        if (ObjectUtil.isNotEmpty(invoiceIds)) {
            QueryWrapper<ShopInvoice> invoiceQueryWrapper = new QueryWrapper<>();
            invoiceQueryWrapper.in("id", invoiceIds)
                    .eq("status", InvoiceStatusEnum.OPEN.getStatus());
            invoiceMap = shopInvoiceMapper.selectList(invoiceQueryWrapper).stream().collect(Collectors.toMap(ShopInvoice::getId, o -> o));
        }

        Map<Integer, ShopOrder> orderMap = shopOrders.stream().collect(Collectors.toMap(ShopOrder::getOrderId, o -> o));
        Set<Integer> encountered = new HashSet<>();
        List<ShopOrderGoods> records = supplier.get();
        JSONArray result = new JSONArray(CollectionUtil.size(records));

        Map<Integer, ShopOrderDelivery> deliveryMap = Collections.emptyMap();
        Set<Integer> deliveryIds = CollStreamUtil.toSet(records, ShopOrderGoods::getDeliveryId);
        if (ObjectUtil.isEmpty(deliveryIds)) {
            deliveryMap = shopOrderDeliveryMapper.selectBatchIds(deliveryIds).stream().collect(Collectors.toMap(ShopOrderDelivery::getId, o -> o));
        }

        Map<Integer, ShopDeliveryCompany> deliveryCompanyMap = Collections.emptyMap();
        Set<Integer> deliveryCompanyIds = CollStreamUtil.toSet(deliveryMap.values(), ShopOrderDelivery::getExpressCompanyId);
        if (ObjectUtil.isNotEmpty(deliveryCompanyIds)) {
            deliveryCompanyMap = shopDeliveryCompanyMapper.selectBatchIds(deliveryCompanyIds).stream()
                    .collect(Collectors.toMap(ShopDeliveryCompany::getCompanyId, e -> e));
        }

        for (ShopOrderGoods record : records) {
            boolean isFirst = !encountered.contains(record.getOrderId());
            if (isFirst) {
                encountered.add(record.getOrderId());
            }

            ShopOrder order = orderMap.get(record.getOrderId());
            Member member = memberMap.get(record.getMemberId());
            Pay pay = payMap.get(order.getOutTradeNo());
            ShopStore store = storeMap.get(order.getTakeStoreId());
            ShopInvoice invoice = invoiceMap.get(order.getInvoiceId());

            JSONObject ret = new JSONObject();
            boolean memberFirst = isFirst && member != null;
            boolean invoiceFirst = isFirst && invoice != null;
            boolean storeFirst = isFirst && store != null;
            ret.set("order_no", isFirst ? order.getOrderNo() + "\t" : "");
            ret.set("member_no", memberFirst ? member.getMemberNo() : "");
            ret.set("nickname", memberFirst ? member.getNickname() : "");
            ret.set("mobile", memberFirst ? member.getMobile() : "");
            ret.set("goods_name", record.getGoodsName());
            ret.set("sku_name", record.getSkuName());
            ret.set("num", record.getNum());
            ret.set("price", record.getPrice());
            ret.set("goods_money", record.getGoodsMoney());
            ret.set("order_from_name", isFirst ? ChannelEnum.getNameByCode(order.getOrderFrom()) : "");
            ret.set("goods_total_money", isFirst ? order.getGoodsMoney().toString() : "");
            ret.set("delivery_money", isFirst ? order.getDeliveryMoney() : "");
            ret.set("discount_money", isFirst ? order.getDiscountMoney() : "");
            ret.set("order_money", isFirst ? order.getOrderMoney() : "");
            ret.set("point", isFirst ? order.getPoint() : "");
            ret.set("taker_name", isFirst ? order.getTakerName() : "");
            ret.set("taker_mobile", isFirst ? order.getTakerMobile() + "\t" : "");
            ret.set("taker_full_address", isFirst ? order.getTakerFullAddress() : "");
            ret.set("delivery_type_name", isFirst ?
                    OrderDeliveryTypeEnum.getNameByType(order.getDeliveryType()) : "");
            ret.set("status_name", isFirst ? OrderStatusEnum.getNameByStatus(Integer.parseInt(order.getStatus())) : "");
            ret.set("create_time", isFirst ? DateUtils.timestampToString(order.getCreateTime()) : "");
            ret.set("pay_time", isFirst && order.getPayTime() != null ? DateUtils.timestampToString(order.getPayTime()) : "");
            ret.set("delivery_time", isFirst && order.getDeliveryTime() != null ? DateUtils.timestampToString(order.getDeliveryTime()) : "");
            ret.set("finish_time", isFirst && order.getFinishTime() != null ? DateUtils.timestampToString(order.getFinishTime()) : "");
            ret.set("member_remark", isFirst ? order.getMemberRemark() : "");
            ret.set("shop_remark", isFirst ? order.getShopRemark() : "");
            ret.set("close_remark", isFirst ? order.getCloseRemark() : "");
            ret.set("out_trade_no", isFirst ? order.getOutTradeNo() : "");
            ret.set("pay_type", isFirst && pay != null ? PayTypeEnum.getType().getByPath(pay.getType() + ".name", String.class) : "");
            ret.set("store_name", storeFirst ? store.getStoreName() : "");
            ret.set("invoice_type", invoiceFirst ? InvoiceTypeEnum.getNameByType(invoice.getType()) : "");
            ret.set("header_name", invoiceFirst ? invoice.getHeaderName() : "");
            ret.set("tax_number", invoiceFirst ? invoice.getTaxNumber() : "");

            ShopOrderDelivery delivery = deliveryMap.get(record.getDeliveryId());
            if (delivery != null) {
                ret.set("express_number", isFirst ? delivery.getExpressNumber() + "\t" : "");
                ShopDeliveryCompany company = deliveryCompanyMap.get(delivery.getExpressCompanyId());
                ret.set("company_name", isFirst ? Optional.ofNullable(company).map(ShopDeliveryCompany::getCompanyName).orElse("") : "");
            } else {
                ret.set("express_number", "");
            }

            result.add(ret);
        }

        ExportDataEventResult eventResult = new ExportDataEventResult();
        eventResult.setResultData(result);
        return eventResult;
    }

    @Autowired
    public void setPayMapper(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setShopStoreMapper(ShopStoreMapper shopStoreMapper) {
        this.shopStoreMapper = shopStoreMapper;
    }

    @Autowired
    public void setShopOrderMapper(ShopOrderMapper shopOrderMapper) {
        this.shopOrderMapper = shopOrderMapper;
    }

    @Autowired
    public void setShopInvoiceMapper(ShopInvoiceMapper shopInvoiceMapper) {
        this.shopInvoiceMapper = shopInvoiceMapper;
    }

    @Autowired
    public void setShopOrderGoodsMapper(ShopOrderGoodsMapper shopOrderGoodsMapper) {
        this.shopOrderGoodsMapper = shopOrderGoodsMapper;
    }

    @Autowired
    public void setShopOrderDeliveryMapper(ShopOrderDeliveryMapper shopOrderDeliveryMapper) {
        this.shopOrderDeliveryMapper = shopOrderDeliveryMapper;
    }

    @Autowired
    public void setShopDeliveryCompanyMapper(ShopDeliveryCompanyMapper shopDeliveryCompanyMapper) {
        this.shopDeliveryCompanyMapper = shopDeliveryCompanyMapper;
    }
}
