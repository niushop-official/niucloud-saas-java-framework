package com.niu.shop.service.admin.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.shop.enums.order.InvoiceHeaderTypeEnum;
import com.niu.shop.enums.order.InvoiceTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopInvoice列表视图
 */
@Data
public class ShopInvoiceListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 发票id
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private String tradeType;  // 开票分类 order:订单
    private Integer tradeId;  // 业务id
    private Integer headerType;  // 抬头类型
    private String headerName;  // 名称（发票抬头）
    private Integer type;  // 发票类型
    private String name;  // 发票内容
    private String taxNumber;  // 公司税号
    private String mobile;  // 开票人手机号
    private String email;  // 开票人邮箱
    private String telephone;  // 注册电话
    private String address;  // 注册地址
    private String bankName;  // 开户银行
    private String bankCardNumber;  // 银行账号
    private BigDecimal money;  // 开票金额
    private Integer isInvoice;  // 是否开票
    private String invoiceNumber;  // 发票代码
    private String invoiceVoucher;  // 发票凭证
    private String remark;  // 备注
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 申请时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long invoiceTime;  // 开票时间
    private Integer status;  // 是否生效

    /**
     * 获取头名称
     * @return
     */
    public String getHeaderTypeName(){
        return InvoiceHeaderTypeEnum.getNameByType(this.headerType);
    }

    /**
     * 获取类型名称
     * @return
     */
    public String getTypeName(){
        return InvoiceTypeEnum.getNameByType(this.type);
    }


}
