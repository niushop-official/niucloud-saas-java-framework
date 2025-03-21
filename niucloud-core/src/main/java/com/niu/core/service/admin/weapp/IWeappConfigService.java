package com.niu.core.service.admin.weapp;

import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.weapp.param.SetDomainParam;
import com.niu.core.service.admin.weapp.vo.WeappStaticInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.open.bean.ma.privacy.GetPrivacySettingResult;

/**
 * 微信配置
 */
public interface IWeappConfigService {

    WeappConfigVo getWeappConfig();

    void setWeappConfig(WeappConfigParam weappConfigParam);

    void setDomain(SetDomainParam param);

    /**
     *  设置隐私设置
     * @param privacySetting
     */
    void setPrivacySetting(JSONObject privacySetting);

    /**
     * 获取隐私设置
     * @return
     */
    GetPrivacySettingResult getPrivacySetting();
}
