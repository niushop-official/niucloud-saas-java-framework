package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员签到实体
 */
@Data
public class MemberSign implements Serializable {

    private static final long serialVersionUID = 1L;


    @TableId(value = "sign_id", type = IdType.AUTO)
    private Integer signId;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 连续签到天数
     */
    private Integer days;

    /**
     * 日签奖励
     */
    private String dayAward;

    /**
     * 连签奖励
     */
    private String continueAward;

    /**
     * 连签奖励标识
     */
    private String continueTag;

    /**
     * 签到时间
     */
    private Long createTime;

    /**
     * 签到周期开始时间
     */
    private Long startTime;

    /**
     * 是否签到（0未签到 1已签到）
     */
    private Integer isSign;

    @TableField(exist = false)
    private String memberNo;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String mobile;

    @TableField(exist = false)
    private String headimg;

}