package com.niu.core.service.admin.wechat.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.enums.channel.WechatEncryptionTypeEnum;
import com.niu.core.service.admin.sys.ISysConfigService;
import com.niu.core.service.admin.wechat.IWechatConfigService;
import com.niu.core.service.admin.wechat.IWechatMenuService;
import com.niu.core.service.admin.wechat.vo.WechatStaticInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 公众号消息回调实现
 */
@Service
public class WechatMenuServiceImpl implements IWechatMenuService {

    @Resource
    ICoreConfigService coreConfigService;

    /**
     * 获取菜单配置
     */
    @Override
    public JSONArray info() {
        return coreConfigService.getConfigArrayValue(RequestUtils.siteId(), "WECHAT_MENU");
    }

    /**
     * 设置菜单配置
     */
    @Override
    public void edit(JSONArray data) {
        try{
            Map<String , JSONArray> params = new HashMap<>();
            params.put("button", data);
            WechatUtils.mp(RequestUtils.siteId()).getMenuService().menuCreate(JSONUtil.parseObj(params).toString()) ;
            coreConfigService.setConfig(RequestUtils.siteId(), "WECHAT_MENU", data);
        }catch (WxErrorException e){
            throw new AdminException(e.getMessage());
        }

    }

}