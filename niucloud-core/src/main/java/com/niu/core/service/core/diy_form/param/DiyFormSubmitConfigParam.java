package com.niu.core.service.core.diy_form.param;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单提交页配置参数
*/
@Data
public class DiyFormSubmitConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    /** 站点id */
    private Integer siteId;

    /** 所属万能表单id */
    private Integer formId;

    /** 填表人提交后操作，text：文字信息，voucher：核销凭证 */
    private String submitAfterAction = "text";

    /** 提示内容类型，default：默认提示，diy：自定义提示 */
    private String tipsType = "default";

    /** 自定义提示内容 */
    private String tipsText = "";

    /** 核销凭证有效期限制类型，no_limit：不限制，specify_time：指定固定开始结束时间，submission_time：按提交时间设置有效期 */
    private String timeLimitType = "no_limit";

    /** 核销凭证时间限制规则，json格式 */
    private JSONObject timeLimitRule = JSONUtil.parseObj("{}");

    /** 核销凭证内容，json格式 */
    private JSONObject voucherContentRule = JSONUtil.parseObj("{'share': false, 'finish': true, 'goback': true}");

    /** 填写成功后续操作 */
    private JSONObject successAfterAction = JSONUtil.parseObj("{}");


}
