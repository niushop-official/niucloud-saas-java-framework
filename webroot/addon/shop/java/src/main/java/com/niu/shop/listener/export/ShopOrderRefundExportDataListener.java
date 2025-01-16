package com.niu.shop.listener.export;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.event.sys.ExportDataEventDefiner;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderRefund;
import com.niu.shop.enums.order.OrderRefundStatusEnum;
import com.niu.shop.enums.order.OrderRefundTypeEnum;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderRefundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@EventCallback("shop")
public class ShopOrderRefundExportDataListener extends ExportDataEventDefiner {
    private MemberMapper memberMapper;
    private ShopOrderGoodsMapper shopOrderGoodsMapper;
    private ShopOrderRefundMapper shopOrderRefundMapper;

    @Override
    public ExportDataEventResult handleCallback(ExportDataEvent event) {
        if (!"shop_order_refund".equals(event.getType())) {
            return new ExportDataEventResult();
        }

        QueryWrapper<ShopOrderRefund> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", event.getSiteId())
                .orderByDesc("create_time");

        JSONObject where = event.getWhere();
        if (where != null) {
            Object orderRefundNo = where.get("order_refund_no");
            if (ObjectUtil.isNotEmpty(orderRefundNo)) {
                queryWrapper.eq("order_refund_no", orderRefundNo);
            }

            Object status = where.get("status");
            if (ObjectUtil.isNotEmpty(status)) {
                queryWrapper.eq("status", status);
            }

            Object createTime = where.get("create_time");
            if (ObjectUtil.isNotEmpty(createTime)) {
                queryWrapper.eq("create_time", createTime);
            }
        }

        List<ShopOrderRefund> shopOrderRefunds;
        PageParam page = event.getPageParam();
        if (page.getPage() > 0 && page.getLimit() > 0) {
            shopOrderRefunds = shopOrderRefundMapper.selectPage(new Page<>(page.getPage(), page.getLimit()), queryWrapper).getRecords();
        } else {
            shopOrderRefunds = shopOrderRefundMapper.selectList(queryWrapper);
        }

        return exportData(shopOrderRefunds);
    }

    private ExportDataEventResult exportData(List<ShopOrderRefund> shopOrderRefunds) {
        if (ObjectUtil.isEmpty(shopOrderRefunds)) {
            return new ExportDataEventResult();
        }

        Map<Integer, Member> memberMap = Collections.emptyMap();
        Set<Integer> memberIds = CollStreamUtil.toSet(shopOrderRefunds, ShopOrderRefund::getMemberId);
        if (ObjectUtil.isNotEmpty(memberIds)) {
            memberMap = memberMapper.selectBatchIds(memberIds).stream()
                    .collect(Collectors.toMap(Member::getMemberId, o -> o));
        }

        Map<Integer, ShopOrderGoods> goodsMap = Collections.emptyMap();
        Set<Integer> goodsIds = CollStreamUtil.toSet(shopOrderRefunds, ShopOrderRefund::getOrderGoodsId);
        if (ObjectUtil.isNotEmpty(goodsIds)) {
            goodsMap = shopOrderGoodsMapper.selectBatchIds(goodsIds).stream()
                    .collect(Collectors.toMap(ShopOrderGoods::getOrderGoodsId, o -> o));
        }

        JSONArray jsonArray = new JSONArray(CollectionUtil.size(shopOrderRefunds));
        for (ShopOrderRefund refund : shopOrderRefunds) {
            ShopOrderGoods goods = goodsMap.get(refund.getOrderGoodsId());
            Member member = memberMap.get(refund.getMemberId());
            JSONObject jsonObject = new JSONObject(16, false);
            Optional<JSONObject> refundAddress = Optional.ofNullable(ObjectUtil.isEmpty(refund.getRefundAddress()) ? null : refund.getRefundAddress())
                    .map(JSONUtil::parseObj);
            Optional<JSONObject> delivery = Optional.ofNullable(ObjectUtil.isEmpty(refund.getDelivery()) ? null : refund.getDelivery())
                    .map(JSONUtil::parseObj);

            jsonObject.set("order_refund_no", refund.getOrderRefundNo())
                    .set("nickname", Optional.ofNullable(member).map(Member::getNickname).orElse(""))
                    .set("refund_type_name", OrderRefundTypeEnum.getNameByType(Integer.parseInt(refund.getRefundType())))
                    .set("apply_money", refund.getApplyMoney())
                    .set("money", refund.getMoney())
                    .set("status_name", OrderRefundStatusEnum.getNameByStatus(Integer.parseInt(refund.getStatus())))
                    .set("create_time", ObjectUtil.isNotEmpty(refund.getCreateTime()) ? DateUtils.timestampToString(refund.getCreateTime()) : null)
                    .set("transfer_time", ObjectUtil.isNotEmpty(refund.getTransferTime()) ? DateUtils.timestampToString(refund.getTransferTime()) : null)
                    .set("reason", refund.getReason())
                    .set("shop_reason", refund.getShopReason())
                    .set("refund_address", refundAddress.map(o -> o.getStr("full_address")).orElse(""))
                    .set("express_number", delivery.map(o -> o.getStr("express_number")).orElse(""))
                    .set("contact_name", refundAddress.map(o -> o.getStr("contact_name")).orElse(""))
                    .set("express_company", delivery.map(o -> o.getStr("express_company")).orElse(""))
                    .set("refund_no", refund.getRefundNo() + "\t")
                    .set("goods_info", Optional.ofNullable(goods).map(o -> o.getGoodsName() + " " + o.getSkuName()).orElse(""))
                    .set("refund_num", Optional.ofNullable(goods).map(ShopOrderGoods::getNum).orElse(0));

            jsonArray.add(jsonObject);
        }

        ExportDataEventResult result = new ExportDataEventResult();
        result.setResultData(jsonArray);
        return result;
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
    public void setShopOrderRefundMapper(ShopOrderRefundMapper shopOrderRefundMapper) {
        this.shopOrderRefundMapper = shopOrderRefundMapper;
    }

}
