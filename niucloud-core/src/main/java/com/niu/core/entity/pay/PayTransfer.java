package com.niu.core.entity.pay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 转账实体
 */
@Data
public class PayTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 业务类型 */
    private String tradeType;

    /** 转账单号 */
    private String transferNo;

    /** 会员id */
    private Integer mainId;

    /** 主体类型 */
    private String mainType;

    /** 转账类型 */
    private String transferType;

    /** 联系人名称 */
    private String transferRealname;

    /** 手机号 */
    private String transferMobile;

    /** 银行名称 */
    private String transferBank;

    /** 收款账号 */
    private String transferAccount;

    /** 凭证 */
    private String transferVoucher;

    /** 凭证说明 */
    private String transferRemark;

    /** 失败原因 */
    private String transferFailReason;

    /** 转账状态 */
    private String transferStatus;

    /** 转账金额 */
    private BigDecimal money;

    /** 申请时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 转账时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long transferTime;

    private Long updateTime;

    private String openid;

    private String remark;

    /** 转账批次id */
    private String batchId;

}