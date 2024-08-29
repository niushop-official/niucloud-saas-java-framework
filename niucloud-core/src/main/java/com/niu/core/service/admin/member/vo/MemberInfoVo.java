package com.niu.core.service.admin.member.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.common.SexEnum;
import com.niu.core.enums.member.*;
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
import java.util.List;

/**
 * Member视图
 */
@Data
public class MemberInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer memberId;  // 主键
    private String memberNo;  // 会员编码
    private Integer pid;  // 推广会员id
    private Integer siteId;  // 站点id
    private String username;  // 会员用户名
    private String mobile;  // 手机号
    private String password;  // 会员密码
    private String nickname;  // 会员昵称
    private String headimg;  // 会员头像
    private Integer memberLevel;  // 会员等级
    private String memberLevelName;  // 会员等级
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String memberLabel;  // 会员标签
    private List<MemberLabelAllListVo> memberLabelArray;
    private String wxOpenid;  // 微信用户openid
    private String weappOpenid;  // 微信小程序openid
    private String wxUnionid;  // 微信unionid
    private String aliOpenid;  // 支付宝账户id
    private String douyinOpenid;  // 抖音小程序openid
    private String registerChannel;  // 注册来源
    private String registerType;  // 注册方式
    private String loginIp;  // 当前登录ip
    private String loginType;  // 当前登录的操作终端类型
    private String loginChannel;  // 
    private Integer loginCount;  // 登录次数
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long loginTime;  // 当前登录时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long lastVisitTime;  // 最后访问时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long lastConsumTime;  // 最后消费时间
    private Integer sex;  // 性别 0保密 1男 2女
    private Integer status;  // 用户状态  用户状态默认为1
    private String birthday;  // 出生日期
    private Integer point;  // 可用积分
    private Integer pointGet;  // 累计获取积分
    private BigDecimal balance;  // 可用余额
    private BigDecimal balanceGet;  // 累计获取余额
    private BigDecimal money;  // 可用余额（可提现）
    private BigDecimal moneyGet;  // 累计获取余额（可提现）
    private BigDecimal moneyCashOuting;  // 提现中余额（可提现）
    private Integer growth;  // 成长值
    private Integer growthGet;  // 累计获得成长值
    private BigDecimal commission;  // 当前佣金
    private BigDecimal commissionGet;  // 佣金获取
    private BigDecimal commissionCashOuting;  // 提现中佣金
    private Integer isMember;  // 是否是会员
    private Integer memberTime;  // 成为会员时间
    private Integer isDel;  // 0正常  1已删除
    private Integer provinceId;  // 省id
    private Integer cityId;  // 市id
    private Integer districtId;  // 区县id
    private String address;  // 详细地址
    private String location;  // 定位地址
    private String registerChannelName; // 注册渠道
    private String registerTypeName; // 注册方式
    private String sexName;
    private String loginChannelName;
    private String loginTypeName;
    private String statusName;

    /**
     * 注册渠道名称
     * @return
     */
    public String getRegisterChannelName() {
        return MemberRegisterChannelEnum.getNameByType(this.registerChannel);
    }

    /**
     * 登录渠道名称
     * @return
     */
    public String getLoginChannelName() {
        return ChannelEnum.getNameByCode(this.loginChannel);
    }

    /**
     * 注册渠道名称
     * @return
     */
    public String getRegisterTypeName() {
        return MemberRegisterTypeEnum.getNameByType(this.registerType);
    }

    /**
     * 登录方式名称
     * @return
     */
    public String getLoginTypeName() {
        return MemberLoginTypeEnum.getNameByType(this.loginType);
    }

    /**
     * 获取性别
     * @return
     */
    public String getSexName() {
        return SexEnum.getNameBySex(this.sex);
    }

    /**
     * 获取状态
     * @return
     */
    public String getStatusName() {
        return StatusEnum.getNameByStatus(this.status);
    }
}
