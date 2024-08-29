package com.niu.core.entity.oplatform;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小程序版本实体类
 */
@Data
public class WxOplatfromWeappVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
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
    private Long createTime;

    /** 修改时间 */
    private Long updateTime;

}