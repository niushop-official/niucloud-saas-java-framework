package com.niu.core.service.admin.verify.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * Verifier视图
 */
@Data
public class VerifierInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 
    private Integer siteId;  // 站点id
    private Integer memberId;  // 会员id
    private Integer uid;  // 用户id
    private String verifyType;  // 核销类型

}
