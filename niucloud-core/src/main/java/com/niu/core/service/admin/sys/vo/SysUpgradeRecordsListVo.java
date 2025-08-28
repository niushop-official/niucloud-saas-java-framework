package com.niu.core.service.admin.sys.vo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.sys.UpgradeRecordStatusEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysUpgradeRecordsListVo implements Serializable {

    /** 主键id */
    private Integer id;

    /** 升级标识 */
    private String upgradeKey;

    /** 插件标识 */
    private String appKey;

    /** 升级名称 */
    private String name;

    private String statusName = "";

    /** 升级内容 */
    private Object content;

    /** 前一版本 */
    private String prevVersion;

    /** 当前版本 */
    private String currentVersion;

    /** 状态 */
    private String status;

    /** 失败原因 */
    private String failReason;


    /** 完成时间 */
    private String completeTime;

    private String createTime;

}
