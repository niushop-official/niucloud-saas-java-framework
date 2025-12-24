package com.niu.shop.service.admin.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.shop.entity.order.ShopInvoice;
import com.niu.shop.entity.order.ShopOrder;
import com.niu.shop.enums.order.InvoiceStatusEnum;
import com.niu.shop.enums.order.InvoicedEnum;
import com.niu.shop.mapper.order.ShopInvoiceMapper;
import com.niu.shop.mapper.order.ShopOrderMapper;
import com.niu.shop.service.admin.order.IShopInvoiceService;
import com.niu.shop.service.admin.order.param.ShopInvoiceAddParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceAuditParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceParam;
import com.niu.shop.service.admin.order.param.ShopInvoiceSearchParam;
import com.niu.shop.service.admin.order.vo.ShopInvoiceInfoVo;
import com.niu.shop.service.admin.order.vo.ShopInvoiceListVo;
import com.niu.shop.service.core.order.ICoreInvoiceService;
import com.niu.shop.service.core.order.ICoreOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class ShopInvoiceServiceImpl implements IShopInvoiceService {


    @Resource
    ShopInvoiceMapper shopInvoiceMapper;

    @Resource
    ShopOrderMapper shopOrderMapper;

    @Resource
    ICoreInvoiceService coreInvoiceService;

    @Resource
    ICoreOrderService coreOrderService;

    @Override
    public PageResult<ShopInvoiceListVo> getPage(PageParam pageParam, ShopInvoiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<ShopInvoice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("status", InvoiceStatusEnum.OPEN.getStatus());

        if (ObjectUtil.isNotEmpty(searchParam.getHeaderType())) queryWrapper.eq("header_type", searchParam.getHeaderType());
        if (ObjectUtil.isNotEmpty(searchParam.getHeaderName())) queryWrapper.like("header_name", searchParam.getHeaderName());
        if (ObjectUtil.isNotEmpty(searchParam.getIsInvoice()) && !searchParam.getIsInvoice().equals("all")) queryWrapper.eq("is_invoice", searchParam.getIsInvoice());
        if (ObjectUtil.isNotEmpty(searchParam.getInvoiceTime())) QueryMapperUtils.buildByTime(queryWrapper, "invoice_time", searchParam.getInvoiceTime());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getInvoiceTime());

        queryWrapper.orderByDesc("id");
        IPage<ShopInvoice> iPage = shopInvoiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<ShopInvoiceListVo> list = new LinkedList<>();
        Integer siteId = RequestUtils.siteId();
        for (ShopInvoice item : iPage.getRecords()) {
            ShopInvoiceListVo vo = new ShopInvoiceListVo();
            BeanUtils.copyProperties(item, vo);
            // 设置是否可以跳转到订单详情
            // 默认可以跳转
            vo.setIsCanJump(1);
            // 如果tradeId包含逗号（多个订单ID），则不能跳转
            if (ObjectUtil.isNotEmpty(item.getTradeId()) && item.getTradeId().contains(",")) {
                vo.setIsCanJump(0);
            }
            
            // 生成支付凭证缩略图
            if (ObjectUtil.isNotEmpty(item.getPayVoucher())) {
                List<String> payVoucherThumbSmall = new ArrayList<>();
                List<String> payVoucherThumbMid = new ArrayList<>();
                List<String> payVoucherThumbBig = new ArrayList<>();
                String[] vouchers = item.getPayVoucher().split(",");
                for (String voucher : vouchers) {
                    if (voucher != null && !voucher.trim().isEmpty()) {
                        String thumbSmall = CommonUtils.thumbImageSmall(siteId, voucher.trim());
                        if (thumbSmall != null && !thumbSmall.isEmpty()) {
                            payVoucherThumbSmall.add(thumbSmall);
                        }
                        String thumbMid = CommonUtils.thumbImageMiddle(siteId, voucher.trim());
                        if (thumbMid != null && !thumbMid.isEmpty()) {
                            payVoucherThumbMid.add(thumbMid);
                        }
                        String thumbBig = CommonUtils.thumbImageBig(siteId, voucher.trim());
                        if (thumbBig != null && !thumbBig.isEmpty()) {
                            payVoucherThumbBig.add(thumbBig);
                        }
                    }
                }
                vo.setPayVoucherThumbSmall(payVoucherThumbSmall);
                vo.setPayVoucherThumbMid(payVoucherThumbMid);
                vo.setPayVoucherThumbBig(payVoucherThumbBig);
            }
            
            // 生成发票凭证缩略图
            if (ObjectUtil.isNotEmpty(item.getInvoiceVoucher())) {
                List<String> invoiceVoucherThumbSmall = new ArrayList<>();
                List<String> invoiceVoucherThumbMid = new ArrayList<>();
                List<String> invoiceVoucherThumbBig = new ArrayList<>();
                String thumbSmall = CommonUtils.thumbImageSmall(siteId, item.getInvoiceVoucher().trim());
                if (thumbSmall != null && !thumbSmall.isEmpty()) {
                    invoiceVoucherThumbSmall.add(thumbSmall);
                }
                String thumbMid = CommonUtils.thumbImageMiddle(siteId, item.getInvoiceVoucher().trim());
                if (thumbMid != null && !thumbMid.isEmpty()) {
                    invoiceVoucherThumbMid.add(thumbMid);
                }
                String thumbBig = CommonUtils.thumbImageBig(siteId, item.getInvoiceVoucher().trim());
                if (thumbBig != null && !thumbBig.isEmpty()) {
                    invoiceVoucherThumbBig.add(thumbBig);
                }
                vo.setInvoiceVoucherThumbSmall(invoiceVoucherThumbSmall);
                vo.setInvoiceVoucherThumbMid(invoiceVoucherThumbMid);
                vo.setInvoiceVoucherThumbBig(invoiceVoucherThumbBig);
            }
            
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public ShopInvoiceInfoVo getInfo(Integer id) {
        ShopInvoice model = shopInvoiceMapper.selectOne(
                new QueryWrapper<ShopInvoice>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", InvoiceStatusEnum.OPEN.getStatus())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        ShopInvoiceInfoVo vo = new ShopInvoiceInfoVo();
        BeanUtils.copyProperties(model, vo);
        
        // 生成支付凭证缩略图
        Integer siteId = RequestUtils.siteId();
        if (ObjectUtil.isNotEmpty(model.getPayVoucher())) {
            List<String> payVoucherThumbSmall = new ArrayList<>();
            List<String> payVoucherThumbMid = new ArrayList<>();
            List<String> payVoucherThumbBig = new ArrayList<>();
            String[] vouchers = model.getPayVoucher().split(",");
            for (String voucher : vouchers) {
                if (voucher != null && !voucher.trim().isEmpty()) {
                    String thumbSmall = CommonUtils.thumbImageSmall(siteId, voucher.trim());
                    if (thumbSmall != null && !thumbSmall.isEmpty()) {
                        payVoucherThumbSmall.add(thumbSmall);
                    }
                    String thumbMid = CommonUtils.thumbImageMiddle(siteId, voucher.trim());
                    if (thumbMid != null && !thumbMid.isEmpty()) {
                        payVoucherThumbMid.add(thumbMid);
                    }
                    String thumbBig = CommonUtils.thumbImageBig(siteId, voucher.trim());
                    if (thumbBig != null && !thumbBig.isEmpty()) {
                        payVoucherThumbBig.add(thumbBig);
                    }
                }
            }
            vo.setPayVoucherThumbSmall(payVoucherThumbSmall);
            vo.setPayVoucherThumbMid(payVoucherThumbMid);
            vo.setPayVoucherThumbBig(payVoucherThumbBig);
        }
        
        return vo;
    }

    @Override
    public void invoicing(Integer id, ShopInvoiceParam shopInvoiceParam) {
        ShopInvoice model = shopInvoiceMapper.selectOne(
                new QueryWrapper<ShopInvoice>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("status", 2)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在");
        if(model.getIsInvoice() == 1) throw new AdminException("订单已开票");
        model.setIsInvoice(1);
        model.setInvoiceNumber(shopInvoiceParam.getInvoiceNumber());
        model.setInvoiceVoucher(shopInvoiceParam.getInvoiceVoucher());
        model.setRemark(shopInvoiceParam.getRemark());
        model.setInvoiceTime(System.currentTimeMillis() / 1000);
        shopInvoiceMapper.updateById(model);

    }

    @Override
    public void audit(ShopInvoiceAuditParam param) {
        coreInvoiceService.audit(param.getIds());
    }

    @Override
    public void add(ShopInvoiceAddParam param) {
        ShopOrder shopOrder = shopOrderMapper.selectOne(new QueryWrapper<ShopOrder>()
            .eq("order_no", param.getOrderNo())
            .eq("site_id", RequestUtils.siteId())
            .last("limit 1"));
        Assert.notNull(shopOrder, "订单不存在");

        ShopInvoice invoiceInfo = shopInvoiceMapper.selectOne(new QueryWrapper<ShopInvoice>()
            .eq("trade_id", shopOrder.getOrderNo())
            .eq("member_id", shopOrder.getMemberId())
            .last("limit 1"));
        Assert.isNull(invoiceInfo, "发票已存在");

        ShopInvoice shopInvoice = new ShopInvoice();
        BeanUtils.copyProperties(param, shopInvoice);
        shopInvoice.setSiteId(RequestUtils.siteId());
        shopInvoice.setMemberId(shopOrder.getMemberId());
        shopInvoice.setTradeType("shop");
        shopInvoice.setTradeId(shopOrder.getOrderId().toString());
        shopInvoice.setIsInvoice(InvoicedEnum.WAIT_INVOICE.getStatus());
        shopInvoice.setCreateTime(System.currentTimeMillis() / 1000);
        shopInvoice.setStatus(InvoiceStatusEnum.OPEN.getStatus());
        shopInvoiceMapper.insert(shopInvoice);
        shopOrderMapper.update(null, new UpdateWrapper<ShopOrder>().eq("order_id", shopOrder.getOrderId()).set("invoice_id", shopInvoice.getId()));
    }
}
