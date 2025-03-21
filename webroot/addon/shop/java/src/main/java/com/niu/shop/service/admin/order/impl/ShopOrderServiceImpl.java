package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.shop.entity.delivery.ShopStore;
import com.niu.shop.entity.goods.ShopGoods;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.entity.order.ShopOrderDelivery;
import com.niu.shop.entity.order.ShopOrderGoods;
import com.niu.shop.entity.order.ShopOrderLog;
import com.niu.shop.enums.order.*;
import com.niu.shop.mapper.delivery.ShopStoreMapper;
import com.niu.shop.mapper.goods.ShopGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderDeliveryMapper;
import com.niu.shop.mapper.order.ShopOrderGoodsMapper;
import com.niu.shop.mapper.order.ShopOrderLogMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.order.IShopOrderService;
import com.niu.shop.service.admin.order.param.EditDeliveryParam;
import com.niu.shop.service.admin.order.param.EditPriceParam;
import com.niu.shop.service.admin.order.param.SetShopRemarkParam;
import com.niu.shop.service.admin.order.param.ShopOrderSearchParam;
import com.niu.shop.service.admin.order.vo.EditDeliveryVo;
import com.niu.shop.service.admin.order.vo.ShopOrderInfoVo;
import com.niu.shop.service.admin.order.vo.ShopOrderListVo;
import com.niu.shop.service.admin.order.vo.ShopOrderLogListVo;
import com.niu.shop.service.core.marketing.ICoreManjianService;
import com.niu.shop.service.core.order.ICoreOrderLogService;
import com.niu.shop.service.core.order.ICoreOrderService;
import com.niu.shop.service.core.order.param.AddOrderLogParam;
import com.niu.shop.service.core.order.param.OrderCloseParam;
import com.niu.shop.service.core.order.param.OrderPayParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


/**
 * 订单实现
 */
@Service
public class ShopOrderServiceImpl implements IShopOrderService {

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ShopStoreMapper shopStoreMapper;

    @Resource
    ShopOrderDeliveryMapper shopOrderDeliveryMapper;

    @Resource
    ShopOrderLogMapper shopOrderLogMapper;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICorePayService corePayService;

    @Resource
    ShopGoodsMapper shopGoodsMapper;

