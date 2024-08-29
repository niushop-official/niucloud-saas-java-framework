package com.niu.core.service.admin.notice;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.enums.notice.vo.NoticeEnumListVo;
import com.niu.core.service.admin.pay.param.PayParam;
import com.niu.core.service.admin.pay.param.PaySearchParam;
import com.niu.core.service.admin.pay.vo.PayInfoVo;
import com.niu.core.service.admin.pay.vo.PayListVo;
import com.niu.core.service.core.notice.vo.AddonNoticeListVo;
import com.niu.core.service.core.notice.vo.NoticeInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 消息管理服务接口
 */
public interface INoticeService {


    Map<String, NoticeInfoVo> getList(String[] keys);

    List<AddonNoticeListVo> getAddonList();

    NoticeInfoVo getInfo(String key);

    void edit(String key, JSONObject data);

}
