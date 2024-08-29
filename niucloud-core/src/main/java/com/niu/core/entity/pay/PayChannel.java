package com.niu.core.entity.pay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 支付渠道配置实体
 */
@Data
public class PayChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 支付类型 */
    private String type;

    /** 支付渠道 */
    private String channel;

    /** 支付配置 */
    private String config;

    /** 创建时间 */
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

    /** 是否启用 */
    private Integer status;

    /** 排序 */
    private Integer sort;

}