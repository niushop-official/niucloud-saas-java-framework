package com.niu.core.service.core.notice;

import cn.hutool.json.JSONObject;
import com.niu.core.entity.sys.SysNotice;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 消息管理接口
 */
public interface ICoreNoticeService {

    /**
     * @param siteId
     * @param key
     * @return
     */
    SysNotice find(Integer siteId, String key);

    Map<String, NoticeInfoVo> getList(Integer siteId, String[] keys);

    List<AddonNoticeListVo> getAddonList(Integer siteId);

    NoticeInfoVo getInfo(Integer siteId, String key);

    void edit(Integer siteId, String key, JSONObject data);

    /**
     * 发送消息
     * @param siteId
     * @param key
     * @param data
     */
    void send(Integer siteId, String key, Map<String, Object> data);
}
