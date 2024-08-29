package com.niu.core.service.admin.site.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.site.SiteAccountLogEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * SiteAccountLog列表视图
 */
@Data
public class SiteAccountLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private Integer siteId;  // 站点id
    private String type;  // 账单类型pay,refund,transfer
    private String typeName;
    private BigDecimal money;  // 交易金额
    private String tradeNo;  // 对应类型交易单号
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间
    private Object payInfo; //支付信息

    /**
     * 获取类型名称
     * @return
     */
    public String getTypeName() {
        SiteAccountLogEnum[] list = SiteAccountLogEnum.values();
        for (SiteAccountLogEnum item : list)
        {
            if(item.getCode().equals(this.type))
            {
                return item.getName();
            }
        }
        return "";
    }
}
