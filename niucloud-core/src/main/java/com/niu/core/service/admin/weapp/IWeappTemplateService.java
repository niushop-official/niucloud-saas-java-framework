package com.niu.core.service.admin.weapp;

import com.niu.core.service.admin.weapp.param.WeappTemplateSyncParam;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;

import java.util.List;

/**
 * 小程序订阅消息接口
 */
public interface IWeappTemplateService {

    List<AddonNoticeListVo> list();

    void sync(WeappTemplateSyncParam param);
}
