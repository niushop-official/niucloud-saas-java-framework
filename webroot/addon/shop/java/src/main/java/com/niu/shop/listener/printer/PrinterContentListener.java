package com.niu.shop.listener.printer;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysPrinter;
import com.niu.core.entity.sys.SysPrinterTemplate;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.sys.SysPrinterMapper;
import com.niu.core.mapper.sys.SysPrinterTemplateMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.event.AbstractPrinterContentCallbackListener;
import com.niu.core.service.core.sys.event.SysPrinterPrintTicketEvent;
import com.niu.core.service.core.sys.event.SysPrinterPrintTicketResult;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.enums.order.OrderGoodsRefundEnum;
import com.niu.shop.enums.order.OrderStatusEnum;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.order.IShopOrderService;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
@EventCallback("shop")
public class PrinterContentListener extends AbstractPrinterContentCallbackListener {
    private static final String KEY = "shopGoodsOrder";

    private PayMapper payMapper;
    private MemberMapper memberMapper;
    private SysPrinterMapper printerMapper;
    private ShopOrderMapper shopOrderMapper;
    private ICoreSiteService coreSiteService;
    private IShopOrderService shopOrderService;
    private ShopOrderGoodsMapper shopOrderGoodsMapper;
    private SysPrinterTemplateMapper sysPrinterTemplateMapper;

