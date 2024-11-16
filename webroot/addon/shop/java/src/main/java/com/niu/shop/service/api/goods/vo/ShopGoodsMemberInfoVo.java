package com.niu.shop.service.api.goods.vo;

import com.niu.core.entity.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ShopGoodsMemberInfoVo extends Member {
    private MemberLevel level;

    @Data
    public static class MemberLevel {
        private Integer levelId;

        /**
         * 站点id
         */
        private Integer siteId;

        /**
         * 等级名称
         */
        private String levelName;

        /**
         * 所需成长值
         */
        private Integer growth;

        /**
         * 备注
         */
        private String remark;

        /**
         * 状态 0已禁用1已启用
         */
        private Integer status;

        /**
         * 添加时间
         */
        private Long createTime;

        /**
         * 更新时间
         */
        private Long updateTime;

        /**
         * 等级权益
         */
        private String levelBenefits;

        /**
         * 等级礼包
         */
        private String levelGifts;
    }
}
