package com.niu.core.service.admin.sys.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.notice.NoticeEnum;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.enums.sys.SmsTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * SysNoticeSmsLog列表视图
 */
@Data
public class SysNoticeSmsLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // id
    private Integer siteId;  // 
    private String mobile;  // 手机号码
    private String smsType;  // 发送关键字（注册、找回密码）
    private String key;  // 发送关键字（注册、找回密码）
    private String templateId;  //
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String params;  // 数据参数
    private String status;  // 发送状态：sending-发送中；success-发送成功；fail-发送失败
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String result;  // 短信结果
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 创建时间
    private Integer sendTime;  // 发送时间
    private String updateTime; // 更新时间
    private String smsTypeName;
    private String name;
    private String statusName;

    public String getSmsTypeName() {
        JSONObject smsTypeEnum = SmsTypeEnum.getType();
        return smsTypeEnum.getByPath(this.smsType + ".name", String.class);
    }

    public String getName() {
        Map<String, NoticeEnumListVo> notice = NoticeEnum.getNotice();
        return ObjectUtil.defaultIfNull(notice.get(this.key).getName(), "");
    }

    public String getStatusName() {
        return SmsStatusEnum.getNameByStatus(this.status);
    }
}
