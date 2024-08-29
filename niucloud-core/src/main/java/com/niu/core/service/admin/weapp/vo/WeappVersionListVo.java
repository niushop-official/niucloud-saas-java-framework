package com.niu.core.service.admin.weapp.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.channel.WeappVersionStatusEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * weapp版本列表
 */
@Data
public class WeappVersionListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**  站点id */
    private Integer siteId;

    /**  版本 */
    private String version;

    /**  版本号 */
    private String versionNo;

    /**  说明 */
    private String desc;

    /**  创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**  状态 */
    private Integer status;

    /**  更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    /**  失败原因 */
    private String failReason;

    /**  上传任务key */
    private String taskKey;

    /**  提交来源 */
    private String fromType;

    /**  审核id */
    private String auditid;

    private String statusName;

    public String getStatusName() {
        return WeappVersionStatusEnum.getNameByStatus(this.status);
    }
}
