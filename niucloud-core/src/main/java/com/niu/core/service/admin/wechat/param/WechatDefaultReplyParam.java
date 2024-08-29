package com.niu.core.service.admin.wechat.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
*/
@Data
public class WechatDefaultReplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "content参数缺失")
    private JSONObject content;

}
