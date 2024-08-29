package com.niu.core.service.admin.notice;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.notice.param.SmsTypeParam;
import com.niu.core.service.admin.notice.vo.SmsTypeVo;

import java.util.List;

public interface ISmsService {

    /**
     * 获取短信类型列表
     * @return
     */
    List<SmsTypeVo> getList();

    /**
     * 获取短信类型配置
     * @param smsType
     * @return
     */
    SmsTypeVo getConfig(String smsType);

    /**
     *  设置短信类型配置
     * @param smsType
     * @param data
     */
    void setConfig(String smsType, JSONObject data);


}
