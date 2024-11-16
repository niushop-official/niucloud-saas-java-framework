package com.niu.shop.service.admin.order.vo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.sys.SysUserMapper;
import com.niu.shop.enums.order.OrderLogActionEnum;
import com.niu.shop.enums.order.OrderLogMainTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopOrderLog列表视图
 */
@Data
public class ShopOrderLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer orderId;  // 订单id
    private String mainType;  //
    private String mainTypeName;
    private Integer mainId;  // 操作人id
    private String mainName;
    private Integer status;  // 订单状态
    private String type;  //
    private String typeName;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间

    public String getMainTypeName() {
        return OrderLogMainTypeEnum.getNameByType(this.mainType);
    }

    public String getTypeName() {
        return OrderLogActionEnum.getNameByType(this.type);
    }

    private MemberMapper memberMapper() {
        return SpringContext.bean(MemberMapper.class);
    }

    private SysUserMapper userMapper() {
        return SpringContext.bean(SysUserMapper.class);
    }

    public String getMainName() {
        if (this.mainType.equals(OrderLogMainTypeEnum.MEMBER.getType())) {
            Member member = memberMapper().selectOne(new QueryWrapper<Member>().eq("member_id", this.mainId).select("nickname"));
            if (member != null) return member.getNickname();
        }
        if (this.mainType.equals(OrderLogMainTypeEnum.STORE.getType())) {
            SysUser user = userMapper().selectOne(new QueryWrapper<SysUser>().eq("uid", this.mainId).select("username"));
            if (user != null) return user.getUsername();
        }
        return "";
    }
}