    @Override
    public SysPrinterPrintTicketResult handleCallback(SysPrinterPrintTicketEvent event) {
        SysPrinterPrintTicketResult result = new SysPrinterPrintTicketResult();
        if (ObjectUtil.isEmpty(event)) {
            return new SysPrinterPrintTicketResult();
        }

        SysPrinterPrintTicketParam param = event.getParam();
        if (!KEY.equals(param.getType())) {
            return new SysPrinterPrintTicketResult();
        }

        LambdaQueryWrapper<SysPrinter> printerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        printerLambdaQueryWrapper.eq(SysPrinter::getSiteId, event.getParam().getSiteId())
                .eq(SysPrinter::getStatus, 1)
                .like(SysPrinter::getTemplateType, KEY)
                .or(queryWrapper -> queryWrapper.like(SysPrinter::getTrigger, "shopGoodsOrder_trigger_pay_after")
                        .like(SysPrinter::getTrigger, "shopGoodsOrder_trigger_take_delivery")
                        .like(SysPrinter::getTrigger, "shopGoodsOrder_trigger_manual"));
        List<SysPrinter> printers = printerMapper.selectList(printerLambdaQueryWrapper);

        if (ObjectUtil.isEmpty(printers)) {
            result.setCode(-1);
            result.setMessage("未找到小票打印机");
            return result;
        }

        ShopOrderInfoVo shopOrder = shopOrderService.detail(param.getBusiness().getOrderId());
        if (ObjectUtil.isEmpty(shopOrder)) {
            result.setCode(-1);
            result.setMessage("未找到订单信息");
            return result;
        }

        LambdaQueryWrapper<ShopOrderGoods> orderGoodsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderGoodsLambdaQueryWrapper.eq(ShopOrderGoods::getOrderId, shopOrder.getOrderId());
        List<ShopOrderGoods> shopOrderGoods = shopOrderGoodsMapper.selectList(orderGoodsLambdaQueryWrapper);

        Order order = new Order();
        BeanUtils.copyProperties(shopOrder, order);
        order.setMember(memberMapper.selectById(shopOrder.getMemberId()));
        order.setStatusName(OrderStatusEnum.getNameByStatus(Integer.parseInt(shopOrder.getStatus())));

        List<OrderGoods> orderGoodsList = shopOrderGoods.stream().map(goods -> {
            OrderGoods orderGoods = new OrderGoods();
            BeanUtils.copyProperties(goods, orderGoods);
            orderGoods.setStatusName(OrderGoodsRefundEnum.getNameByStatus(goods.getStatus()));
            return orderGoods;
        }).collect(Collectors.toList());
        order.setOrderGoods(orderGoodsList);

        if (ObjectUtil.isNotEmpty(order.getOutTradeNo())) {
            Pay pay = new Pay();
            BeanUtils.copyProperties(shopOrder.getPay(), pay);
            order.setPay(pay);
        }

        printers = printers.stream()
                .filter(printer -> {
                    JSONObject value = JSONObject.parseObject(printer.getValue());
                    JSONObject type = value.getJSONObject(param.getType());
                    JSONObject trigger;
                    return !(ObjectUtil.hasEmpty(type, (trigger = type.getJSONObject("trigger_" + param.getTrigger())))) ||
                            !order.getDeliveryType().contains(trigger.getString("print_delivery_type"));
                })
                .collect(Collectors.toList());
        List<JSONObject> triggers = printers.stream()
                .map(printer -> JSONObject.parseObject(printer.getValue()).getJSONObject(param.getType()).getJSONObject("trigger_" + param.getTrigger()))
                .filter(trigger -> trigger.getJSONArray("print_delivery_type").contains(order.getDeliveryType()))
                .collect(Collectors.toList());

        Map<Integer, SysPrinterTemplate> templateMap = Collections.emptyMap();
        Set<Integer> templateIds = CollStreamUtil.toSet(triggers, trigger -> trigger.getInteger("template_id"));
        if (ObjectUtil.isNotEmpty(templateIds)) {
            templateMap = sysPrinterTemplateMapper.selectBatchIds(templateIds)
                    .stream()
                    .collect(Collectors.toMap(SysPrinterTemplate::getTemplateId, e -> e));
        }

        List<SysPrinterPrintTicketResult.Item> resData = new ArrayList<>(triggers.size());
        for (int i = 0; i < triggers.size(); i++) {
            JSONObject trigger = triggers.get(i);
            SysPrinterTemplate template = templateMap.get(trigger.getInteger("template_id"));
            if (ObjectUtil.hasEmpty(template, template.getValue())) {
                continue;
            }

            String value = template.getValue();
            StringBuilder content = new StringBuilder("<MN>")
                    .append(trigger.get("print_num"))
                    .append("</MN>");

            JSONObject valueJsonObject = JSONObject.parseObject(value);
            content.append(getTicketHeadContent(param.getSiteId(), valueJsonObject.getJSONObject("head_info")))
                    .append(getOrderInfoContent(valueJsonObject.getJSONObject("order_info"), order))
                    .append(getGoodsInfoContent(valueJsonObject.getJSONObject("goods_info"), order))
                    .append(getMemberInfoContent(valueJsonObject.getJSONObject("buyer_info"), order))
                    .append(getBottomInfoContent(valueJsonObject.getJSONObject("bottom_info")));

            SysPrinterPrintTicketResult.Item item = new SysPrinterPrintTicketResult.Item();
            item.setPrinterInfo(printers.get(i));
            item.setOriginId(order.getOrderNo());
            item.setContent(content.toString());

            resData.add(item);
        }

        result.setCode(0);
        result.setMessage("");
        result.setData(resData);
        return result;
    }

    private StringBuilder getTicketHeadContent(Integer siteId, JSONObject data) {
        StringBuilder content = new StringBuilder();

        // 小票名称
        JSONObject ticketName = data.getJSONObject("ticket_name");
        if (ticketName != null && ticketName.getInteger("status") == 1 && !ticketName.getString("value").isEmpty()) {
            // 文字大小
            if ("big".equals(ticketName.getString("fontSize"))) {
                content.append("<FH2>");
            }

            // 文字加粗
            if ("bold".equals(ticketName.getString("fontWeight"))) {
                content.append("<FB>");
            }

            content.append("<center>")
                    .append(ticketName.getString("value"))
                    .append("</center>");

            if ("bold".equals(ticketName.getString("fontWeight"))) {
                content.append("</FB>");
            }

            // 文字大小
            if ("big".equals(ticketName.getString("fontSize"))) {
                content.append("</FH2>");
            }

            content.append(repeat('.', 32));
        }

        // 获取商城信息
        SiteInfoVo siteInfo = coreSiteService.getSiteCache(siteId);

        // 商城名称
        JSONObject shopName = data.getJSONObject("shop_name");
        if (shopName != null && shopName.getInteger("status") == 1 && siteInfo != null && ObjectUtil.isNotEmpty(siteInfo.getSiteName())) {
            // 文字大小
            if ("big".equals(shopName.getString("fontSize"))) {
                content.append("<FH2>");
            }

            // 文字加粗
            if ("bold".equals(shopName.getString("fontWeight"))) {
                content.append("<FB>");
            }

            content.append("<center>")
                    .append(siteInfo.getSiteName())
                    .append("</center>");

            if ("bold".equals(shopName.getString("fontWeight"))) {
                content.append("</FB>");
            }

            // 文字大小
            if ("big".equals(shopName.getString("fontSize"))) {
                content.append("</FH2>");
            }

            content.append(repeat('.', 32));
        }

        return content;
    }

