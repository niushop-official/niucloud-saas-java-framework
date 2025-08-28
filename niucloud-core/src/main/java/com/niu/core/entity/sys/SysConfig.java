package com.niu.core.entity.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 配置实体
 */
@Data
public class SysConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer Id;

    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 站点名称
     */
    @TableField(select = false)
    private String siteName;

    /**
     * 配置项关键字
     */
    private String configKey;

    /**
     * 配置项json
     */
    private String value;

    /**
     * 是否启用 1启用 0不启用
     */
    private Integer status;

    /**
     * 添加时间
     */
    private Long createTime;

    /**
     * 最后修改时间
     */
    private Long updateTime;

    /**
     * 所属插件
     */
    private String addon;

    /**
     * 返回 JSON对象
     *
     * @return
     */
    public JSONObject getObjectValue() {
        if (this.getValue() != null && this.getValue().startsWith("{")) {
            return JSONUtil.parseObj(this.getValue());
        }
        return new JSONObject();
    }

    /**
     * 返回 JSON数组
     *
     * @return
     */
    public JSONArray getArrayValue() {
        if (this.getValue() != null && this.getValue().startsWith("[")) {
            return JSONUtil.parseArray(this.getValue());
        }
        return new JSONArray();
    }

}