package com.niu.core.service.api.member.vo;

import com.niu.core.entity.member.MemberLevel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberInfoVo {

    /**
     * 主键
     */
    private Integer memberId;

    /**
     * 会员编码
     */
    private String memberNo;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 会员用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 注册来源
     */
    private String registerChannel;

    /**
     * 会员昵称
     */
    private String nickname;

    /**
     * 会员头像
     */
    private String headimg;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 会员标签
     */
    private String memberLabel;

    /**
     * 当前登录ip
     */
    private String loginIp;

    /**
     * 当前登录的操作终端类型
     */
    private String loginType;

    /**
     * 当前登录时间
     */
    private Long loginTime;

    /**
     * 注册时间
     */
    private Long createTime;

    /**
     * 最后访问时间
     */
    private Long lastVisitTime;

    /**
     * 最后消费时间
     */
    private Integer lastConsumTime;

    /**
     * 性别 0保密 1男 2女
     */
    private Integer sex;

    /**
     * 用户状态  用户状态默认为1
     */
    private Integer status;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 可用积分
     */
    private Integer point;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 成长值
     */
    private Integer growth;

    /**
     * 是否是会员
     */
    private Integer isMember;

    /**
     * 成为会员时间
     */
    private Integer memberTime;

    /**
     * 0正常  1已删除
     */
    private Integer isDel;

    /**
     * 省id
     */
    private Integer provinceId;

    /**
     * 市id
     */
    private Integer cityId;

    /**
     * 区县id
     */
    private Integer districtId;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 定位地址
     */
    private String location;

    /**
     * 可用余额（可提现）
     */
    private BigDecimal money;

    /**
     * 累计获取余额（可提现）
     */
    private BigDecimal moneyGet;

    /**
     * 微信用户openid
     */
    private String wxOpenid;

    /**
     * 微信小程序openid
     */
    private String weappOpenid;

    /**
     * 当前佣金
     */
    private BigDecimal commission;

    /**
     * 佣金获取
     */
    private BigDecimal commissionGet;

    /**
     * 提现中佣金
     */
    private BigDecimal commissionCashOuting;

    /**
     * 会员级别名称
     */
    private String memberLevelName;

    /**
     * 性别 0保密 1男 2女
     *
     * @return
     */
    public String getSexName() {
        if (this.getSex() == 1) {
            return "男";
        } else if (this.getSex() == 2) {
            return "女";
        }
        return "保密";
    }

}
