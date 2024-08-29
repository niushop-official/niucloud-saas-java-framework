package com.niu.core.service.admin.notice.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.sys.SmsTypeEnum;
import com.niu.core.service.admin.notice.ISmsService;
import com.niu.core.service.admin.notice.param.SmsTypeParam;
import com.niu.core.service.admin.notice.vo.SmsTypeParamVo;
import com.niu.core.service.admin.notice.vo.SmsTypeVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsService implements ISmsService {

    @Resource
    ICoreConfigService coreConfigService;

    @Override
    public List<SmsTypeVo> getList() {

        JSONObject smsTypeConfig = coreConfigService.getConfigValue(RequestUtils.siteId(), "SMS");
        JSONObject smsTypeList = SmsTypeEnum.getType();
        List<SmsTypeVo> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : smsTypeList.entrySet()) {
            SmsTypeVo vo = new SmsTypeVo();
            JSONObject smsType = (JSONObject) entry.getValue();
            vo.setSmsType(entry.getKey());
            vo.setName(smsType.getStr("name"));
            vo.setComponent(smsType.getStr("component"));
            Map<String, SmsTypeParamVo> map = new HashMap<>();
            if(ObjectUtil.isEmpty(smsTypeConfig))
            {
                vo.setIsUse(0);
            } else {
               boolean isUse = ((String)smsTypeConfig.get("default")).equals(entry.getKey());
               vo.setIsUse(isUse ? 1 : 0);
            }
            JSONObject params = (JSONObject) smsType.get("params");

            for (Map.Entry<String, Object> paramEntry : params.entrySet()) {

                SmsTypeParamVo paramVo = new SmsTypeParamVo();
                paramVo.setName(paramEntry.getValue().toString());
                if(ObjectUtil.isEmpty(smsTypeConfig))
                {
                    paramVo.setValue("");

                }else{
                   JSONObject smsTypeParam = (JSONObject) smsTypeConfig.get(entry.getKey());

                   if(!ObjectUtil.isEmpty(smsTypeParam)){
                       paramVo.setValue(smsTypeParam.getStr(paramEntry.getKey()));
                   }else{
                        paramVo.setValue("");
                   }
                }

                map.put(paramEntry.getKey(), paramVo);
            }
            vo.setParams(map);
            list.add(vo);
        }
        return list;
    }

    @Override
    public SmsTypeVo getConfig(String smsType) {
        List<SmsTypeVo> list = getList();
        for (SmsTypeVo vo : list) {
            if(vo.getSmsType().equals(smsType))
            {
                return vo;
            }
        }
        return null;
    }

    /**
     * 设置短信类型配置
     *
     * @param smsType
     * @param data
     */
    @Override
    public void setConfig(String smsType, JSONObject data) {
        if (!SmsTypeEnum.getType().containsKey(smsType)) throw new CommonException("短信类型不存在");
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.siteId(), "SMS");

        if (config.isEmpty()) {
            config.set("default", "");
        }
        if (data.getInt("is_use", 0).equals(1)) {
            config.set("default", smsType);
        } else {
            config.set("default", config.getStr("default") == smsType ? "" : config.getStr("default"));
        }

        JSONObject smsTypeEnum = SmsTypeEnum.getType().getJSONObject(smsType);
        for (String key: smsTypeEnum.getJSONObject("params").keySet()) {
            config.putByPath(smsType + "." + key, data.getStr(key, ""));
        }

        coreConfigService.setConfig(RequestUtils.siteId(), "SMS", config);
    }
}
