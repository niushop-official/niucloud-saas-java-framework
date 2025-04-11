package com.niu.core.service.admin.wxoplatform.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 微信提交记录 视图
 */
@Data
public class SiteGroupWeappVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer groupId;

    /** 分组名称 */
    private String groupName;

    private WxOplatfromWeappVersionVo commitRecord;

    public Object getCommitRecord() {
        if (this.commitRecord == null) {
            return new HashMap<>();
        }
        return this.commitRecord;
    }
}
