package com.niu.core.entity.addon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 插件日志实体
 */
@Data
public class AddonLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 操作类型   install 安装 uninstall 卸载 update 更新 */
    private String action;

    /** 插件标识 */
    private String key;

    /** 升级前的版本号 */
    private String fromVersion;

    /** 升级后的版本号 */
    private String toVersion;

    /** 创建时间 */
    private Long createTime;

}