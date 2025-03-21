package com.niu.core.service.admin.wechat.param;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 公众号消息回调参数
*/
@Data
public class WechatReplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "name参数缺失")
    private String name;

    @NotNull(message = "keyword参数缺失")
    private String keyword;

    @NotNull(message = "matchingType参数缺失")
    private String matchingType;

    @NotNull(message = "content参数缺失")
    private JSONArray content;

    private Integer sort = 0;

    @NotNull(message = "replyMethod参数缺失")
    private String replyMethod;

}
