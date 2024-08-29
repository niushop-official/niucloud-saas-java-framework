package com.niu.core.service.admin.wechat;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.WechatDefaultReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplySearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.wechat.param.WechatSubscribeReplyParam;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;

/**
 * 公众号消息回调服务接口
 */
public interface IWechatReplyService {

    PageResult<WechatReplyListVo> getKeywordList(PageParam pageParam, WechatReplySearchParam searchParam);

    WechatReplyInfoVo getKeywordInfo(Integer id);

    void addKeyword(WechatReplyParam addParam);

    void editKeyword(Integer id, WechatReplyParam editParam);

    WechatReplyInfoVo getDefault();

    void editDefault(WechatDefaultReplyParam param);

    WechatReplyInfoVo getSubscribe();

    void editSubscribe(WechatSubscribeReplyParam param);

    void delKeyword(Integer id);
}
