package com.niu.core.service.admin.wechat.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 公众号消息回调参数
 */
@Data
public class WechatReplySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer siteId;

    private String keyword;

    private String replyType;

    private String matchingType;

    private String content;

    private Integer sort;

    private String replyMethod;

}
