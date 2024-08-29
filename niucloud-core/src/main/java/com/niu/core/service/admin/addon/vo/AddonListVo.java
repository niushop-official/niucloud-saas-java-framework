package com.niu.core.service.admin.addon.vo;

import com.niu.core.common.utils.date.DateUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * AddonVo
 */
@Data
public class AddonListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;  // 主键
    private String title;  // 插件名称
    private String icon;  // 插件图标
    private String key;  // 插件标识
    private String desc;  // 插件描述
    private Integer status;  // 状态
    private String author;  // 作者
    private String version;  // 版本号
    private Long createTime; // 创建时间
    private Long installTime;  // 安装时间
    private Long updateTime; // 更新时间
    private String cover;  // 封面
    private String type;  // 插件类型app，addon
    private String typeName;
    private String supportApp;  // 插件支持的应用空表示通用插件
    private Integer isStar;  // 是否加星
    private String compile;  // 编译端口

    public String getCreateTime() {
        return DateUtils.timestampToString(createTime);
    }

    public String getInstallTime() {
        return DateUtils.timestampToString(installTime);
    }

    public String getUpdateTime() {
        return DateUtils.timestampToString(updateTime);
    }

    public String getTypeName() {
        if (type != null) {
            if (type.equals("app")) {
                return "应用";
            } else {
                return "插件";
            }
        }
        return "";
    }

}
