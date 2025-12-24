package com.niu.shop.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopInvoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
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
    private Long createTime; // 申请时间
    private Long invoiceTime; // 开票时间
    private Integer status; // 是否生效
    private String payVoucher;
}