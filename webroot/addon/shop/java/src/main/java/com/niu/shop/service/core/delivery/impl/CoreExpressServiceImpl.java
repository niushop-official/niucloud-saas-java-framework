package com.niu.shop.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.member.MemberAddress;
import com.niu.shop.entity.delivery.ShopDeliveryShippingTemplateItem;
import com.niu.shop.enums.delivery.ShippingTemplateEnum;
import com.niu.shop.enums.order.OrderDeliveryTypeEnum;
import com.niu.shop.mapper.delivery.ShopDeliveryShippingTemplateItemMapper;
import com.niu.shop.service.core.delivery.ICoreExpressService;
import com.niu.shop.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class CoreExpressServiceImpl implements ICoreExpressService {

    @Resource
    ShopDeliveryShippingTemplateItemMapper shopDeliveryShippingTemplateItemMapper;

    /**
     * 计算运费
     *
     * @param vo
     */
    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
            vo.getError().add("需要先选择收货地址");
            return;
        }

        BigDecimal deliveryMoney = new BigDecimal(0);
        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
            OrderCreateDataVo.Goods goods = item.getGoods();
            if (goods.getGoodsType().equals("real")) {
                JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
                if (deliveryType.contains(OrderDeliveryTypeEnum.EXPRESS.getType())) {
                    if (goods.getIsFreeShipping().equals(0)) {
                        // 是按固定运费 还是按运费模板
                        if (goods.getFeeType().equals("template")) {
                            feeCalculate(vo, item);
                        } else {
                            item.setDeliveryMoney(goods.getDeliveryMoney().multiply(new BigDecimal(item.getNum())));
                        }
                        deliveryMoney = deliveryMoney.add(item.getDeliveryMoney());
                    }
                } else {
                    item.setNotSupportDelivery(1);
                    vo.getError().add("不支持选择的配送方式");
                }
            }
        }
        vo.getBasic().setDeliveryMoney(deliveryMoney);
    }

    public void calculate() {

    }

    private void feeCalculate(OrderCreateDataVo vo, OrderCreateDataVo.Sku sku) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();

        ShopDeliveryShippingTemplateItem template = shopDeliveryShippingTemplateItemMapper.selectOne(new QueryWrapper<ShopDeliveryShippingTemplateItem>().eq("template_id", sku.getGoods().getDeliveryTemplateId()).eq("site_id", vo.getSiteId()).eq("city_id", address.getCityId()));
        ShopDeliveryShippingTemplateItem nationwide = shopDeliveryShippingTemplateItemMapper.selectOne(new QueryWrapper<ShopDeliveryShippingTemplateItem>().eq("template_id", sku.getGoods().getDeliveryTemplateId()).eq("site_id", vo.getSiteId()).eq("city_id", 0));

        BigDecimal deliveryMoney = new BigDecimal(0);

        if (template == null && nationwide == null) {
            vo.getError().add("未配置可用的运费模板");
            return;
        }
        if (template == null) {
            template = nationwide;
        } else {
            BigDecimal zero = new BigDecimal(0);
            if (template.getSnum().compareTo(0) == 0) template.setSnum(nationwide.getSnum());
            if (template.getSprice().compareTo(zero) == 0) template.setSprice(nationwide.getSprice());
            if (template.getXprice().compareTo(zero) == 0) template.setXprice(nationwide.getXprice());
            if (template.getXnum().compareTo(0) == 0) template.setXnum(nationwide.getXnum());
        }

        BigDecimal total = new BigDecimal(0);
        // 按件
        if (template.getFeeType().equals(ShippingTemplateEnum.NUM.getType())) {
            total = new BigDecimal(sku.getNum());
        }
        // 按重量
        if (template.getFeeType().equals(ShippingTemplateEnum.WEIGHT.getType())) {
            total = sku.getWeight().multiply(new BigDecimal(sku.getNum()));
        }
        // 按体积
        if (template.getFeeType().equals(ShippingTemplateEnum.VOLUME.getType())) {
            total = sku.getVolume().multiply(new BigDecimal(sku.getNum()));
        }

        // 是否选择地区不支持配送
        if (template.getNoDelivery().equals(1) && !template.getNoDeliveryAreaIds().isEmpty()) {
            vo.getError().add("所选的收货地址不支持配送");
            return;
        }

        // 判断是否包邮
        if (template.getIsFreeShipping().equals(1) && !template.getFreeShippingAreaIds().isEmpty()) {
            if (total.compareTo(new BigDecimal(template.getFreeShippingNum())) != -1 || total.compareTo(template.getFreeShippingPrice()) != -1) {
                sku.setIsFreeShipping(1);
            }
        }

        // 计算续重费用
        deliveryMoney = deliveryMoney.add(template.getSprice());
        if (total.compareTo(new BigDecimal(template.getSnum())) == 1) {
            BigDecimal xprice = total.subtract(new BigDecimal(template.getSnum())).divide(new BigDecimal(template.getXnum()), 2, BigDecimal.ROUND_HALF_UP).multiply(template.getXprice());
            deliveryMoney = deliveryMoney.add(xprice);
        }
        sku.setDeliveryMoney(deliveryMoney);
    }
}
