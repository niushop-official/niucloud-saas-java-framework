package com.niu.core.service.admin.pay;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.pay.param.PayTransferParam;
import com.niu.core.service.admin.pay.param.PayTransferSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.pay.param.SetSceneIdParam;
import com.niu.core.service.admin.pay.vo.PayTransferInfoVo;
import com.niu.core.service.admin.pay.vo.PayTransferListVo;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import com.niu.core.service.core.pay.vo.WechatTransferSceneListVo;

import java.util.Map;

/**
 * 转账服务接口
 */
public interface IPayTransferService {

    Map<String, WechatTransferSceneListVo> getWechatTransferScene();

    /**
     * 设置场景id
     * @param param
     */
    void setSceneId(SetSceneIdParam param);

    /**
     * 设置业务场景配置
     * @param param
     */
    void setTradeScene(SetTradeSceneParam param);
}
