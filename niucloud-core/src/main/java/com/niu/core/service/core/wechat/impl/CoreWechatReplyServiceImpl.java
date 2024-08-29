package com.niu.core.service.core.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.wechat.WechatReply;
import com.niu.core.enums.channel.WechatReplyTypeEnum;
import com.niu.core.mapper.wechat.WechatReplyMapper;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.wechat.ICoreWechatConfigService;
import com.niu.core.service.core.wechat.ICoreWechatReplyService;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreWechatReplyServiceImpl implements ICoreWechatReplyService {

    @Resource
    WechatReplyMapper wechatReplyMapper;

    /**
     * 查询默认回复
     * @param siteId
     * @return
     */
    public WechatReplyInfoVo getDefault(Integer siteId) {
        WechatReply model = wechatReplyMapper.selectOne(new QueryWrapper<WechatReply>()
                .eq("reply_type", WechatReplyTypeEnum.REPLY_DEFAULT.getType())
                .eq("site_id", siteId));

        if (!ObjectUtil.isNotNull(model)) return null;

        WechatReplyInfoVo vo = new WechatReplyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 查询关注回复
     * @param siteId
     * @return
     */
    public WechatReplyInfoVo getSubscribe(Integer siteId) {
        WechatReply model = wechatReplyMapper.selectOne(new QueryWrapper<WechatReply>()
                .eq("reply_type", WechatReplyTypeEnum.REPLY_SUBSCRIBE.getType())
                .eq("site_id", siteId));

        if (!ObjectUtil.isNotNull(model)) return null;

        WechatReplyInfoVo vo = new WechatReplyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

}
