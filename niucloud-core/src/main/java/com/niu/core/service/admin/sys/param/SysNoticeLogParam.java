package com.niu.core.service.admin.sys.param;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

/**
 * 通知记录参数
*/
@Data
public class SysNoticeLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "siteId参数缺失")
    private Integer siteId;

    @NotNull(message = "key参数缺失")
    private String key;

    @NotNull(message = "noticeType参数缺失")
    private String noticeType;

    @NotNull(message = "uid参数缺失")
    private Integer uid;

    @NotNull(message = "memberId参数缺失")
    private Integer memberId;

    @NotNull(message = "nickname参数缺失")
    private String nickname;

    @NotNull(message = "receiver参数缺失")
    private String receiver;

    @NotNull(message = "content参数缺失")
    private String content;

    @NotNull(message = "isClick参数缺失")
    private Integer isClick;

    @NotNull(message = "isVisit参数缺失")
    private Integer isVisit;

    @NotNull(message = "visitTime参数缺失")
    private Integer visitTime;

    @NotNull(message = "result参数缺失")
    private String result;

    @NotNull(message = "params参数缺失")
    private String params;

}