    private StringBuilder getOrderInfoContent(JSONObject data, Order orderInfo) {
        StringBuilder content = new StringBuilder();

        // 订单基础信息
        if (data.getJSONObject("order_item").getInteger("status") == 1 &&
                !data.getJSONObject("order_item").getJSONArray("value").isEmpty()) {
            // 订单编号
            if (data.getJSONObject("order_item").getJSONArray("value").contains("order_no")) {
                content.append("订单编号：").append(orderInfo.getOrderNo()).append("\n");
            }

            // 订单来源
            if (data.getJSONObject("order_item").getJSONArray("value").contains("order_from")) {
                content.append("订单来源：").append(orderInfo.getOrderFromName()).append("\n");
            }

            // 订单状态
            if (data.getJSONObject("order_item").getJSONArray("value").contains("order_status")) {
                content.append("订单状态：").append(orderInfo.getStatusName()).append("\n");
            }

            // 支付方式
            if (data.getJSONObject("order_item").getJSONArray("value").contains("pay_type")) {
                content.append("支付方式：").append(orderInfo.getPay().getTypeName()).append("\n");
            }

            // 配送方式
            if (data.getJSONObject("order_item").getJSONArray("value").contains("delivery_type")) {
                content.append("配送方式：").append(orderInfo.getDeliveryTypeName()).append("\n");
            }

            // 下单时间
            if (data.getJSONObject("order_item").getJSONArray("value").contains("create_time")) {
                content.append("下单时间：").append(DateUtils.timestampToString(orderInfo.getCreateTime())).append("\n");
            }

            // 付款时间
            if (data.getJSONObject("order_item").getJSONArray("value").contains("pay_time")) {
                content.append("付款时间：").append(DateUtils.timestampToString(orderInfo.getPayTime())).append("\n");
            }

            // 商品总额
            if (data.getJSONObject("order_item").getJSONArray("value").contains("goods_money") &&
                    orderInfo.getGoodsMoney().compareTo(BigDecimal.ZERO) > 0) {
                content.append("商品总额：￥").append(orderInfo.getGoodsMoney().toString()).append("\n");
            }

            // 优惠金额
            if (data.getJSONObject("order_item").getJSONArray("value").contains("discount_money") &&
                    orderInfo.getDiscountMoney().compareTo(BigDecimal.ZERO) > 0) {
                content.append("优惠金额：￥").append(orderInfo.getDiscountMoney().toString()).append("\n");
            }

            // 配送金额
            if (data.getJSONObject("order_item").getJSONArray("value").contains("delivery_money") &&
                    orderInfo.getDeliveryMoney().compareTo(BigDecimal.ZERO) > 0) {
                content.append("配送金额：￥").append(orderInfo.getDeliveryMoney().toString()).append("\n");
            }
        }

        // 实付金额
        if (data.getJSONObject("order_money").getInteger("status") == 1) {
            if ("big".equals(data.getJSONObject("order_money").getString("fontSize"))) {
                content.append("<FH2>");
            }

            if ("bold".equals(data.getJSONObject("order_money").getString("fontWeight"))) {
                content.append("<FB>");
            }

            content.append("实付金额：￥").append(orderInfo.getOrderMoney().toString());

            if ("bold".equals(data.getJSONObject("order_money").getString("fontWeight"))) {
                content.append("</FB>");
            }

            if ("big".equals(data.getJSONObject("order_money").getString("fontSize"))) {
                content.append("</FH2>");
            }

            content.append("\n");
        }

        // 商家备注
        if (data.getJSONObject("shop_remark").getInteger("status") == 1 &&
                ObjectUtil.isNotEmpty(orderInfo.getShopRemark())) {
            if ("big".equals(data.getJSONObject("shop_remark").getString("fontSize"))) {
                content.append("<FH2>");
            }

            if ("bold".equals(data.getJSONObject("shop_remark").getString("fontWeight"))) {
                content.append("<FB>");
            }

            content.append("商家备注：").append(orderInfo.getShopRemark()).append("\n");

            if ("bold".equals(data.getJSONObject("shop_remark").getString("fontWeight"))) {
                content.append("</FB>");
            }

            if ("big".equals(data.getJSONObject("shop_remark").getString("fontSize"))) {
                content.append("</FH2>");
            }
        }

        if (data.getJSONObject("order_item").getInteger("status") == 1 ||
                data.getJSONObject("order_money").getInteger("status") == 1 ||
                data.getJSONObject("shop_remark").getInteger("status") == 1) {
            content.append(repeat('.', 32));
        }

        return content;
    }

