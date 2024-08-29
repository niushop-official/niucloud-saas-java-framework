package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 通知记录实体
 */
@Data
public class SysNoticeLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 通知记录ID */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 消息key */
    @TableField(value = "`key`")
    private String key;

    /** 消息类型（sms,wechat.weapp） */
    private String noticeType;

    /** 通知的用户id */
    private Integer uid;

    /** 消息的会员id */
    private Integer memberId;

    /** 接收人用户昵称或姓名 */
    private String nickname;

    /** 接收人（对应手机号，openid） */
    private String receiver;

    /** 消息数据 */
    private String content;

    /** 点击次数 */
    private Integer isClick;

    /** 访问次数 */
    private Integer isVisit;

    /** 访问时间 */
    private Integer visitTime;

    /** 消息时间 */
    private Long createTime;

    /** 结果 */
    private String result;

    private String params;

}