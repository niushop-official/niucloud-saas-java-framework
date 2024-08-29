package com.niu.core.service.core.wechat;

import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;

/**
 * 微信配置core服务层
 */
public interface ICoreWechatReplyService {

    WechatReplyInfoVo getDefault(Integer siteId);

    WechatReplyInfoVo getSubscribe(Integer siteId);
}
