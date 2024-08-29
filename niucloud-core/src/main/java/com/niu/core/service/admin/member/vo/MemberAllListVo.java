package com.niu.core.service.admin.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Member列表视图
 */
@Data
public class MemberAllListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;  // 主键
    private String nickname;  // 会员昵称
    private String headimg;  // 会员头像
}