    private StringBuilder getGoodsInfoContent(JSONObject data, Order orderInfo) {
        StringBuilder content = new StringBuilder()
                .append("<table>")
                .append("<tr>")
                .append("<td>商品名称</td>")
                .append("<td></td>")
                .append("<td>数量</td>");

        if (data.getJSONObject("goods_price").getInteger("status") == 1) {
            content.append("<td>金额</td>");
        }

        content.append("</tr>")
                .append("</table>\n")
                .append("<table>");
        for (OrderGoods goods : orderInfo.getOrderGoods()) {
            content.append("<tr>")
                    .append("<td>");

            // 文字大小
            if ("big".equals(data.getJSONObject("goods_name").getString("fontSize"))) {
                content.append("<FH2>");
            }

            // 文字加粗
            if ("bold".equals(data.getJSONObject("goods_name").getString("fontWeight"))) {
                content.append("<FB>");
            }

            content.append(goods.getGoodsName())
                    .append(" ")
                    .append(goods.getSkuName());

            // 文字加粗
            if ("bold".equals(data.getJSONObject("goods_name").getString("fontWeight"))) {
                content.append("</FB>");
            }

            // 文字大小
            if ("big".equals(data.getJSONObject("goods_name").getString("fontSize"))) {
                content.append("</FH2>");
            }

            content.append("</td>")
                    .append("<td></td>")
                    .append("<td>x")
                    .append(goods.getNum())
                    .append("</td>");

            if (data.getJSONObject("goods_price").getInteger("status") == 1) {
                content.append("<td>");

                // 文字大小
                if ("big".equals(data.getJSONObject("goods_price").getString("fontSize"))) {
                    content.append("<FH2>");
                }

                // 文字加粗
                if ("bold".equals(data.getJSONObject("goods_price").getString("fontWeight"))) {
                    content.append("<FB>");
                }

                content.append("￥")
                        .append(goods.getPrice());

                // 文字加粗
                if ("bold".equals(data.getJSONObject("goods_price").getString("fontWeight"))) {
                    content.append("</FB>");
                }

                // 文字大小
                if ("big".equals(data.getJSONObject("goods_price").getString("fontSize"))) {
                    content.append("</FH2>");
                }

                content.append("</td>");
            }
            content.append("</tr>");
        }

        return content.append("</table>\n")
                .append(repeat('.', 32));
    }

