package com.niu.core.service.admin.wxoplatform.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import com.niu.core.enums.member.StatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信提交记录 视图
 */
@Data
public class WxOplatfromWeappVersionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**  主键   */
    private Integer id;

    /** 站点套餐id */
    private Integer siteGroupId;

    /** 代码模板 ID */
    private String templateId;

    /** 代码版本号 */
    private String userVersion;

    /** 代码描述 */
    private String userDesc;

    /** 上传任务key */
    private String taskKey;

    /** 状态 */
    private Integer status;

    /** 失败原因 */
    private String failReason;

    /** 版本号 */
    private Integer versionNo;

    /** 创建时间 */
    @JsonSerialize(using = com.niu.core.common.domain.BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 修改时间 */
    @JsonSerialize(using = com.niu.core.common.domain.BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    /** 状态名称 */
    private String statusName;

    /** 应用名称 */
    private String siteGroupName;

    public String getStatusName() {
        return WeappVersionStatusEnum.getNameByStatus(this.status);
    }
}
