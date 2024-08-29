package com.niu.core.service.admin.notice.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.service.admin.notice.INoticeService;
import com.niu.core.service.admin.pay.IPayService;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 消息管理实现
 */
@Service
public class NoticeServiceImpl implements INoticeService {

    @Resource
    ICoreNoticeService coreNoticeService;

    @Override
    public Map<String, NoticeInfoVo> getList(String[] keys) {
        return coreNoticeService.getList(RequestUtils.siteId(), keys);
    }

    @Override
    public List<AddonNoticeListVo> getAddonList() {
        return coreNoticeService.getAddonList(RequestUtils.siteId());
    }

    @Override
    public NoticeInfoVo getInfo(String key) {
//        return coreNoticeService.getInfo(RequestUtils.siteId(), key);
        return null;
    }

    @Override
    public void edit(String key, JSONObject data) {
        coreNoticeService.edit(RequestUtils.siteId(), key, data);
    }
}