    private StringBuilder getMemberInfoContent(JSONObject data, Order orderInfo) {
        StringBuilder content = new StringBuilder();

        // 会员基础信息
        if (data.getJSONObject("member_basic_info").getInteger("status") == 1) {
            // 买家昵称
            if (data.getJSONObject("member_basic_info").getJSONArray("value").contains("nickname")) {
                content.append("会员昵称：").append(orderInfo.getTakerName()).append("\n");
            }

            // 账户余额
            if (data.getJSONObject("member_basic_info").getJSONArray("value").contains("account_balance")) {
                content.append("账户余额：￥").append(orderInfo.getMember().getBalance()).append("\n");
            }

            // 账户积分
            if (data.getJSONObject("member_basic_info").getJSONArray("value").contains("account_point")) {
                content.append("账户积分：").append(orderInfo.getMember().getPoint()).append("\n");
            }
        }

        // 买家手机号
        if (data.getJSONObject("buyer_mobile").getInteger("status") == 1 && ObjectUtil.isNotEmpty(orderInfo.getMember().getMobile())) {
            // 文字大小
            if ("big".equals(data.getJSONObject("buyer_mobile").getString("fontSize"))) {
                content.append("<FH2>");
            }

            // 文字加粗
            if ("bold".equals(data.getJSONObject("buyer_mobile").getString("fontWeight"))) {
                content.append("<FB>");
            }

            if ("yes".equals(data.getJSONObject("buyer_mobile").getString("value"))) {
                content.append("会员手机号：")
                        .append(orderInfo.getMember().getMobile(), 0, 3)
                        .append("****")
                        .append(orderInfo.getMember().getMobile().substring(7))
                        .append("\n");
            } else {
                content.append("会员手机号：").append(orderInfo.getMember().getMobile()).append("\n");
            }

            if ("bold".equals(data.getJSONObject("buyer_mobile").getString("fontWeight"))) {
                content.append("</FB>");
            }

            if ("big".equals(data.getJSONObject("buyer_mobile").getString("fontSize"))) {
                content.append("</FH2>");
            }

            content.append("\n").append(repeat('.', 32));
        }

        // 收货人姓名
        if (data.getJSONObject("taker_name").getInteger("status") == 1 && !ObjectUtil.isNotEmpty(orderInfo.getTakerName())) {
            if ("big".equals(data.getJSONObject("taker_name").getString("fontSize"))) {
                content.append("<FH2>");
            }
            if ("bold".equals(data.getJSONObject("taker_name").getString("fontWeight"))) {
                content.append("<FB>");
            }
            content.append("收货人：").append(orderInfo.getTakerName());
            if ("bold".equals(data.getJSONObject("taker_name").getString("fontWeight"))) {
                content.append("</FB>");
            }
            if ("big".equals(data.getJSONObject("taker_name").getString("fontSize"))) {
                content.append("</FH2>");
            }
            content.append("\n");
        }

        // 收货人手机号
        if (data.getJSONObject("taker_mobile").getInteger("status") == 1) {
            if ("big".equals(data.getJSONObject("taker_mobile").getString("fontSize"))) {
                content.append("<FH2>");
            }
            if ("bold".equals(data.getJSONObject("taker_mobile").getString("fontWeight"))) {
                content.append("<FB>");
            }
            if ("yes".equals(data.getJSONObject("taker_mobile").getString("value"))) {
                content.append("联系方式：")
                        .append(orderInfo.getTakerMobile(), 0, 3)
                        .append("****")
                        .append(orderInfo.getTakerMobile().substring(7))
                        .append("\n");
            } else {
                content.append("联系方式：")
                        .append(orderInfo.getTakerMobile())
                        .append("\n");
            }
            if ("bold".equals(data.getJSONObject("taker_mobile").getString("fontWeight"))) {
                content.append("</FB>");
            }
            if ("big".equals(data.getJSONObject("taker_mobile").getString("fontSize"))) {
                content.append("</FH2>");
            }
            content.append("\n");
        }

        // 收货人地址
        if (data.getJSONObject("taker_full_address").getInteger("status") == 1) {
            if ("big".equals(data.getJSONObject("taker_full_address").getString("fontSize"))) {
                content.append("<FH2>");
            }
            if ("bold".equals(data.getJSONObject("taker_full_address").getString("fontWeight"))) {
                content.append("<FB>");
            }
            content.append("收货地址：")
                    .append(orderInfo.getTakerFullAddress());
            if ("bold".equals(data.getJSONObject("taker_full_address").getString("fontWeight"))) {
                content.append("</FB>");
            }
            if ("big".equals(data.getJSONObject("taker_full_address").getString("fontSize"))) {
                content.append("</FH2>");
            }
            content.append("\n");
        }

        // 买家备注
        if (data.getJSONObject("member_remark").getInteger("status") == 1 && ObjectUtil.isNotEmpty(orderInfo.getMemberRemark())) {
            if ("big".equals(data.getJSONObject("member_remark").getString("fontSize"))) {
                content.append("<FH2>");
            }
            if ("bold".equals(data.getJSONObject("member_remark").getString("fontWeight"))) {
                content.append("<FB>");
            }
            content.append("买家备注：")
                    .append(orderInfo.getMemberRemark());
            if ("bold".equals(data.getJSONObject("member_remark").getString("fontWeight"))) {
                content.append("</FB>");
            }
            if ("big".equals(data.getJSONObject("member_remark").getString("fontSize"))) {
                content.append("</FH2>");
            }
            content.append("\n");
        }

        if (data.getJSONObject("member_basic_info").getInteger("status") == 1 || data.getJSONObject("buyer_mobile").getInteger("status") == 1 ||
                data.getJSONObject("taker_name").getInteger("status") == 1 || data.getJSONObject("taker_mobile").getInteger("status") == 1 ||
                data.getJSONObject("taker_full_address").getInteger("status") == 1 || data.getJSONObject("member_remark").getInteger("status") == 1) {
            content.append("\n");
        }

        return content;
    }

