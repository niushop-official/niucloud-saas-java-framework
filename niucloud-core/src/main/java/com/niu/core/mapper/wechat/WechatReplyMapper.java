package com.niu.core.mapper.wechat;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.wechat.WechatReply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号消息回调 Mapper
 */
@Mapper
public interface WechatReplyMapper extends MPJBaseMapper<WechatReply> {
}
