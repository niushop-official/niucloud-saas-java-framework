package com.niu.core.service.core.diy_form.param;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单填写配置表参数
*/
@Data
public class DiyFormWriteConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 填写方式，no_limit：不限制，scan：仅限微信扫一扫，url：仅限链接进入 */
    private String writeWay = "no_limit";

    /** 参与会员，all_member：所有会员参与，selected_member_level：指定会员等级，selected_member_label：指定会员标签 */
    private String joinMemberType = "all_member";

    /** 会员等级id集合 */
    private JSONArray levelIds = JSONUtil.parseArray("[]");

    /** 会员标签id集合 */
    private JSONArray labelIds = JSONUtil.parseArray("[]");

    /** 每人可填写次数，no_limit：不限制，diy：自定义 */
    private String memberWriteType = "no_limit";

    /** 每人可填写次数自定义规则 */
    private JSONObject memberWriteRule = JSONUtil.parseObj("{\"time_value\": 1, \"time_unit\": \"day\", \"num\": 1}");

    /** 表单可填写数量，no_limit：不限制，diy：自定义 */
    private String formWriteType = "no_limit";

    /** 表单可填写总数自定义规则 */
    private JSONObject formWriteRule = JSONUtil.parseObj("{\"time_value\": 1, \"time_unit\": \"day\", \"num\": 1}");

    /** 填写时间限制类型，no_limit：不限制， specify_time：指定开始结束时间，open_day_time：设置每日开启时间 */
    private String timeLimitType = "no_limit";

    /** 填写时间限制规则 */
    private JSONObject timeLimitRule = JSONUtil.parseObj("{\"specify_time\": [], \"specify_time\": []}");

    /** 是否允许修改自己填写的内容，0：否，1：是 */
    private Integer isAllowUpdateContent = 0;

    /** 表单填写须知 */
    private String writeInstruction = "";


}