    private StringBuilder getBottomInfoContent(JSONObject data) {
        StringBuilder content = new StringBuilder();

        // 二维码
        if (data.getJSONObject("qrcode").getInteger("status") == 1) {
            content.append("<QR>")
                    .append(data.getJSONObject("qrcode").getString("value"))
                    .append("</QR>\n\n");
        }

        // 小票打印时间
        if (data.getJSONObject("ticket_print_time").getInteger("status") == 1) {
            content.append("<center>打印时间：")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                    .append("</center>\n");
        }

        // 底部信息
        if (data.getJSONObject("bottom_remark").getInteger("status") == 1) {
            content.append("<center>")
                    .append(data.getJSONObject("bottom_remark").getString("value"))
                    .append("</center>\n");
        }

        return content;
    }

    // 字符串重复方法
    private String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    @Autowired
    public void setPrinterMapper(SysPrinterMapper printerMapper) {
        this.printerMapper = printerMapper;
    }

    @Autowired
    public void setShopOrderMapper(ShopOrderMapper shopOrderMapper) {
        this.shopOrderMapper = shopOrderMapper;
    }

    @Autowired
    public void setMemberMapper(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Autowired
    public void setShopOrderGoodsMapper(ShopOrderGoodsMapper shopOrderGoodsMapper) {
        this.shopOrderGoodsMapper = shopOrderGoodsMapper;
    }

    @Autowired
    public void setPayMapper(PayMapper payMapper) {
        this.payMapper = payMapper;
    }

    @Autowired
    public void setSysPrinterTemplateMapper(SysPrinterTemplateMapper sysPrinterTemplateMapper) {
        this.sysPrinterTemplateMapper = sysPrinterTemplateMapper;
    }

    @Autowired
    public void setCoreSiteService(ICoreSiteService coreSiteService) {
        this.coreSiteService = coreSiteService;
    }

    @Autowired
    public void setShopOrderService(IShopOrderService shopOrderService) {
        this.shopOrderService = shopOrderService;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class Order extends ShopOrder {
        private String orderFromName;
        private String statusName;
        private String deliveryTypeName;

        private Pay pay;
        private Member member;
        private List<OrderGoods> orderGoods;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class OrderGoods extends ShopOrderGoods {
        private String statusName;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class Pay extends com.niu.core.entity.pay.Pay {
        private String typeName;
    }

}
