package com.niu.core.service.admin.diy_form.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.sys.vo.SceneDomainVo;
import lombok.Data;

import java.io.Serializable;

/**
 * 万能表单表参数
*/
@Data
public class DiyFormInitVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 表单id */
    private Integer formId;


    /** 表单名称（用于后台展示） */
    private String pageTitle;


    /** 表单名称（用于前台展示） */
    private String title;

    private Integer status;

    /** 分享内容 */
    private String share;


    /** 表单类型 */
    private String type;

    private String typeName;


    /** 表单数据，json格式，包含展示组件 */
    private String value;

    private JSONObject component;

    private SceneDomainVo domainUrl;


    public JSONObject getShare(){
        if(ObjectUtil.isNotEmpty(this.share)){
            return JSONUtil.parseObj(this.share);
        }else{
            return JSONUtil.parseObj("{\"wechat\": {\"title\": " + this.title+ ", \"desc\" : \"\", \"url\": \"\"}, \"weapp\": {\"title\": " + this.title + ", \"desc\" : \"\", \"url\": \"\"}}");
        }
    }

    public Object getValue(){
        if(this.value.equals(""))
        {
            return "";
        }else{
            return JSONUtil.parseObj(this.value);
        }
    }

}
