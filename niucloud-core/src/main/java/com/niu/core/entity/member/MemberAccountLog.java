package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 会员账单实体
 */
@Data
public class MemberAccountLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 用户id */
    private Integer memberId;

    /** 站点id */
    private Integer siteId;

    /** 账户类型 */
    private String accountType;

    /** 账户数据 */
    private BigDecimal accountData;

    /** 变动后的账户余额 */
    private BigDecimal accountSum;

    /** 来源类型 */
    private String fromType;

    /** 关联Id */
    private String relatedId;

    private Long createTime;

    /** 备注信息 */
    private String memo;

}