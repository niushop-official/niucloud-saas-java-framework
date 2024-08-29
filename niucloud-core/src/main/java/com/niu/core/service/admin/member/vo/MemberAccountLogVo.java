package com.niu.core.service.admin.member.vo;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * MemberAccountLog视图
 */
@Data
public class MemberAccountLogVo implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private String memberNo;

    private String username;

    private String nickname;

    private String mobile;

    private String headimg;

}
