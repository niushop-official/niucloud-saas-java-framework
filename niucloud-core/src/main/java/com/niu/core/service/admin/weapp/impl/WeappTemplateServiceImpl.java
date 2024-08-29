package com.niu.core.service.admin.weapp.impl;

import cn.hutool.json.JSONObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.admin.weapp.IWeappTemplateService;
import com.niu.core.service.admin.weapp.param.WeappTemplateSyncParam;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 微信小程序订阅消息
 */
@Service
public class WeappTemplateServiceImpl implements IWeappTemplateService {

    @Resource
    ICoreNoticeService coreNoticeService;

    public List<AddonNoticeListVo> list() {
        List<AddonNoticeListVo> addonNoticeList = coreNoticeService.getAddonList(RequestUtils.siteId());

        for (AddonNoticeListVo item : addonNoticeList) {
            List<NoticeInfoVo> filter = new LinkedList<>();
            for (NoticeInfoVo noticeItem: item.getNotice()) {
                if (noticeItem.getSupport_type().indexOf("weapp") != -1) {
                    filter.add(noticeItem);
                }
            }
            item.setNotice(filter);
        }
        return addonNoticeList;
    }

    /**
     * 同步小程序订阅消息
     * @param param
     */
    public void sync(WeappTemplateSyncParam param) {
        Map<String, NoticeInfoVo> list = coreNoticeService.getList(RequestUtils.siteId(), param.getKeys());

        for (NoticeInfoVo item : list.values()) {
            if (item.getWeapp() != null) {
                if (item.getWeappTemplateId().length() > 0) {
                    deleteTemplate(item.getWeappTemplateId());
                }
                String templateId = addTemplate(item.getWeapp().get("tid").toString(), item.getWeapp().getJSONArray("kid_list").toList(Integer.class), item.getWeapp().get("scene_desc").toString());
                coreNoticeService.edit(RequestUtils.siteId(), item.getKey(), new JSONObject().set("weapp_template_id", templateId));
            }
        }
    }

    /**
     * 删除小程序订阅消息
     * @param templateId
     */
    private void deleteTemplate(String templateId) {
        try {
            WechatUtils.miniapp(RequestUtils.siteId()).getSubscribeService().delTemplate(templateId);
        } catch (Exception e) {
        }
    }

    /**
     * 添加小程序订阅消息
     */
    private String addTemplate(String id, List<Integer> keywordIdList, String sceneDesc) {
        try {
            return WechatUtils.miniapp(RequestUtils.siteId()).getSubscribeService().addTemplate(id, keywordIdList, sceneDesc);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}
