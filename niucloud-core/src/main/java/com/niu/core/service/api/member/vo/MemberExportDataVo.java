package com.niu.core.service.api.member.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MemberExportDataVo implements Serializable {

    /** 会员id */
    private Integer memberId;

    /** 会员编号 */
    private String memberNo;

    /** 手机号 */
    private String mobile;

    /** 昵称 */
    private String nickname;

    /** 生日 */
    private String birthday;

    /** 会员等级 */
    private String memberLevel;

    /** 会员等级名称 */
    private String levelName;

    /** 积分 */
    private Integer point;

    /** 余额 */
    private BigDecimal balance;

    /** 不可提现余额 */
    private BigDecimal money;

    /** 成长值 */
    private Integer growth;

    /** 佣金 */
    private BigDecimal commission;

    /** 会员注册渠道 */
    private String registerChannel;

    /** 用户状态 */
    private Integer status;

    /** 创建时间 */
    private Long createTime;

    /** 最后访问时间 */
    private Long lastVisitTime;


}
