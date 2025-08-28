package com.niu.core.entity.diy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiyTheme implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /**
     * 颜色名称
     */
    private String title;

    /**
     * 插件类型app，addon
     */
    private String type;

    /**
     * 所属应用，app：系统，shop：商城、o2o：上门服务
     */
    private String addon;

    /**
     * 模式，default：默认【跟随系统】，diy：自定义配色
     */
    private String mode;

    /**
     * 配色类型，default：默认，diy：自定义
     */
    private String themeType;

    /**
     * 当前色调的默认值
     */
    private String defaultTheme;

    /**
     * 当前色调
     */
    private String theme;

    /**
     * 新增颜色集合
     */
    private String newTheme;

    /**
     * 已选色调，0：否，1.是
     */
    private Integer isSelected;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private Integer siteId;

}
