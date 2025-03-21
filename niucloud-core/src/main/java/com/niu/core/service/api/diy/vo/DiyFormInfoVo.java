package com.niu.core.service.api.diy.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.diy_form.DiyFormTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* 万能表单表详情
*/
@Data
public class DiyFormInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 表单id */
    private Integer formId;


    /** 站点id */
    private Integer siteId;


    /** 表单名称（用于后台展示） */
    private String pageTitle;


    /** 表单名称（用于前台展示） */
    private String title;


    /** 表单类型 */
    private String type;

    private String typeName;


    /** 状态（0，关闭，1：开启） */
    private Integer status;


    /** 模板名称 */
    private String template;


    /** 表单数据，json格式，包含展示组件 */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String value;


    /** 所属插件标识 */
    private String addon;


    /** 分享内容 */
    private String share;


    /** 表单填写总数量 */
    private Integer writeNum;


    /** 备注说明 */
    private String remark;


    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;


    /** 更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    private List<Map<String, String>> error;

    public String getTypeName() {
        DiyFormTypeEnum diyFormTypeEnum = new DiyFormTypeEnum();
        JSONObject type =  diyFormTypeEnum.getType(this.type);
        if(ObjectUtil.isNotEmpty(type)&&type.containsKey("title"))
        {
            return type.getStr("title");
        }else {
            return "";
        }
    }

    public JSONObject getShare(){
        if (this.formId == null) return null;

        if(ObjectUtil.isNotEmpty(this.share)){
            return JSONUtil.parseObj(this.share);
        } else {
            return JSONUtil.parseObj("{\"wechat\": {\"title\": " + this.title+ ", \"desc\" : \"\", \"url\": \"\"}, \"weapp\": {\"title\": " + this.title + ", \"desc\" : \"\", \"url\": \"\"}}");
        }
    }

}