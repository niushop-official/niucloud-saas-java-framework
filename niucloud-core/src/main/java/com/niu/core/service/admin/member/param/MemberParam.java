package com.niu.core.service.admin.member.param;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;

/**
 * 会员参数
*/
@Data
public class MemberParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;

    private String memberNo;

    private Integer pid;

    private Integer siteId;

    private String username;

    private String mobile;

    private String password;

    private String nickname;

    private String headimg;

    private Integer memberLevel;

    private String memberLabel;

    private String wxOpenid;

    private String weappOpenid;

    private String wxUnionid;

    private String aliOpenid;

    private String douyinOpenid;

    private String registerChannel;

    private String registerType;

    private String loginIp;

    private String loginType;

    private String loginChannel;

    private Integer loginCount;

    private Integer loginTime;

    private Integer lastVisitTime;

    private Integer lastConsumTime;

    private Integer sex;

    private Integer status;

    private String birthday;

    private Integer point;

    private Integer pointGet;

    private BigDecimal balance;

    private BigDecimal balanceGet;

    private BigDecimal money;

    private BigDecimal moneyGet;

    private BigDecimal moneyCashOuting;

    private Integer growth;

    private Integer growthGet;

    private BigDecimal commission;

    private BigDecimal commissionGet;

    private BigDecimal commissionCashOuting;

    private Integer isMember;

    private Integer memberTime;

    private Integer isDel;

    private Integer provinceId;

    private Integer cityId;

    private Integer districtId;

    private String address;

    private String location;

    private Integer[] memberIds;

}
