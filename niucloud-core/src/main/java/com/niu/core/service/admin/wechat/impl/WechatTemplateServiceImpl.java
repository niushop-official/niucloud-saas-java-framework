package com.niu.core.service.admin.wechat.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.wechat.WechatFans;
import com.niu.core.mapper.wechat.WechatFansMapper;
import com.niu.core.service.admin.wechat.IWechatTemplateService;
import com.niu.core.service.admin.wechat.param.WechatFansParam;
import com.niu.core.service.admin.wechat.param.WechatFansSearchParam;
import com.niu.core.service.admin.wechat.IWechatFansService;
import com.niu.core.service.admin.wechat.param.WechatTemplateSyncParam;
import com.niu.core.service.admin.wechat.vo.WechatFansInfoVo;
import com.niu.core.service.admin.wechat.vo.WechatFansListVo;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import com.niu.core.service.core.site.ICoreSiteService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 微信模板消息
 */
@Service
public class WechatTemplateServiceImpl implements IWechatTemplateService {

    @Resource
    ICoreNoticeService coreNoticeService;

    public List<AddonNoticeListVo> list() {
        List<AddonNoticeListVo> addonNoticeList = coreNoticeService.getAddonList(RequestUtils.siteId());

        for (AddonNoticeListVo item : addonNoticeList) {
            List<NoticeInfoVo> filter = new LinkedList<>();
            for (NoticeInfoVo noticeItem: item.getNotice()) {
                if (noticeItem.getSupport_type().indexOf("wechat") != -1) {
                    filter.add(noticeItem);
                }
            }
            item.setNotice(filter);
        }
        return addonNoticeList;
    }

    /**
     * 同步模板消息
     * @param param
     */
    public void sync(WechatTemplateSyncParam param) {
        Map<String, NoticeInfoVo> list = coreNoticeService.getList(RequestUtils.siteId(), param.getKeys());

        for (NoticeInfoVo item : list.values()) {
            if (item.getWechat() != null) {
                if (item.getWeappTemplateId().length() > 0) {
                    deleteTemplate(item.getWeappTemplateId());
                }
                String templateId = addTemplate(item.getWechat().get("temp_key").toString(), item.getWechat().getJSONArray("keyword_name_list").toList(String.class));
                coreNoticeService.edit(RequestUtils.siteId(), item.getKey(), new JSONObject().set("wechat_template_id", templateId));
            }
        }
    }

    /**
     * 删除模板
     * @param templateId
     */
    private void deleteTemplate(String templateId) {
        try {
            WechatUtils.mp(RequestUtils.siteId()).getTemplateMsgService().delPrivateTemplate(templateId);
        } catch (Exception e) {
        }
    }

    /**
     * 添加模板
     * @param shortTemplateId
     * @param keywordNameList
     */
    private String addTemplate(String shortTemplateId, List<String> keywordNameList) {
        try {
            return WechatUtils.mp(RequestUtils.siteId()).getTemplateMsgService().addTemplate(shortTemplateId, keywordNameList);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
