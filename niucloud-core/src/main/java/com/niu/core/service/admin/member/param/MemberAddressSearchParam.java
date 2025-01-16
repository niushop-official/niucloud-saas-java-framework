package com.niu.core.service.admin.member.param;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员收货地址参数
 */
@Data
public class MemberAddressSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

}
