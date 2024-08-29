package com.niu.core.service.core.sys.vo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoreSysConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer Id; // 主键
    private Integer siteId; // 站点id
    private String configKey; // 配置项关键字
    private String value; // 配置项json
    private JSON valueJson; //配置json对象
    private Integer status; // 是否启用 1启用 0不启用
    private Long createTime; // 添加时间
    private Long updateTime; // 最后修改时间
    private String addon; //所属插件

    /**
     * 默认返回JSON对象
     *
     * @return
     */
    public JSONObject getValueJson() {
        return getValueJsonObject();
    }

    /**
     * 返回JSON对象
     *
     * @return
     */
    public JSONObject getValueJsonObject() {
        if (valueJson != null && valueJson instanceof JSONObject) {
            return (JSONObject) valueJson;
        }
        return new JSONObject();
    }

    /**
     * 返回JSON数组
     *
     * @return
     */
    public JSONArray getValueJsonArray() {
        if (valueJson != null && valueJson instanceof JSONArray) {
            return (JSONArray) valueJson;
        }
        return new JSONArray();
    }

}