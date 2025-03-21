package com.niu.core.entity.diy_form;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 万能表单填写配置表实体
*/
@Data
public class DiyFormWriteConfig implements Serializable {

private static final long serialVersionUID = 1L;

    /** 主键id */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 填写方式，no_limit：不限制，scan：仅限微信扫一扫，url：仅限链接进入 */
    private String writeWay;

    /** 参与会员，all_member：所有会员参与，selected_member_level：指定会员等级，selected_member_label：指定会员标签 */
    private String joinMemberType;

    /** 会员等级id集合 */
    private String levelIds;

    /** 会员标签id集合 */
    private String labelIds;

    /** 每人可填写次数，no_limit：不限制，diy：自定义 */
    private String memberWriteType;

    /** 每人可填写次数自定义规则 */
    private String memberWriteRule;

    /** 表单可填写数量，no_limit：不限制，diy：自定义 */
    private String formWriteType;

    /** 表单可填写总数自定义规则 */
    private String formWriteRule;

    /** 填写时间限制类型，no_limit：不限制， specify_time：指定开始结束时间，open_day_time：设置每日开启时间 */
    private String timeLimitType;

    /** 填写时间限制规则 */
    private String timeLimitRule;

    /** 是否允许修改自己填写的内容，0：否，1：是 */
    private Integer isAllowUpdateContent;

    /** 表单填写须知 */
    private String writeInstruction;

    /** 创建时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;

}