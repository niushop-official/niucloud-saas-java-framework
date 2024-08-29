package com.niu.core.service.admin.sys.vo;
import lombok.Data;

import java.io.Serializable;

/**
 * SysNoticeLog视图
 */
@Data
public class SysNoticeLogInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 通知记录ID
    private Integer siteId;  // 站点id
    private String key;  // 消息key
    private String noticeType;  // 消息类型（sms,wechat.weapp）
    private Integer uid;  // 通知的用户id
    private Integer memberId;  // 消息的会员id
    private String nickname;  // 接收人用户昵称或姓名
    private String receiver;  // 接收人（对应手机号，openid）
    private String content;  // 消息数据
    private Integer isClick;  // 点击次数
    private Integer isVisit;  // 访问次数
    private Integer visitTime;  // 访问时间
    private String result;  // 结果
    private String params;  // 

}
