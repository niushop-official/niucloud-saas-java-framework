package com.niu.core.service.core.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import com.niu.core.service.core.pay.vo.TransferSceneInfo;
import com.niu.core.service.core.pay.vo.WechatTransferSceneListVo;

import java.util.List;
import java.util.Map;

public interface ICoreTransferSceneService {

    /**
     * 获取微信转账场景配置
     * @param siteId
     */
    JSONObject getWechatTransferSceneConfig(Integer siteId);

    /**
     * 设置微信转账场景配置
     * @param siteId
     */
    void setWechatTransferSceneConfig(Integer siteId, JSONObject data);

    /**
     * 获取转账场景
     * @param siteId
     */
    Map<String, WechatTransferSceneListVo> getWechatTransferScene(Integer siteId);

    /**
     * 通过业务设置转账场景的配置信息
     * @param siteId
     */
    void setTradeScene(Integer siteId, String type, SetTradeSceneParam param);

    /**
     * 通过业务获取转账场景的配置信息
     */
    TransferSceneInfo getSceneInfoByType(Integer siteId, String type);
}
