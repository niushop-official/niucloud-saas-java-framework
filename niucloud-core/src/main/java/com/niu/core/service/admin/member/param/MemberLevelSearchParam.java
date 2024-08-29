package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员等级参数
 */
@Data
public class MemberLevelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String levelName;

}
