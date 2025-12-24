package com.niu.shop.service.api.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.shop.enums.order.InvoiceHeaderTypeEnum;
import com.niu.shop.enums.order.InvoiceTypeEnum;
import com.niu.shop.enums.order.InvoicedEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ShopInvoice列表视图
 */
@Data
public class ShopInvoiceListVo implements Serializable {

    private Integer id; // 发票id
    private Integer siteId; // 站点id
    private Integer memberId; // 会员id
    private String tradeType; // 开票分类 order:订单
    private String tradeId; // 业务id
    private Integer headerType; // 抬头类型
    private String headerName; // 名称（发票抬头）
    private Integer type; // 发票类型
    private String name; // 发票内容
    private String taxNumber; // 公司税号
    private String mobile; // 开票人手机号
    private String email; // 开票人邮箱
    private String telephone; // 注册电话
    private String address; // 注册地址
    private String bankName; // 开户银行
    private String bankCardNumber; // 银行账号
    private BigDecimal money; // 开票金额
    private Integer isInvoice; // 是否开票
    private String invoiceNumber; // 发票代码
    private String invoiceVoucher; // 发票凭证
    private String remark; // 备注
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 申请时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long invoiceTime; // 开票时间
    private Integer status; // 是否生效
    private String payVoucher;

    private String headerTypeName;
    private String typeName;
    private String invoiceTypeName;
    private String payVoucherThumbMid;
    private String payVoucherThumbSmall;
    private String payVoucherThumbBig;
    private String invoiceVoucherThumbMid;
    private String invoiceVoucherThumbSmall;
    private String invoiceVoucherThumbBig;

    private Member member;


    public String getHeaderTypeName() {
        return InvoiceHeaderTypeEnum.getNameByType(this.headerType);
    }

    public String getTypeName() {
        return InvoiceTypeEnum.getNameByType(this.type);
    }

    public String getInvoiceTypeName() {
        return InvoicedEnum.getNameByStatus(this.isInvoice);
    }

    public List<String> getPayVoucherThumbMid(){
        if (StringUtils.isEmpty(this.payVoucher)){
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String vouchers : this.payVoucher.split(",")) {
            result.add(CommonUtils.thumbImageMiddle(this.siteId, vouchers));
        }
        return result;
    }

    public List<String> getPayVoucherThumbSmall(){
        if (StringUtils.isEmpty(this.payVoucher)){
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String vouchers : this.payVoucher.split(",")) {
            result.add(CommonUtils.thumbImageSmall(this.siteId, vouchers));
        }
        return result;
    }

    public List<String> getPayVoucherThumbBig(){
        if (StringUtils.isEmpty(this.payVoucher)){
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String vouchers : this.payVoucher.split(",")) {
            result.add(CommonUtils.thumbImageBig(this.siteId, vouchers));
        }
        return result;
    }

    public String getInvoiceVoucherThumbMid(){
        if (StringUtils.isEmpty(this.invoiceVoucher)){
            return "";
        }
        return CommonUtils.thumbImageMiddle(this.siteId, this.invoiceVoucher);
    }

    public String getInvoiceVoucherThumbSmall(){
        if (StringUtils.isEmpty(this.invoiceVoucher)){
            return "";
        }
        return CommonUtils.thumbImageSmall(this.siteId, this.invoiceVoucher);
    }

    public String getInvoiceVoucherThumbBig(){
        if (StringUtils.isEmpty(this.invoiceVoucher)){
            return "";
        }
        return CommonUtils.thumbImageBig(this.siteId, this.invoiceVoucher);
    }


}
