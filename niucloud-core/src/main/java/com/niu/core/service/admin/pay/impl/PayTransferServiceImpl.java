package com.niu.core.service.admin.pay.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.pay.TransferSceneEnum;
import com.niu.core.service.admin.pay.IPayTransferService;
import com.niu.core.service.admin.pay.param.SetSceneIdParam;
import com.niu.core.service.core.pay.ICoreTransferSceneService;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import com.niu.core.service.core.pay.vo.WechatTransferSceneListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 转账实现
 */
@Service
public class PayTransferServiceImpl implements IPayTransferService {

    @Resource
    ICoreTransferSceneService coreTransferSceneService;

    public Map<String, WechatTransferSceneListVo> getWechatTransferScene() {
        return coreTransferSceneService.getWechatTransferScene(RequestUtils.siteId());
    }

    /**
     * 设置场景id
     *
     * @param param
     */
    @Override
    public void setSceneId(SetSceneIdParam param) {
        JSONObject config = coreTransferSceneService.getWechatTransferSceneConfig(RequestUtils.siteId());

        Map<String, TransferSceneEnum> tradeScenelist = TransferSceneEnum.getWechatTransferSceneMap();
        if (!tradeScenelist.containsKey(param.getScene())) {
            throw new CommonException("不存在的商户转账场景");
        }

        config.put(param.getScene(), param.getSceneId());
        coreTransferSceneService.setWechatTransferSceneConfig(RequestUtils.siteId(), config);
    }

    /**
     * 设置业务场景配置
     * @param param
     */
    @Override
    public void setTradeScene(SetTradeSceneParam param) {
        coreTransferSceneService.setTradeScene(RequestUtils.siteId(), param.getType(), param);
    }
}
