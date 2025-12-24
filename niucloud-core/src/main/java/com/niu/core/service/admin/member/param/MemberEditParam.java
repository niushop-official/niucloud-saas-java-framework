package com.niu.core.service.admin.member.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员参数
*/
@Data
public class MemberEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;

    private String headimg;

    private String password;

    private List<Integer> memberLabel;

    private Integer sex = 0;

    private String birthday;

}
