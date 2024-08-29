package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 站点账单记录实体
 */
@Data
public class SiteAccountLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 账单类型pay,refund,transfer */
    private String type;

    /** 交易金额 */
    private BigDecimal money;

    /** 对应类型交易单号 */
    private String tradeNo;

    /** 添加时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

}