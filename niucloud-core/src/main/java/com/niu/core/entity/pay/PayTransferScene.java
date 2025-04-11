package com.niu.core.entity.pay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;

/**
 * 支付转账场景表实体
 */
@Data
public class PayTransferScene implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建时间 */
    private Long createTime;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 转账报备背景 */
    private String infos;

    /** 转账收款感知 */
    private String perception;

    /** 场景 */
    private String scene;

    /** 站点id */
    private Integer siteId;

    /** 业务类型 */
    private String type;

}