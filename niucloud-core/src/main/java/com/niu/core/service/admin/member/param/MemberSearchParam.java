package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;

/**
 * 会员参数
 */
@Data
public class MemberSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 关键字 */
    private String keyword;

    /** 注册类型 */
    private String registerType;

    /** 注册途径 */
    private String registerChannel;

    /** 创建时间筛选 */
    private String[] createTime;

    /** 会员标签 */
    private Integer memberLabel;

    /** 会员等级 */
    private Integer memberLevel;

    /** 是否删除 */
    private Integer isDel;
}
