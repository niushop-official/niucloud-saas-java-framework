package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 短信发送实体
 */
@Data
public class SysNoticeSmsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 手机号码 */
    private String mobile;

    /** 短信类型 */
    private String smsType;

    /** 发送关键字 */
    @TableField(value = "`key`")
    private String key;

    /** 发送模板id */
    private String templateId; //

    /** 发送内容 */
    private String content;

    /** 数据参数 */
    private String params;

    /** 发送状态：sending-发送中；success-发送成功；fail-发送失败 */
    private String status;

    /** 短信结果 */
    private String result;

    /** 创建时间 */
    private Long createTime;

    /** 发送时间 */
    private Long sendTime;

    /** 更新时间 */
    private Long updateTime;

    /** 删除时间 */
    private Long deleteTime;

}