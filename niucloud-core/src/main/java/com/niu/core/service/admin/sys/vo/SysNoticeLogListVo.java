package com.niu.core.service.admin.sys.vo;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.notice.NoticeEnum;
import com.niu.core.enums.notice.NoticeTypeEnum;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysNoticeLog列表视图
 */
@Data
public class SysNoticeLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 通知记录ID
    private Integer siteId;  // 站点id
    private String key;  // 消息key
    private String noticeType;  // 消息类型（sms,wechat.weapp）
    private Integer uid;  // 通知的用户id
    private Integer memberId;  // 消息的会员id
    private String nickname;  // 接收人用户昵称或姓名
    private String receiver;  // 接收人（对应手机号，openid）
    private Integer isClick;  // 点击次数
    private Integer isVisit;  // 访问次数
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long visitTime;  // 访问时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 消息时间
    private String result;  // 结果
    private String params;  //
    private String noticeTypeName;
    private String name;

    public String getNoticeTypeName() {
        return NoticeTypeEnum.getNameByType(this.noticeType);
    }

}