    @Resource
    ICoreManjianService coreManjianService;

    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public PageResult<ShopOrderListVo> page(PageParam pageParam, ShopOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ShopOrder> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("o")
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("o.point,activity_type,order_id,order_no,order_type,order_from,o.out_trade_no,o.status,o.member_id,ip,goods_money,delivery_money,order_money,o.create_time,o.pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,o.pay_money");
        queryWrapper.eq("o.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("o.create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getSearchType()) && ObjectUtil.isNotEmpty(searchParam.getSearchName())) {
            queryWrapper.like("o." + searchParam.getSearchType(), searchParam.getSearchName());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("o.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom())) queryWrapper.eq("o.order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getPayType())) queryWrapper.eq("p.type", searchParam.getPayType());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "o.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", searchParam.getPayTime());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.and(i -> i.eq("taker_name", searchParam.getKeyword()).or().eq("taker_mobile", searchParam.getKeyword()));
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType())) queryWrapper.eq("o.activity_type", searchParam.getActivityType());

        IPage<ShopOrderListVo> iPage = shopOrderMapper.selectJoinPage(new Page<>(page, limit), ShopOrderListVo.class, queryWrapper);
        for (ShopOrderListVo vo : iPage.getRecords()) {
            List<ShopOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", vo.getOrderId())), ShopOrderListVo.OrderGoods.class);
            vo.setOrderGoods(orderGoods);

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);

            if (!vo.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
                if (pay != null) {
                    ShopOrderListVo.Pay payVo = new ShopOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);
                    vo.setPay(payVo);
                }
            }
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 订单详情
     * @param id 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public ShopOrderInfoVo detail(Integer id) {
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .select("relate_id,activity_type,point,order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate,form_record_id"));

        Assert.notNull(model, "订单不存在");

        ShopOrderInfoVo vo = new ShopOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
        BeanUtil.copyProperties(member, memberVo);
        vo.setMember(memberVo);

        // 查询订单项
        List<ShopOrderInfoVo.OrderGoods> orderGoods = CollectUtils.convert(shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderGoods.class);

        for(ShopOrderInfoVo.OrderGoods orderGoodsVo : orderGoods){
            ShopGoods goodsInfo = shopGoodsMapper.selectById(orderGoodsVo.getGoodsId());
            orderGoodsVo.setUnit(goodsInfo.getUnit().equals("") ? "件"  : goodsInfo.getUnit() );
            coreManjianService.getOrderGoodsGiveInfo(orderGoodsVo, orderGoods, orderGoodsVo.getSiteId(), orderGoodsVo.getMemberId());
        }

        vo.setOrderGoods(orderGoods);



        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                ShopOrderInfoVo.Pay payVo = new ShopOrderInfoVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                vo.setPay(payVo);
            }
        }

        // 查询订单自提门店
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            ShopStore store = shopStoreMapper.selectOne(new QueryWrapper<ShopStore>().eq("store_id", vo.getTakeStoreId()));
            if (store != null) {
                ShopOrderInfoVo.Store storeVo = new ShopOrderInfoVo.Store();
                BeanUtil.copyProperties(store, storeVo);
                vo.setStore(storeVo);
            }
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            List<ShopOrderInfoVo.OrderDelivery> orderDelivery = CollectUtils.convert(shopOrderDeliveryMapper.selectList(new QueryWrapper<ShopOrderDelivery>().eq("order_id", vo.getOrderId())), ShopOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery);
        }



        List<ShopOrderLogListVo> orderLog = CollectUtils.convert(shopOrderLogMapper.selectList(new QueryWrapper<ShopOrderLog>().eq("order_id", vo.getOrderId()).orderByDesc("create_time")), ShopOrderLogListVo.class);
        vo.setOrderLog(orderLog);

        return vo;
    }

    /**
     * 关闭订单
     *
     * @param id
     */
    @Override
    public void close(Integer id) {
        OrderCloseParam closeParam = new OrderCloseParam();
        closeParam.setOrderId(id);
        closeParam.setSiteId(RequestUtils.siteId());
        closeParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        closeParam.setMainId(RequestUtils.uid());
        closeParam.setCloseType(OrderCloseTypeEnum.SHOP_CLOSE.getType());
        coreOrderService.close(closeParam);
    }

    /**
     * 设置商家备注
     *
     * @param param
     */
    @Override
    public void setShopRemark(SetShopRemarkParam param) {
        ShopOrder model = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .select("order_id,status")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "订单不存在");

        model.setShopRemark(param.getShopRemark());
        shopOrderMapper.updateById(model);

        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(model.getOrderId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_REMARK_ACTION.getAction());
        coreOrderLogService.add(logParam);
    }

    /**
     * 订单调价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editPrice(EditPriceParam param) {
        ShopOrder order = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .select("order_id,site_id,status,order_type,goods_money,delivery_money,order_money,discount_money")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以改价");

        // 关闭已经发起的支付单据
        corePayService.closeByTrade(order.getSiteId(), order.getOrderType(), order.getOrderId());

        BigDecimal goodsMoney = new BigDecimal(0);
        List<ShopOrderGoods> orderGoods = shopOrderGoodsMapper.selectList(new QueryWrapper<ShopOrderGoods>().eq("order_id", order.getOrderId()).select("order_goods_id,order_goods_money,goods_money,price,discount_money,num"));

        for (ShopOrderGoods item: orderGoods) {
            if (ObjectUtil.isNotEmpty(param.getOrderGoodsData().get(item.getOrderGoodsId()))) {
                BigDecimal money = param.getOrderGoodsData().get(item.getOrderGoodsId()).getMoney();
                if (money.compareTo(BigDecimal.ZERO) != 0) {
                    ShopOrderGoods orderGoodsUpdate = new ShopOrderGoods();
                    orderGoodsUpdate.setOrderGoodsId(item.getOrderGoodsId());
                    orderGoodsUpdate.setOrderGoodsMoney(item.getOrderGoodsMoney().add(money));
                    if (orderGoodsUpdate.getOrderGoodsMoney().compareTo(BigDecimal.ZERO) == -1) {
                        throw new CommonException("订单项小计总额不能小于0");
                    }
                    orderGoodsUpdate.setGoodsMoney(orderGoodsUpdate.getOrderGoodsMoney().add(item.getDiscountMoney()));
                    orderGoodsUpdate.setPrice(orderGoodsUpdate.getGoodsMoney().divide(new BigDecimal(item.getNum()), 2, BigDecimal.ROUND_HALF_UP));
                    shopOrderGoodsMapper.updateById(orderGoodsUpdate);
                    goodsMoney = goodsMoney.add(orderGoodsUpdate.getGoodsMoney());
                    continue;
                }
            }
            goodsMoney = goodsMoney.add(item.getGoodsMoney());
        }

        // 修改订单表
        ShopOrder orderUpdate = new ShopOrder();
        orderUpdate.setOrderId(order.getOrderId());
        orderUpdate.setOrderMoney(goodsMoney.add(param.getDeliveryMoney()).subtract(order.getDiscountMoney()));
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == -1) orderUpdate.setOrderMoney(BigDecimal.ZERO);
        orderUpdate.setDeliveryMoney(param.getDeliveryMoney());
        orderUpdate.setGoodsMoney(goodsMoney);
        this.shopOrderMapper.updateById(orderUpdate);

        // 添加订单操作日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_EDIT_PRICE_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 如果调价后的订单金额为0
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
            OrderPayParam payParam = new OrderPayParam();
            payParam.setTradeId(order.getOrderId());
            payParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            payParam.setMainId(0);
            coreOrderService.pay(payParam);
        }
    }

    /**
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    @Override
    public EditDeliveryVo getEditDeliveryData(EditDeliveryParam param) {
        ShopOrder order = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以修改");
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) throw new CommonException("虚拟商品订单不可以修改配送地址");

        EditDeliveryVo vo = new EditDeliveryVo();
        BeanUtil.copyProperties(order, vo);

        return vo;
    }

    /**
     * 修改订单配送
     *
     * @param param
     */
    @Override
    public void editDelivery(EditDeliveryParam param) {
        ShopOrder order = shopOrderMapper.selectOne(
                new QueryWrapper<ShopOrder>()
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以修改");
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) throw new CommonException("虚拟商品订单不可以修改配送地址");

        if (param.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            if (ObjectUtil.isEmpty(param.getTakerName())
                    || ObjectUtil.isEmpty(param.getTakerMobile())
                    || ObjectUtil.isEmpty(param.getTakerProvince())
                    || ObjectUtil.isEmpty(param.getTakerCity())
                    || ObjectUtil.isEmpty(param.getTakerDistrict())
                    || ObjectUtil.isEmpty(param.getTakerAddress())
                    || ObjectUtil.isEmpty(param.getTakerFullAddress())
            ) {
                throw new CommonException("地址信息有误");
            }
        } else if (param.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            if (ObjectUtil.isEmpty(param.getTakerName())
                    || ObjectUtil.isEmpty(param.getTakerMobile())
                    || ObjectUtil.isEmpty(param.getTakerProvince())
                    || ObjectUtil.isEmpty(param.getTakerCity())
                    || ObjectUtil.isEmpty(param.getTakerDistrict())
                    || ObjectUtil.isEmpty(param.getTakerAddress())
                    || ObjectUtil.isEmpty(param.getTakerFullAddress())
                    || ObjectUtil.isEmpty(param.getTakerLatitude())
                    || ObjectUtil.isEmpty(param.getTakerLongitude())
            ) {
                throw new CommonException("地址信息有误");
            }
        } else if (param.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            if (ObjectUtil.isEmpty(param.getTakeStoreId())) throw new CommonException("地址信息有误");
        } else {
            throw new CommonException("不支持的配送方式");
        }

        ShopOrder model = new ShopOrder();
        BeanUtil.copyProperties(param, model);

        shopOrderMapper.updateById(model);
    }
}
