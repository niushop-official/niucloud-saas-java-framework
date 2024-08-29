package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 会员等级实体
 */
@Data
public class MemberLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 会员等级 */
    @TableId(value="level_id", type= IdType.AUTO)
    private Integer levelId;

    /** 站点id */
    private Integer siteId;

    /** 等级名称 */
    private String levelName;

    /** 所需成长值 */
    private Integer growth;

    /** 备注 */
    private String remark;

    /** 状态 0已禁用1已启用 */
    private Integer status;

    /** 添加时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

    /** 等级权益 */
    private String levelBenefits;

    /** 等级礼包 */
    private String levelGifts;

}