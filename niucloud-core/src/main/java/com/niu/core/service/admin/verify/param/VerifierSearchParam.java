package com.niu.core.service.admin.verify.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 核销员参数
 */
@Data
public class VerifierSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private Integer memberId;

    private Integer uid;

    private String verifyType;

}
