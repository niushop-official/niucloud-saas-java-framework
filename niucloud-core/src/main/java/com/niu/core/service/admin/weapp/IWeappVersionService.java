package com.niu.core.service.admin.weapp;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.weapp.param.WeappTemplateSyncParam;
import com.niu.core.service.admin.weapp.param.WeappVersionAddParam;
import com.niu.core.service.admin.weapp.vo.WeappVersionListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;

import java.util.List;

/**
 * 小程序版本管理接口
 */
public interface IWeappVersionService {

    PageResult<WeappVersionListVo> list(PageParam pageParam);

    void add(WeappVersionAddParam param);

    JSONObject getWeappCompileLog(String key);

    String getWeappPreviewImage();
}
