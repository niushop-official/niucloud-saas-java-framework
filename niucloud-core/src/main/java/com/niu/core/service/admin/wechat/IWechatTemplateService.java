package com.niu.core.service.admin.wechat;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.wechat.param.*;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;

import java.util.List;

/**
 * 公众号消息回调服务接口
 */
public interface IWechatTemplateService {

    List<AddonNoticeListVo> list();

    void sync(WechatTemplateSyncParam param);
}
