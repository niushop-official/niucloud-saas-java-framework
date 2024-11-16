package com.niu.core.service.admin.wechat.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.wechat.WechatReply;
import com.niu.core.enums.channel.WechatReplyTypeEnum;
import com.niu.core.mapper.wechat.WechatReplyMapper;
import com.niu.core.service.admin.wechat.param.WechatDefaultReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplyParam;
import com.niu.core.service.admin.wechat.param.WechatReplySearchParam;
import com.niu.core.service.admin.wechat.IWechatReplyService;
import com.niu.core.service.admin.wechat.param.WechatSubscribeReplyParam;
import com.niu.core.service.admin.wechat.vo.WechatReplyInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatReplyListVo;
import com.niu.core.service.core.wechat.ICoreWechatReplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 公众号消息回调实现
 */
@Service
public class WechatReplyServiceImpl implements IWechatReplyService {
        
    @Resource
    WechatReplyMapper wechatReplyMapper;

    @Resource
    ICoreWechatReplyService coreWechatReplyService;

    /**
     * 公众号消息回调列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WechatReplyListVo>
     */
    @Override
    public PageResult<WechatReplyListVo> getKeywordList(PageParam pageParam, WechatReplySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<WechatReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reply_type", WechatReplyTypeEnum.REPLY_KEYWORD.getType());
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.like("keyword", searchParam.getKeyword());
        if (ObjectUtil.isNotEmpty(searchParam.getName())) queryWrapper.like("name", searchParam.getName());

        IPage<WechatReply> iPage = wechatReplyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<WechatReplyListVo> list = new LinkedList<>();
        for (WechatReply item : iPage.getRecords()) {
            WechatReplyListVo vo = new WechatReplyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 获取关键字回复信息
     * @param id
     * @return
     */
    public WechatReplyInfoVo getKeywordInfo(Integer id) {
        WechatReply model = wechatReplyMapper.selectOne(
                new QueryWrapper<WechatReply>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("reply_type", WechatReplyTypeEnum.REPLY_KEYWORD.getType()));

        Assert.notNull(model, "数据不存在");

        WechatReplyInfoVo vo = new WechatReplyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 公众号消息回调添加
     * @param addParam 参数
     */
    @Override
    public void addKeyword(WechatReplyParam addParam) {
        WechatReply model = new WechatReply();
        model.setName(addParam.getName());
        model.setSiteId(RequestUtils.siteId());
        model.setKeyword(addParam.getKeyword());
        model.setReplyType(WechatReplyTypeEnum.REPLY_KEYWORD.getType());
        model.setMatchingType(addParam.getMatchingType());
        model.setContent(addParam.getContent().toString());
        model.setSort(addParam.getSort());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setReplyMethod(addParam.getReplyMethod());
        wechatReplyMapper.insert(model);
    }

    /**
     * 更新关键词回复
     * @param editParam 参数
     */
    @Override
    public void editKeyword(Integer id, WechatReplyParam editParam) {
        WechatReply model = wechatReplyMapper.selectOne(
                new QueryWrapper<WechatReply>()
                        .eq("id",  id)
                        .eq("site_id", RequestUtils.siteId())
                        .eq("reply_type", WechatReplyTypeEnum.REPLY_KEYWORD.getType()));

        Assert.notNull(model, "数据不存在!");

        model.setName(editParam.getName());
        model.setKeyword(editParam.getKeyword());
        model.setMatchingType(editParam.getMatchingType());
        model.setContent(editParam.getContent().toString());
        model.setSort(editParam.getSort());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setReplyMethod(editParam.getReplyMethod());
        wechatReplyMapper.updateById(model);
    }

    /**
     * @Override
     * @return
     */
    public WechatReplyInfoVo getDefault() {
        return coreWechatReplyService.getDefault(RequestUtils.siteId());
    }

    /**
     * 修改默认回复
     * @param param
     */
    public void editDefault(WechatDefaultReplyParam param) {
        QueryWrapper queryWrapper =new QueryWrapper<WechatReply>()
                .eq("reply_type", WechatReplyTypeEnum.REPLY_DEFAULT.getType())
                .eq("site_id", RequestUtils.siteId());

        WechatReply reply = wechatReplyMapper.selectOne(queryWrapper);

        if (ObjectUtil.isEmpty(reply)) {
            WechatReply model = new WechatReply();
            model.setContent(param.getContent().toString());
            model.setSiteId(RequestUtils.siteId());
            model.setReplyType(WechatReplyTypeEnum.REPLY_DEFAULT.getType());
            wechatReplyMapper.insert(model);
        } else {
            WechatReply model = new WechatReply();
            model.setContent(param.getContent().toString());
            wechatReplyMapper.update(model, queryWrapper);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public WechatReplyInfoVo getSubscribe() {
        return coreWechatReplyService.getSubscribe(RequestUtils.siteId());
    }

    /**
     * 修改关注回复
     * @param param
     */
    public void editSubscribe(WechatSubscribeReplyParam param) {
        QueryWrapper queryWrapper = new QueryWrapper<WechatReply>()
                .eq("reply_type", WechatReplyTypeEnum.REPLY_SUBSCRIBE.getType())
                .eq("site_id", RequestUtils.siteId());

        WechatReply reply = wechatReplyMapper.selectOne(queryWrapper);

        if (ObjectUtil.isEmpty(reply)) {
            WechatReply model = new WechatReply();
            model.setContent(param.getContent().toString());
            model.setSiteId(RequestUtils.siteId());
            model.setReplyType(WechatReplyTypeEnum.REPLY_DEFAULT.getType());
            wechatReplyMapper.insert(model);
        } else {
            WechatReply model = new WechatReply();
            model.setContent(param.getContent().toString());
            wechatReplyMapper.update(model, queryWrapper);
        }
    }

    /**
     * 删除关键字回复
     * @param id
     */
    public void delKeyword(Integer id) {
        wechatReplyMapper.delete(new QueryWrapper<WechatReply>()
                .eq("reply_type", WechatReplyTypeEnum.REPLY_KEYWORD.getType())
                .eq("id", id)
                .eq("site_id", RequestUtils.siteId()));
    }
}
