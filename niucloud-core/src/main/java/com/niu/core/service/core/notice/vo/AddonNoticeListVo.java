package com.niu.core.service.core.notice.vo;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件消息列表
 */
@Data
public class AddonNoticeListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key = "";

    private String title = "";

    private List<NoticeInfoVo> notice = new ArrayList<>();

}
